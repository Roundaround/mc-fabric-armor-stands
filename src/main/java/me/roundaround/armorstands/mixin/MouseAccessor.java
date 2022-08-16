package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public interface MouseAccessor {
  @Accessor("x")
  void setX(double x);
  
  @Accessor("y")
  void setY(double y);
}
