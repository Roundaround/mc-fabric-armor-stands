package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.client.gui.screen.PassesEventsThrough;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {
  protected HandledScreenMixin() {
    super(null);
  }

  @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
  public void keyPressed(
      int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
    if (this instanceof PassesEventsThrough passesEventsThrough &&
        passesEventsThrough.shouldPassEvents()) {
      info.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
    }
  }
}
