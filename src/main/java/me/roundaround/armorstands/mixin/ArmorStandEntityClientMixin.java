package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.armorstands.client.gui.screen.ArmorStandPoseScreen;
import me.roundaround.armorstands.network.PosePart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;

@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityClientMixin {
  @Inject(method = "setHeadRotation", at = @At("RETURN"))
  public void setHeadRotation(EulerAngle headRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen) {
      ArmorStandPoseScreen screen = (ArmorStandPoseScreen) client.currentScreen;
      screen.onArmorStandPoseChanged((ArmorStandEntity) (Object) this, PosePart.HEAD);
    }
  }

  @Inject(method = "setBodyRotation", at = @At("RETURN"))
  public void setBodyRotation(EulerAngle bodyRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen) {
      ArmorStandPoseScreen screen = (ArmorStandPoseScreen) client.currentScreen;
      screen.onArmorStandPoseChanged((ArmorStandEntity) (Object) this, PosePart.BODY);
    }
  }

  @Inject(method = "setLeftArmRotation", at = @At("RETURN"))
  public void setLeftArmRotation(EulerAngle leftArmRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen) {
      ArmorStandPoseScreen screen = (ArmorStandPoseScreen) client.currentScreen;
      screen.onArmorStandPoseChanged((ArmorStandEntity) (Object) this, PosePart.LEFT_ARM);
    }
  }

  @Inject(method = "setRightArmRotation", at = @At("RETURN"))
  public void setRightArmRotation(EulerAngle rightArmRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen) {
      ArmorStandPoseScreen screen = (ArmorStandPoseScreen) client.currentScreen;
      screen.onArmorStandPoseChanged((ArmorStandEntity) (Object) this, PosePart.RIGHT_ARM);
    }
  }

  @Inject(method = "setLeftLegRotation", at = @At("RETURN"))
  public void setLeftLegRotation(EulerAngle leftLegRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen) {
      ArmorStandPoseScreen screen = (ArmorStandPoseScreen) client.currentScreen;
      screen.onArmorStandPoseChanged((ArmorStandEntity) (Object) this, PosePart.LEFT_LEG);
    }
  }

  @Inject(method = "setRightLegRotation", at = @At("RETURN"))
  public void setRightLegRotation(EulerAngle rightLegRotation, CallbackInfo info) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof ArmorStandPoseScreen) {
      ArmorStandPoseScreen screen = (ArmorStandPoseScreen) client.currentScreen;
      screen.onArmorStandPoseChanged((ArmorStandEntity) (Object) this, PosePart.RIGHT_LEG);
    }
  }
}
