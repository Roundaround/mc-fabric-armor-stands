package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.armorstands.server.ArmorStandUsers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(NameTagItem.class)
public abstract class NameTagItemMixin {
  @Inject(method = "useOnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setCustomName(Lnet/minecraft/text/Text;)V"), cancellable = true)
  public void useOnEntity(
      ItemStack stack,
      PlayerEntity player,
      LivingEntity entity,
      Hand hand,
      CallbackInfoReturnable<ActionResult> info) {
    if (!(player instanceof ServerPlayerEntity) || !ArmorStandUsers.canEditArmorStands(player)) {
      return;
    }

    if (entity instanceof ArmorStandEntity) {
      entity.setCustomNameVisible(true);
    }
  }
}
