package me.roundaround.armorstands.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.roundaround.armorstands.client.gui.screen.PassesEventsThrough;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Keyboard.class)
public class KeyboardMixin {
  @ModifyExpressionValue(
      method = "onKey", at = @At(
      value = "FIELD",
      target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
      ordinal = 2
  )
  )
  private Screen modifyCurrentScreen(Screen screen) {
    return screen instanceof PassesEventsThrough passesEventsThrough &&
        passesEventsThrough.shouldPassEvents() ? null : screen;
  }
}
