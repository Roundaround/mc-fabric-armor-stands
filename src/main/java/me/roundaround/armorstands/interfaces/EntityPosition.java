package me.roundaround.armorstands.interfaces;

import net.minecraft.world.phys.Vec3;

public interface EntityPosition {
  default Vec3 armorstands$getPos() {
    return Vec3.ZERO;
  }
}
