package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
  @Shadow
  @Final
  private MinecraftClient client;

  @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
  private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
    if (client.currentScreen instanceof AbstractArmorStandScreen) {
      ci.cancel();
    }
  }
}
