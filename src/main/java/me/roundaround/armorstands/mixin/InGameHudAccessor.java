package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
  @Invoker("updateVignetteDarkness")
  public void invokeUpdateVignetteDarkness(Entity entity);
  
  @Invoker("renderVignetteOverlay")
  public void invokeRenderVignetteOverlay(Entity entity);
}
