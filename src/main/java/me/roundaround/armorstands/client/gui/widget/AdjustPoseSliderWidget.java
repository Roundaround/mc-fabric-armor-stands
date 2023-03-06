package me.roundaround.armorstands.client.gui.widget;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
import me.roundaround.armorstands.network.packet.c2s.AdjustPosePacket;
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
  private Optional<Float> lastAngle = Optional.empty();

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

    refresh();
  }

  public void setPart(PosePart part) {
    this.part = part;
    refresh();
  }

  public void setParameter(EulerAngleParameter parameter) {
    this.parameter = parameter;
    refresh();
  }

  public void refresh() {
    float armorStandAngle = this.parameter.get(this.part.get(this.armorStand));
    if (this.lastAngle.isPresent() && Math.abs(armorStandAngle - this.lastAngle.get()) < MathHelper.EPSILON) {
      return;
    }

    this.lastAngle = Optional.of(armorStandAngle);
    setAngle(armorStandAngle);
  }

  public void zero() {
    setAngle(0);
    persistValue();
  }

  public void increment() {
    setAngle(Math.round(getAngle() / 1) * 1 + 1);
    persistValue();
  }

  public void decrement() {
    setAngle(Math.round(getAngle() / 1) * 1 - 1);
    persistValue();
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
    if (keyCode == GLFW.GLFW_KEY_LEFT) {
      decrement();
      return true;
    } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
      increment();
      return true;
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public void playDownSound(SoundManager soundManager) {
    soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
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
    AdjustPosePacket.sendToServer(this.part, this.parameter, getAngle());
  }
}
