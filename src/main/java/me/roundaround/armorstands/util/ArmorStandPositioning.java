package me.roundaround.armorstands.util;

import me.roundaround.armorstands.ArmorStandsMod;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class ArmorStandPositioning {
  public static void lowerDownToBlock(Entity entity) {
    Vec3d position = entity.getPos();
    ArmorStandsMod.LOGGER.info(position.y);
    ArmorStandsMod.LOGGER.info(entity.getBlockY());
    setPosition(entity, position.x, entity.getBlockY(), position.z);
  }
  
  public static void setPosition(Entity entity, double x, double z) {
    setPosition(entity, x, entity.getPos().y, z);
  }

  public static void setPosition(Entity entity, double x, double y, double z) {
    entity.updateTrackedPosition(x, y, z);
    entity.setPosition(new Vec3d(x, y, z));
    entity.resetPosition();
  }
}
