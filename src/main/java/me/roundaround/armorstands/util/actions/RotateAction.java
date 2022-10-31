package me.roundaround.armorstands.util.actions;

import java.util.Optional;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class RotateAction implements ArmorStandAction {
  private final float argument;
  private final boolean absolute;
  private final boolean round;
  private Optional<Float> originalRotation = Optional.empty();

  private RotateAction(float argument, boolean absolute, boolean round) {
    this.argument = argument;
    this.absolute = absolute;
    this.round = round;
  }

  public static RotateAction absolute(float rotation) {
    return absolute(rotation, false);
  }

  public static RotateAction absolute(float rotation, boolean round) {
    return new RotateAction(rotation, true, round);
  }

  public static RotateAction relative(float rotation) {
    return relative(rotation, true);
  }

  public static RotateAction relative(float rotation, boolean round) {
    return new RotateAction(rotation, false, round);
  }

  @Override
  public Text getName(ArmorStandEntity armorStand) {
    return Text.translatable("armorstands.action.rotate");
  }

  @Override
  public void apply(ArmorStandEntity armorStand) {
    originalRotation = Optional.of(armorStand.getYaw());

    float rotation = argument;

    if (!absolute) {
      rotation += originalRotation.get();
    }

    setRotation(armorStand, rotation, round);
  }

  @Override
  public void undo(ArmorStandEntity armorStand) {
    if (originalRotation.isEmpty()) {
      return;
    }
    setRotation(armorStand, originalRotation.get());
  }

  public static void setRotation(ArmorStandEntity armorStand, float rotation) {
    setRotation(armorStand, rotation, false);
  }

  public static void setRotation(ArmorStandEntity armorStand, float rotation, boolean round) {
    float target = rotation % 360f;
    if (round) {
      target = Math.round(target);
    }

    armorStand.setYaw(target);
  }
}
