package me.roundaround.armorstands.util;

import java.util.Optional;

import net.minecraft.entity.Entity;
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

  public static Optional<Vec3d> getSittingPos(ArmorStandEntity armorStand) {
    return getGroundPos(armorStand, true);
  }

  public static Optional<Vec3d> getGroundPos(ArmorStandEntity armorStand, boolean sitting) {
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

    return Optional.of(newPosition);
  }

  public static Vec3d localToRelative(Entity entity, Vec3d amount) {
    float pitch = entity.getPitch();
    float yaw = entity.getYaw();

    float pi = (float) Math.PI;

    float f = MathHelper.cos((yaw + 90f) * pi / 180f);
    float g = MathHelper.sin((yaw + 90f) * pi / 180f);
    float h = MathHelper.cos(-pitch * pi / 180f);
    float i = MathHelper.sin(-pitch * pi / 180f);
    float j = MathHelper.cos((-pitch + 90f) * pi / 180f);
    float k = MathHelper.sin((-pitch + 90f) * pi / 180f);

    Vec3d vec3d2 = new Vec3d(f * h, i, g * h);
    Vec3d vec3d3 = new Vec3d(f * j, k, g * j);

    Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1);

    double x = vec3d2.x * amount.z
        + vec3d3.x * amount.y
        + vec3d4.x * amount.x;
    double y = vec3d2.y * amount.z
        + vec3d3.y * amount.y
        + vec3d4.y * amount.x;
    double z = vec3d2.z * amount.z
        + vec3d3.z * amount.y
        + vec3d4.z * amount.x;

    return new Vec3d(x, y, z);
  }

  public static float getLookYaw(ArmorStandEntity armorStand, Vec3d point) {
    Vec3d pos = armorStand.getPos();
    double dX = point.x - pos.x;
    double dZ = point.z - pos.z;
    return MathHelper.wrapDegrees((float) Math.toDegrees(MathHelper.atan2(dZ, dX)) - 90.0f);
  }
}
