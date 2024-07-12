package me.roundaround.armorstands.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
  @Invoker("updateVignetteDarkness")
  void invokeUpdateVignetteDarkness(Entity entity);

  @Invoker("renderVignetteOverlay")
  void invokeRenderVignetteOverlay(DrawContext context, Entity entity);
}
