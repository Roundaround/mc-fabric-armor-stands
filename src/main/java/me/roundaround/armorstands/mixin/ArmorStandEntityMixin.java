package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin {
  @Shadow
  public abstract boolean isMarker();

  @Inject(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"), cancellable = true)
  public void interactAt(
      PlayerEntity player,
      Vec3d hitPos,
      Hand hand,
      CallbackInfoReturnable<ActionResult> info) {
    player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player0) -> {
      return new ArmorStandScreenHandler(syncId, playerInventory);
    }, Text.literal("")));
    info.setReturnValue(ActionResult.PASS);
  }
}
