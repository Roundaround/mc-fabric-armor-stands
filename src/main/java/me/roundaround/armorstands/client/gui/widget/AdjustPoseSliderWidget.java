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
  private int min = -180;
  private int max = 180;

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

  public void setRange(int min, int max) {
    this.min = min;
    this.max = max;
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
    double up = Math.ceil(getAngle());
    if (up - getAngle() < MathHelper.EPSILON) {
      up += 1;
    }
    setAngle((float) up);
    persistValue();
  }

  public void decrement() {
    double down = Math.floor(getAngle());
    if (getAngle() - down < MathHelper.EPSILON) {
      down -= 1;
    }
    setAngle((float) down);
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
    return valueToAngle(this.value, this.min, this.max);
  }

  private void setAngle(float value) {
    this.value = angleToValue(MathHelper.clamp(value, this.min, this.max), this.min, this.max);
    updateMessage();
  }

  private static double angleToValue(float value, int min, int max) {
    // Map angle (min-max) to value (0-1)
    return (value - min) / (max - min);
  }

  private static float valueToAngle(double value, int min, int max) {
    // Map value (0-1) to angle (min-max)
    return (float) (value * (max - min) + min);
  }

  private void persistValue() {
    AdjustPosePacket.sendToServer(this.part, this.parameter, getAngle());
  }
}
