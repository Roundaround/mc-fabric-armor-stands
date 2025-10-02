package me.roundaround.armorstands.interfaces;

import net.minecraft.util.math.Vec3d;

public interface EntityPosition {
  default Vec3d armorstands$getPos() {
    return Vec3d.ZERO;
  }
}
