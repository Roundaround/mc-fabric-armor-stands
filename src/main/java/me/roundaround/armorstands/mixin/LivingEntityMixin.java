package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.gradle.api.annotation.MixinEnv;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@MixinEnv(MixinEnv.CLIENT)
public abstract class LivingEntityMixin {
  @Inject(method = "updateTrackedPositionAndAngles", at = @At(value = "HEAD"), cancellable = true)
  public void updateTrackedPositionAndAngles(
      double x,
      double y,
      double z,
      float yaw,
      float pitch,
      int interpolationSteps,
      CallbackInfo ci
  ) {
    if (!(this.self() instanceof ArmorStandEntity self)) {
      return;
    }

    World world = self.getWorld();
    if (!world.isClient) {
      return;
    }

    MinecraftClient client = MinecraftClient.getInstance();
    if (client.player == null) {
      return;
    }

    ScreenHandler rawScreenHandler = client.player.currentScreenHandler;
    if (!(rawScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
      return;
    }

    if (screenHandler.getArmorStand() == self) {
      ci.cancel();
    }
  }

  @Unique
  private Entity self() {
    return (Entity) (Object) this;
  }
}
