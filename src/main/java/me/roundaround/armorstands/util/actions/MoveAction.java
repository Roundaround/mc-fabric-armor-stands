package me.roundaround.armorstands.util.actions;

import java.util.Optional;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Vec3d;

public class MoveAction implements ArmorStandAction {
  private final Vec3d argument;
  private final boolean absolute;
  private final boolean roundToPixel;
  private Optional<Vec3d> originalPosition = Optional.empty();

  private MoveAction(Vec3d argument, boolean absolute, boolean roundToPixel) {
    this.argument = argument;
    this.absolute = absolute;
    this.roundToPixel = roundToPixel;
  }

  public static MoveAction absolute(Vec3d position) {
    return new MoveAction(position, true, false);
  }

  public static MoveAction absolute(double x, double y, double z) {
    return absolute(new Vec3d(x, y, z));
  }

  public static MoveAction relative(Vec3d position, boolean roundToPixel) {
    return new MoveAction(position, false, roundToPixel);
  }

  public static MoveAction relative(double x, double y, double z, boolean roundToPixel) {
    return relative(new Vec3d(x, y, z), roundToPixel);
  }

  public static MoveAction relative(Vec3d position) {
    return relative(position, false);
  }

  public static MoveAction relative(double x, double y, double z) {
    return relative(x, y, z, false);
  }

  @Override
  public void apply(ArmorStandEntity armorStand) {
    originalPosition = Optional.of(armorStand.getPos());

    Vec3d position = argument;

    if (!absolute) {
      position = position.add(originalPosition.get());
    }

    if (roundToPixel) {
      double x = Math.round(position.x * 16) / 16.0;
      double y = Math.round(position.y * 16) / 16.0;
      double z = Math.round(position.z * 16) / 16.0;
      position = new Vec3d(x, y, z);
    }

    setPosition(armorStand, position);
  }

  @Override
  public void undo(ArmorStandEntity armorStand) {
    if (originalPosition.isEmpty()) {
      return;
    }

    setPosition(armorStand, originalPosition.get());
  }

  public static void setPosition(ArmorStandEntity armorStand, Vec3d position) {
    setPosition(armorStand, position.x, position.y, position.z);
  }

  public static void setPosition(ArmorStandEntity armorStand, double x, double y, double z) {
    armorStand.updateTrackedPosition(x, y, z);
    armorStand.setPosition(new Vec3d(x, y, z));
  }
}
