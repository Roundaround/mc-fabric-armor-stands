package me.roundaround.armorstands.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class ArmorStandPositioning {
  public static void setPosition(Entity entity, double x, double z) {
    setPosition(entity, x, entity.getPos().y, z);
  }

  public static void setPosition(Entity entity, double x, double y, double z) {
    entity.updateTrackedPosition(x, y, z);
    entity.setPosition(new Vec3d(x, y, z));
    entity.resetPosition();
  }
}
