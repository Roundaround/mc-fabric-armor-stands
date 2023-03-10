package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.armorstands.client.ArmorStandsClientMod;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
  @Inject(method = "scale", at = @At("HEAD"))
  private void onScale(
      AbstractClientPlayerEntity player,
      MatrixStack matrixStack,
      float f,
      CallbackInfo info) {
    if (!ArmorStandsClientMod.isAprilFirst()) {
      return;
    }

    if (ArmorStandsClientMod.hasAdmittedInTheLastHour()) {
      return;
    }

    if (!ArmorStandsClientMod.isTheDoofusNugget(player)) {
      return;
    }

    matrixStack.scale(1f, 0.65f, 1f);
  }
}
