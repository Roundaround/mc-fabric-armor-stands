package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
@Environment(value = EnvType.CLIENT)
public abstract class EntityMixin {
  @Inject(method = "updateTrackedPositionAndAngles", at = @At(value = "HEAD"), cancellable = true)
  public void updateTrackedPositionAndAngles(Vec3d pos, float yaw, float pitch, CallbackInfo info) {
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
      info.cancel();
    }
  }

  @Unique
  private Entity self() {
    return (Entity) (Object) this;
  }
}
