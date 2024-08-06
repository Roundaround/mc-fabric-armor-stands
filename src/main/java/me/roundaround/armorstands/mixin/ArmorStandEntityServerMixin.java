package me.roundaround.armorstands.mixin;

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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityServerMixin {
  @Inject(
      method = "interactAt", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)" +
          "Lnet/minecraft/entity/EquipmentSlot;"
  ), cancellable = true
  )
  public void interactAt(
      PlayerEntity playerEntity, Vec3d hitPos, Hand hand, CallbackInfoReturnable<ActionResult> info
  ) {
    if (!(playerEntity instanceof ServerPlayerEntity player) || !ArmorStandUsers.canEditArmorStands(player) ||
        !ArmorStandUsers.doesSneakStateMatchConfig(player)) {
      return;
    }

    ArmorStandEntity armorStand = (ArmorStandEntity) (Object) this;

    ScreenType screenType = LastUsedScreen.getOrDefault(player, armorStand, ScreenType.UTILITIES);

    player.openHandledScreen(ArmorStandScreenHandler.Factory.create(screenType, armorStand));
    info.setReturnValue(ActionResult.PASS);
  }
}
