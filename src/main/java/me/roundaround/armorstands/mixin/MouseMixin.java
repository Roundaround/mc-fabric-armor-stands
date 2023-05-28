package me.roundaround.armorstands.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.gui.screen.PassesEventsThrough;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mouse.class)
public abstract class MouseMixin {
  @Shadow
  private MinecraftClient client;

  @Inject(method = "isCursorLocked", at = @At(value = "HEAD"), cancellable = true)
  public void isCursorLocked(CallbackInfoReturnable<Boolean> info) {
    if (client.currentScreen instanceof AbstractArmorStandScreen) {
      info.setReturnValue(((AbstractArmorStandScreen) client.currentScreen).isCursorLocked());
    }
  }

  @ModifyExpressionValue(
      method = "onMouseButton",
      at = @At(
          value = "FIELD",
          target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
          ordinal = 3
      )
  )
  private Screen modifyCurrentScreen(Screen screen) {
    return
        screen instanceof PassesEventsThrough && ((PassesEventsThrough) screen).shouldPassEvents()
            ? null
            : screen;
  }
}
