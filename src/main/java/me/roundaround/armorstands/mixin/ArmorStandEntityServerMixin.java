package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.client.ClientSideConfig;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.server.ArmorStandUsers;
import me.roundaround.armorstands.server.config.ServerSideConfig;
import me.roundaround.armorstands.util.LastUsedScreen;
import me.roundaround.armorstands.roundalib.config.option.BooleanConfigOption;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityServerMixin {
  @Inject(
      method = "interactAt", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;getPreferredEquipmentSlot" +
          "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"
  ), cancellable = true
  )
  public void interactAt(
      PlayerEntity playerEntity, Vec3d hitPos, Hand hand, CallbackInfoReturnable<ActionResult> info
  ) {
    if (!(playerEntity instanceof ServerPlayerEntity player) || !ArmorStandUsers.canEditArmorStands(player)) {
      return;
    }

    if (player.isSneaking() != doesConfigRequireSneaking(player)) {
      return;
    }

    ArmorStandEntity self = (ArmorStandEntity) (Object) this;
    ScreenType screenType = LastUsedScreen.getOrDefault(player, self, ScreenType.UTILITIES);
    player.openArmorStandScreen(self, screenType);
    info.setReturnValue(ActionResult.PASS);
  }

  @Unique
  private static boolean doesConfigRequireSneaking(ServerPlayerEntity player) {
    MinecraftServer server = player.getServer();
    BooleanConfigOption configOption = (server != null && server.isDedicated()) ?
        ServerSideConfig.getInstance().requireSneakingToEdit :
        ClientSideConfig.getInstance().requireSneakingToEdit;
    return configOption.getValue();
  }
}
