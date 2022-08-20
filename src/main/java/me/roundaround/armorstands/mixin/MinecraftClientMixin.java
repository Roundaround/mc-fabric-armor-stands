package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
  @Shadow
  private Screen currentScreen;

  @Inject(method = "hasOutline", at = @At(value = "HEAD"), cancellable = true)
  private void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> info) {
    if (!(currentScreen instanceof AbstractArmorStandScreen)) {
      return;
    }

    info.setReturnValue(((AbstractArmorStandScreen) currentScreen).shouldHighlight(entity));
  }
}
