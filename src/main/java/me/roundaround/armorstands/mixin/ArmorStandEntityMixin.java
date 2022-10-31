package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.armorstands.network.ServerNetworking;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.server.ArmorStandUsers;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin {
  @Inject(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), cancellable = true)
  public void interactAt(
      PlayerEntity playerEntity,
      Vec3d hitPos,
      Hand hand,
      CallbackInfoReturnable<ActionResult> info) {
    if (!(playerEntity instanceof ServerPlayerEntity)) {
      return;
    }

    ServerPlayerEntity player = (ServerPlayerEntity) playerEntity;
    ServerPlayerEntityAccessor accessor = (ServerPlayerEntityAccessor) playerEntity;

    if (!ArmorStandUsers.canEditArmorStands(player)) {
      return;
    }

    if (player.currentScreenHandler != player.playerScreenHandler) {
      player.closeHandledScreen();
    }

    accessor.invokeIncrementScreenHandlerSyncId();
    int syncId = accessor.getScreenHandlerSyncId();

    ArmorStandEntity armorStand = (ArmorStandEntity) (Object) this;

    ServerNetworking.sendOpenScreenPacket(player, armorStand, syncId);

    ArmorStandScreenHandler screenHandler = new ArmorStandScreenHandler(syncId, player.getInventory(), armorStand);

    player.currentScreenHandler = screenHandler;
    accessor.invokeOnScreenHandlerOpened(screenHandler);

    info.setReturnValue(ActionResult.PASS);
  }
}
