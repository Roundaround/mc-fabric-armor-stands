package me.roundaround.armorstands.client.gui.widget;

import org.lwjgl.glfw.GLFW;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class AdjustPoseSliderWidget extends SliderWidget {
  private final ArmorStandEntity armorStand;

  private PosePart part;
  private EulerAngleParameter parameter;

  public AdjustPoseSliderWidget(
      int x,
      int y,
      int width,
      int height,
      PosePart part,
      EulerAngleParameter parameter,
      ArmorStandEntity armorStand) {
    super(x, y, width, height, Text.empty(), 0);

    this.part = part;
    this.parameter = parameter;
    this.armorStand = armorStand;

    readAngle();
  }

  public void setPart(PosePart part) {
    this.part = part;
    readAngle();
  }

  public void setParameter(EulerAngleParameter parameter) {
    this.parameter = parameter;
    readAngle();
  }

  @Override
  protected void updateMessage() {
    setMessage(Text.translatable(
        "armorstands.adjustPose.label",
        String.format("%.2f", getAngle())));
  }

  @Override
  protected void applyValue() {
    this.part.set(this.armorStand, this.parameter, getAngle());
  }

  @Override
  public void onRelease(double mouseX, double mouseY) {
    super.onRelease(mouseX, mouseY);

    persistValue();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    float previousValue = getAngle();

    boolean handled = false;
    if (keyCode == GLFW.GLFW_KEY_LEFT) {
      this.setAngle(previousValue - 5);
      handled = true;
    } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
      this.setAngle(previousValue + 5);
      handled = true;
    }

    if (!handled) {
      handled = super.keyPressed(keyCode, scanCode, modifiers);
    }

    if (Math.abs(getAngle() - previousValue) >= 0.01f) {
      persistValue();
    }

    return handled;
  }

  @Override
  public void playDownSound(SoundManager soundManager) {
    soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
  }

  private void readAngle() {
    setAngle(this.parameter.get(this.part.get(this.armorStand)));
  }

  private float getAngle() {
    return valueToAngle(this.value);
  }

  private void setAngle(float value) {
    this.value = angleToValue(value);
    updateMessage();
  }

  private static double angleToValue(float value) {
    return MathHelper.clamp((value + 180f) / 360f, 0f, 1f);
  }

  private static float valueToAngle(double value) {
    return (float) (value * 360f - 180f);
  }

  private void persistValue() {
    ClientNetworking.sendAdjustPosePacket(this.part, this.parameter, getAngle());
  }
}
