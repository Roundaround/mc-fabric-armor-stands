package me.roundaround.armorstands.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
  @Invoker("updateVignetteDarkness")
  public void invokeUpdateVignetteDarkness(Entity entity);

  @Invoker("renderVignetteOverlay")
  public void invokeRenderVignetteOverlay(MatrixStack matrixStack, Entity entity);
}
