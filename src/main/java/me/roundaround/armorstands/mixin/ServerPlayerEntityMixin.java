package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.server.network.ArmorStandScreenHandlerAccess;
import me.roundaround.armorstands.server.network.ServerNetworking;
import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.Clipboard;
import me.roundaround.armorstands.util.LastUsedScreen;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ArmorStandScreenHandlerAccess {
  @Shadow
  private int screenHandlerSyncId;

  @Shadow
  protected abstract void incrementScreenHandlerSyncId();

  @Shadow
  protected abstract void onScreenHandlerOpened(ScreenHandler screenHandler);

  @Inject(method = "onDisconnect", at = @At(value = "HEAD"))
  public void onDisconnect(CallbackInfo info) {
    ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

    Clipboard.remove(self);
    ArmorStandEditor.remove(self);
    LastUsedScreen.remove(self);
  }

  @Override
  @SuppressWarnings("AddedMixinMembersNamePattern")
  public void openArmorStandScreen(ArmorStandEntity armorStand, ScreenType screenType) {
    ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

    if (self.currentScreenHandler instanceof ArmorStandScreenHandler) {
      // Skip sending the close packet to the client so that it doesn't try to exit the screen.
      self.onHandledScreenClosed();
    } else if (self.currentScreenHandler != self.playerScreenHandler) {
      self.closeHandledScreen();
    }

    LastUsedScreen.set(self, armorStand, screenType);

    this.incrementScreenHandlerSyncId();
    ServerNetworking.sendOpenScreenPacket(self, this.screenHandlerSyncId, armorStand, screenType);
    self.currentScreenHandler = new ArmorStandScreenHandler(
        this.screenHandlerSyncId, self.getInventory(), armorStand, screenType);
    this.onScreenHandlerOpened(self.currentScreenHandler);
  }
}
