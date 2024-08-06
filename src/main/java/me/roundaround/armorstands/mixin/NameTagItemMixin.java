package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.server.ArmorStandUsers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public abstract class NameTagItemMixin {
  @Inject(
      method = "useOnEntity", at = @At(
      value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setCustomName(Lnet/minecraft/text/Text;)V"
  )
  )
  public void useOnEntity(
      ItemStack stack,
      PlayerEntity playerEntity,
      LivingEntity entity,
      Hand hand,
      CallbackInfoReturnable<ActionResult> info
  ) {
    if (!(playerEntity instanceof ServerPlayerEntity player) || !ArmorStandUsers.canEditArmorStands(player)) {
      return;
    }

    if (entity instanceof ArmorStandEntity) {
      entity.setCustomNameVisible(true);
    }
  }
}
