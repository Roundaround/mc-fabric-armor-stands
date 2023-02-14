package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
@Environment(value = EnvType.CLIENT)
public abstract class LivingEntityMixin {
  @Shadow
  protected int bodyTrackingIncrements;

  @Inject(method = "updateTrackedPositionAndAngles", at = @At(value = "HEAD"), cancellable = true)
  public void updateTrackedPositionAndAngles(
      double x,
      double y,
      double z,
      float yaw,
      float pitch,
      int interpolationSteps,
      boolean interpolate,
      CallbackInfo info) {
    LivingEntity self = (LivingEntity) (Object) this;

    if (!(self instanceof ArmorStandEntity)) {
      return;
    }

    World world = self.getWorld();
    if (!world.isClient) {
      return;
    }

    MinecraftClient client = MinecraftClient.getInstance();
    ScreenHandler screenHandler = client.player.currentScreenHandler;
    if (!(screenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    if (((ArmorStandScreenHandler) screenHandler).getArmorStand() == self) {
      info.cancel();
    }
  }
}
