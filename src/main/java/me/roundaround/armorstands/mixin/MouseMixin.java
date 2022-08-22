package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public abstract class MouseMixin {
  @Shadow
  private MinecraftClient client;

  @Inject(method = "isCursorLocked", at = @At(value = "HEAD"), cancellable = true)
  public void isCursorLocked(CallbackInfoReturnable<Boolean> info) {
    if (client.currentScreen instanceof ArmorStandScreen) {
      info.setReturnValue(((ArmorStandScreen) client.currentScreen).isCursorLocked());
    }
  }
}
