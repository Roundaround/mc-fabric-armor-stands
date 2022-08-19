package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.armorstands.client.hooks.ClientHooks;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin {
  @Shadow
  public abstract boolean isMarker();

  @Inject(method = "interactAt", at = @At(value = "HEAD"), cancellable = true)
  public void interactAt(
      PlayerEntity player,
      Vec3d hitPos,
      Hand hand,
      CallbackInfoReturnable<ActionResult> info) {
    ItemStack itemStack = player.getStackInHand(hand);
    if (isMarker() || itemStack.isOf(Items.NAME_TAG)) {
      info.setReturnValue(ActionResult.PASS);
      return;
    }
    if (player.isSpectator()) {
      info.setReturnValue(ActionResult.SUCCESS);
      return;
    }
    if (player.world.isClient) {
      ClientHooks.openArmorStandScreen((ArmorStandEntity) (Object) this);
      info.setReturnValue(ActionResult.CONSUME);
      return;
    }
    info.setReturnValue(ActionResult.PASS);
  }
}
