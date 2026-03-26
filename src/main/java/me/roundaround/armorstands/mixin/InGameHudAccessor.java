package me.roundaround.armorstands.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface InGameHudAccessor {
  @Invoker("updateVignetteBrightness")
  void invokeUpdateVignetteDarkness(Entity entity);

  @Invoker("renderVignette")
  void invokeRenderVignetteOverlay(GuiGraphics context, Entity entity);
}
