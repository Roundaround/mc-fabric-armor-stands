package me.roundaround.armorstands.util;

import java.util.Optional;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArmorStandHelper {
  public static Vec3d getCornerPos(ArmorStandEntity armorStand) {
    Vec3d position = armorStand.getPos();
    return new Vec3d(Math.floor(position.x), position.y, Math.floor(position.z));
  }

  public static Vec3d getCenterPos(ArmorStandEntity armorStand) {
    Vec3d position = armorStand.getPos();
    return new Vec3d(Math.round(position.x + 0.5) - 0.5, position.y, Math.round(position.z + 0.5) - 0.5);
  }

  public static Optional<Vec3d> getStandingPos(ArmorStandEntity armorStand) {
    return getGroundPos(armorStand, false);
  }

  public static Optional<Vec3d> getStandingPos(ArmorStandEntity armorStand, boolean hasBasePlate) {
    return getGroundPos(armorStand, false, hasBasePlate);
  }

  public static Optional<Vec3d> getGroundPos(ArmorStandEntity armorStand, boolean sitting) {
    return getGroundPos(armorStand, sitting, !armorStand.shouldHideBasePlate());
  }

  public static Optional<Vec3d> getGroundPos(ArmorStandEntity armorStand, boolean sitting, boolean hasBasePlate) {
    Vec3d position = armorStand.getPos();

    World world = armorStand.getWorld();
    BlockPos blockPos = armorStand.getBlockPos().up(2);
    boolean failed = false;

    while (!world.isTopSolid(blockPos.down(), armorStand)) {
      blockPos = blockPos.down();

      if (world.isOutOfHeightLimit(blockPos)) {
        failed = true;
        break;
      }
    }

    if (failed) {
      return Optional.empty();
    }

    Vec3d newPosition = new Vec3d(position.x, blockPos.getY(), position.z);

    if (sitting) {
      newPosition = newPosition.subtract(0, 11 * 0.0625, 0);
    }

    if (!hasBasePlate && !sitting) {
      newPosition = newPosition.subtract(0, 0.0625, 0);
    }

    return Optional.of(newPosition);
  }

  public static float getLookYaw(ArmorStandEntity armorStand, Vec3d point) {
    Vec3d pos = armorStand.getPos();
    double dX = point.x - pos.x;
    double dZ = point.z - pos.z;
    return MathHelper.wrapDegrees((float) Math.toDegrees(MathHelper.atan2(dZ, dX)) - 90.0f);
  }
}
