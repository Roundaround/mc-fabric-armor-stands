package me.roundaround.armorstands.client.gui.widget;

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

import java.util.Optional;

public class AdjustPoseSliderWidget extends SliderWidget {
  private final ArmorStandEntity armorStand;

  private PosePart part;
  private EulerAngleParameter parameter;
  private Optional<Float> lastAngle = Optional.empty();
  private int min = -180;
  private int max = 180;
  private Optional<Long> lastScroll = Optional.empty();

  public AdjustPoseSliderWidget(
      int width, int height, PosePart part, EulerAngleParameter parameter, ArmorStandEntity armorStand
  ) {
    super(0, 0, width, height, Text.empty(), 0);

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

  public void tick() {
    this.lastScroll.ifPresent(lastScroll -> {
      if (System.currentTimeMillis() - lastScroll > 500) {
        this.lastScroll = Optional.empty();
        persistValue();
      }
    });
  }

  @Override
  protected void updateMessage() {
    setMessage(Text.translatable("armorstands.angle", String.format("%.2f", getAngle())));
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
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount
  ) {
    if (isMouseOver(mouseX, mouseY)) {
      setAngle(getAngle() + (float) verticalAmount);
      applyValue();
      this.lastScroll = Optional.of(System.currentTimeMillis());
      return true;
    }

    return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
  }

  @Override
  public void playDownSound(SoundManager soundManager) {
    soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
  }

  private float getAngle() {
    return valueToAngle(this.value, this.min, this.max);
  }

  private void setAngle(float value) {
    setValue(angleToValue(value, this.min, this.max));
  }

  private void setValue(double value) {
    this.value = MathHelper.clamp(value, 0, 1);
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
    // Cancel any pending scroll updates
    this.lastScroll = Optional.empty();

    ClientNetworking.sendAdjustPosePacket(this.part, this.parameter, getAngle());
  }
}
