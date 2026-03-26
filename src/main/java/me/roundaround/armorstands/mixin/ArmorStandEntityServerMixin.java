package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.client.ClientSideConfig;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.roundalib.config.option.BooleanConfigOption;
import me.roundaround.armorstands.server.ArmorStandUsers;
import me.roundaround.armorstands.server.config.ServerSideConfig;
import me.roundaround.armorstands.util.LastUsedScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandEntityServerMixin {
  @Inject(
      method = "interactAt", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/decoration/ArmorStand;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"
  ), cancellable = true
  )
  public void interactAt(
      Player playerEntity,
      Vec3 hitPos,
      InteractionHand hand,
      CallbackInfoReturnable<InteractionResult> info
  ) {
    if (!(playerEntity instanceof ServerPlayer player) || !ArmorStandUsers.canEditArmorStands(player)) {
      return;
    }

    if (player.isShiftKeyDown() != doesConfigRequireSneaking(player)) {
      return;
    }

    ArmorStand self = (ArmorStand) (Object) this;
    ScreenType screenType = LastUsedScreen.getOrDefault(player, self, ScreenType.UTILITIES);
    player.armorstands$openScreen(self, screenType);
    info.setReturnValue(InteractionResult.PASS);
  }

  @Unique
  private static boolean doesConfigRequireSneaking(ServerPlayer player) {
    MinecraftServer server = player.level().getServer();
    BooleanConfigOption configOption = server.isDedicatedServer() ?
        ServerSideConfig.getInstance().requireSneakingToEdit :
        ClientSideConfig.getInstance().requireSneakingToEdit;
    return configOption.getValue();
  }
}
