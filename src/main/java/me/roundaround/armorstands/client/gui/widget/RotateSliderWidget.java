package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.network.ClientNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class RotateSliderWidget extends SliderWidget {
  private final AbstractArmorStandScreen parent;
  private final ArmorStandEntity armorStand;

  private Optional<Float> lastAngle = Optional.empty();
  private int min = -180;
  private int max = 180;
  private Optional<Long> lastScroll = Optional.empty();
  private boolean isDragging = false;
  private boolean pendingDragPing = false;

  public RotateSliderWidget(
      AbstractArmorStandScreen parent, int x, int y, int width, int height, ArmorStandEntity armorStand
  ) {
    super(x, y, width, height, Text.empty(), 0);

    this.parent = parent;
    this.armorStand = armorStand;

    refresh();
  }

  public boolean isPending(Screen parent) {
    return isDragging() || this.lastScroll.isPresent();
  }

  public void setRange(int min, int max) {
    this.min = min;
    this.max = max;
    refresh();
  }

  public void refresh() {
    float armorStandAngle = this.armorStand.getYaw();
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

  public boolean isDragging() {
    return this.isDragging || this.pendingDragPing;
  }

  public void onPong() {
    this.pendingDragPing = false;
  }

  @Override
  protected void updateMessage() {
    setMessage(Text.translatable("armorstands.rotate.label", String.format("%.2f", getAngle())));
  }

  @Override
  protected void applyValue() {
    this.armorStand.setYaw(getAngle());
  }

  @Override
  public void onClick(double mouseX, double mouseY) {
    super.onClick(mouseX, mouseY);

    this.isDragging = true;
  }

  @Override
  public void onRelease(double mouseX, double mouseY) {
    super.onRelease(mouseX, mouseY);

    this.isDragging = false;
    this.pendingDragPing = true;
    this.parent.sendPing();

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
  protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
    this.isDragging = true;
    super.onDrag(mouseX, mouseY, deltaX, deltaY);
  }

  @Override
  public void playDownSound(SoundManager soundManager) {
    soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
  }

  private float getAngle() {
    return valueToAngle(this.value, this.min, this.max);
  }

  public void setAngle(float value) {
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

    ClientNetworking.sendSetYawPacket(getAngle());
  }
}
