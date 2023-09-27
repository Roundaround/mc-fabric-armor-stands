package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@Environment(value = EnvType.CLIENT)
public abstract class LivingEntityMixin {
  @Inject(method = "updateTrackedPositionAndAngles", at = @At(value = "HEAD"), cancellable = true)
  public void updateTrackedPositionAndAngles(
      double x,
      double y,
      double z,
      float yaw,
      float pitch,
      int interpolationSteps,
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
