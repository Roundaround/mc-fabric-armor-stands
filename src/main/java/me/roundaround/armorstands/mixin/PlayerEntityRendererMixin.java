package me.roundaround.armorstands.mixin;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.hash.Hashing;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
  @Inject(method = "scale", at = @At("HEAD"))
  private void onScale(
      AbstractClientPlayerEntity abstractClientPlayerEntity,
      MatrixStack matrixStack,
      float f,
      CallbackInfo info) {
    // Don't want to expose his username, so check against the hash instead
    String username = abstractClientPlayerEntity.getName().getString();
    String expected = "ba0df6ea0d50feddda95673a8398abac0e6b158c7e23467ab4edcd24ce7c0a60e85728208ba73780f0bf7abe3c1628d612791d2d82ec52494d5d7ee5aa7dc94e";

    String hash = Hashing.sha512()
        .hashString(username + "V%^tAHwdHjh*r5af", StandardCharsets.UTF_8)
        .toString();

    if (!hash.equals(expected)) {
      return;
    }

    // Only adjust on April 1st
    if (LocalDate.now().getMonthValue() != 4 || LocalDate.now().getDayOfMonth() != 1) {
      return;
    }

    matrixStack.scale(1f, 0.65f, 1f);
  }
}
