package me.roundaround.armorstands.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {
  protected HandledScreenMixin(Text title) {
    super(title);
  }

  @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
  private void onKeyPressed(
      int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
    if (((ScreenAccessor) this).getPassEvents()) {
      info.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
    }
  }
}
