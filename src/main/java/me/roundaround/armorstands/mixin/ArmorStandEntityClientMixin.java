package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.client.gui.screen.ArmorStandPoseScreen;
import me.roundaround.armorstands.network.PosePart;
import me.roundaround.gradle.api.annotation.MixinEnv;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntity.class)
@MixinEnv(MixinEnv.CLIENT)
public class ArmorStandEntityClientMixin {
  @Inject(method = "setHeadRotation", at = @At("RETURN"))
  public void setHeadRotation(EulerAngle headRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen screen) {
      screen.onArmorStandPoseChanged(this.self(), PosePart.HEAD);
    }
  }

  @Inject(method = "setBodyRotation", at = @At("RETURN"))
  public void setBodyRotation(EulerAngle bodyRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen screen) {
      screen.onArmorStandPoseChanged(this.self(), PosePart.BODY);
    }
  }

  @Inject(method = "setLeftArmRotation", at = @At("RETURN"))
  public void setLeftArmRotation(EulerAngle leftArmRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen screen) {
      screen.onArmorStandPoseChanged(this.self(), PosePart.LEFT_ARM);
    }
  }

  @Inject(method = "setRightArmRotation", at = @At("RETURN"))
  public void setRightArmRotation(EulerAngle rightArmRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen screen) {
      screen.onArmorStandPoseChanged(this.self(), PosePart.RIGHT_ARM);
    }
  }

  @Inject(method = "setLeftLegRotation", at = @At("RETURN"))
  public void setLeftLegRotation(EulerAngle leftLegRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen screen) {
      screen.onArmorStandPoseChanged(this.self(), PosePart.LEFT_LEG);
    }
  }

  @Inject(method = "setRightLegRotation", at = @At("RETURN"))
  public void setRightLegRotation(EulerAngle rightLegRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen screen) {
      screen.onArmorStandPoseChanged(this.self(), PosePart.RIGHT_LEG);
    }
  }

  @Unique
  private ArmorStandEntity self() {
    return (ArmorStandEntity) (Object) this;
  }
}
