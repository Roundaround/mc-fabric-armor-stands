package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
  @Shadow
  private MinecraftClient client;

  @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
  private void render(MatrixStack matrixStack, float delta, CallbackInfo info) {
    if (client.currentScreen instanceof ArmorStandScreen) {
      info.cancel();
    }
  }
}
