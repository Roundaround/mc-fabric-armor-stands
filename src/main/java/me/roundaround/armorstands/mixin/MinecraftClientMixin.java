package me.roundaround.armorstands.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.gui.screen.PassesEventsThrough;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
  @Shadow
  public Screen currentScreen;

  @Inject(method = "hasOutline", at = @At(value = "HEAD"), cancellable = true)
  private void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> info) {
    if (!(currentScreen instanceof AbstractArmorStandScreen)) {
      return;
    }

    info.setReturnValue(((AbstractArmorStandScreen) currentScreen).shouldHighlight(entity));
  }

  @ModifyExpressionValue(
      method = "tick", at = @At(
      value = "FIELD",
      target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"
  ), slice = @Slice(
      from = @At(
          value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;resetDebugHudChunk()V"
      ), to = @At(
      value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V"
  )
  )
  )
  private Screen modifyCurrentScreen(Screen screen) {
    return screen instanceof PassesEventsThrough && ((PassesEventsThrough) screen).shouldPassEvents() ? null : screen;
  }

  @WrapWithCondition(
      method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;unpressAll()V")
  )
  private boolean shouldUnpressAll(@Local(argsOnly = true) Screen screen) {
    if (!(screen instanceof AbstractArmorStandScreen armorStandScreen)) {
      return true;
    }
    return !armorStandScreen.shouldPassEvents();
  }

  @WrapWithCondition(
      method = "setScreen", at = @At(
      value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;skipGameRender:Z"
  )
  )
  private boolean shouldSkipGameRender(
      MinecraftClient client, boolean skipGameRender, @Local(argsOnly = true) Screen screen
  ) {
    if (!(screen instanceof AbstractArmorStandScreen armorStandScreen)) {
      return true;
    }
    return !armorStandScreen.shouldPassEvents();
  }
}
