package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.server.ArmorStandUsers;
import me.roundaround.armorstands.util.LastUsedScreen;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityServerMixin {
  @Inject(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), cancellable = true)
  public void interactAt(
      PlayerEntity player,
      Vec3d hitPos,
      Hand hand,
      CallbackInfoReturnable<ActionResult> info) {
    if (!(player instanceof ServerPlayerEntity)) {
      return;
    }

    if (!ArmorStandUsers.canEditArmorStands(player) || !player.isSneaking()) {
      return;
    }

    ArmorStandEntity armorStand = (ArmorStandEntity) (Object) this;

    ScreenType screenType = LastUsedScreen.getOrDefault(
        (ServerPlayerEntity) player,
        armorStand,
        ScreenType.UTILITIES);

    player.openHandledScreen(ArmorStandScreenHandler.Factory.create(screenType, armorStand));
    info.setReturnValue(ActionResult.PASS);
  }
}
