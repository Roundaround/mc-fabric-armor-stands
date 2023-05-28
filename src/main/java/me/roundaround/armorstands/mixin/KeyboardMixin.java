package me.roundaround.armorstands.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.gui.screen.PassesEventsThrough;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
  @ModifyExpressionValue(
      method = "onKey",
      at = @At(
          value = "FIELD",
          target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
          ordinal = 2
      )
  )
  private Screen modifyCurrentScreen(Screen screen) {
    if (screen instanceof AbstractArmorStandScreen) {
      ArmorStandsMod.LOGGER.info("We're in the armor stand screen!");
    }
    if (screen instanceof PassesEventsThrough && ((PassesEventsThrough) screen).shouldPassEvents()) {
      ArmorStandsMod.LOGGER.info("We're in a screen that passes events through!");
    }
    return
        screen instanceof PassesEventsThrough && ((PassesEventsThrough) screen).shouldPassEvents()
            ? null
            : screen;
  }

  @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;", shift = At.Shift.AFTER))
  private void afterScreenCheck(long window, int key, int scancode, int action, int modifiers, CallbackInfo info) {
    ArmorStandsMod.LOGGER.info("We've made it!");
  }
}
