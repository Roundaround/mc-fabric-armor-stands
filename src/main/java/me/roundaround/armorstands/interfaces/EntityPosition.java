package me.roundaround.armorstands.interfaces;

import me.roundaround.gradle.api.annotation.InjectedInterface;
import net.minecraft.world.phys.Vec3;

@InjectedInterface
public interface EntityPosition {
  default Vec3 armorstands$getPos() {
    return Vec3.ZERO;
  }
}
