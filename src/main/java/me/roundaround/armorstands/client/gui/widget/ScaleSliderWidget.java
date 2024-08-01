package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.util.actions.ScaleAction;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public class ScaleSliderWidget extends SliderWidget {
  private static final float MIN = 0.01f;
  private static final float MAX = 10f;
  private static final float SPLIT_SCALE = 2f;
  private static final double SPLIT_VALUE = 0.5;

  private final AbstractArmorStandScreen parent;
  private final ArmorStandEntity armorStand;

  private Optional<Float> lastScale = Optional.empty();
  private Optional<Long> lastScroll = Optional.empty();
  private boolean isDragging = false;
  private boolean pendingDragPing = false;

  public ScaleSliderWidget(
      AbstractArmorStandScreen parent, int width, int height, ArmorStandEntity armorStand
  ) {
    super(0, 0, width, height, Text.empty(), 0);

    this.parent = parent;
    this.armorStand = armorStand;

    refresh();
  }

  public boolean isPending(Screen parent) {
    return isDragging() || this.lastScroll.isPresent();
  }

  public void refresh() {
    float armorStandScale = this.armorStand.getScale();
    if (this.lastScale.isPresent() && Math.abs(armorStandScale - this.lastScale.get()) < MathHelper.EPSILON) {
      return;
    }

    this.lastScale = Optional.of(armorStandScale);
    this.setScale(armorStandScale);
  }

  public void setToOne() {
    this.setScale(1);
    this.persistValue();
  }

  public void increment() {
    float nextTick = nextTickUp(this.getScale());
    if (nextTick - this.getScale() < MathHelper.EPSILON) {
      nextTick += stepAmount(nextTick, true);
    }
    this.setScale(nextTick);
    this.persistValue();
  }

  public void decrement() {
    float nextTick = nextTickDown(this.getScale());
    if (this.getScale() - nextTick < MathHelper.EPSILON) {
      nextTick -= stepAmount(nextTick, false);
    }
    this.setScale(nextTick);
    this.persistValue();
  }

  public void tick() {
    this.lastScroll.ifPresent(lastScroll -> {
      if (System.currentTimeMillis() - lastScroll > 500) {
        this.lastScroll = Optional.empty();
        this.persistValue();
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
    this.setMessage(Text.of(String.format("%.2f", this.getScale())));
  }

  @Override
  protected void applyValue() {
    ScaleAction.setScale(this.armorStand, this.getScale());
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

    this.persistValue();
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount
  ) {
    if (this.isMouseOver(mouseX, mouseY)) {
      if (verticalAmount > 0) {
        this.increment();
      } else {
        this.decrement();
      }
      this.applyValue();
      this.lastScroll = Optional.of(System.currentTimeMillis());
      return true;
    }

    return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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

  private float getScale() {
    return valueToScale(this.value);
  }

  public void setScale(float scale) {
    this.setValue(scaleToValue(scale));
  }

  private void setValue(double value) {
    this.value = MathHelper.clamp(value, 0, 1);
    updateMessage();
  }

  private static float nextTickUp(float from) {
    float stepAmount = stepAmount(from, true);
    return (float) (Math.ceil(from / stepAmount) * stepAmount);
  }

  private static float nextTickDown(float from) {
    float stepAmount = stepAmount(from, false);
    return (float) (Math.floor(from / stepAmount) * stepAmount);
  }

  private static float stepAmount(float from, boolean up) {
    return from < SPLIT_SCALE || (!up && Math.abs(from - SPLIT_SCALE) < MathHelper.EPSILON) ? 0.1f : 0.5f;
  }

  private static double scaleToValue(float scale) {
    if (scale < SPLIT_SCALE) {
      // Map [MIN, SPLIT_SCALE] to [0, SPLIT_VALUE]
      return (scale - MIN) / (SPLIT_SCALE - MIN) * SPLIT_VALUE;
    }
    // Map [SPLIT_SCALE, MAX] to [SPLIT_VALUE, 1]
    return SPLIT_VALUE + (scale - SPLIT_SCALE) / (MAX - SPLIT_SCALE) * (1 - SPLIT_VALUE);
  }

  private static float valueToScale(double value) {
    if (value < SPLIT_VALUE) {
      // Map [0, SPLIT_VALUE] to [MIN, SPLIT_SCALE]
      return (float) (MIN + (value / SPLIT_VALUE) * (SPLIT_SCALE - MIN));
    }
    // Map [SPLIT_VALUE, 1] to [SPLIT_SCALE, MAX]
    return (float) (SPLIT_SCALE + ((value - SPLIT_VALUE) / (1 - SPLIT_VALUE)) * (MAX - SPLIT_SCALE));
  }

  private void persistValue() {
    // Cancel any pending scroll updates
    this.lastScroll = Optional.empty();

    ClientNetworking.sendSetScalePacket(getScale());
  }
}
