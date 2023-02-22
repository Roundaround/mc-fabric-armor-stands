package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.Clipboard;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
  @Inject(method = "onDisconnect", at = @At(value = "HEAD"))
  public void onDisconnect(CallbackInfo info) {
    Clipboard.remove((ServerPlayerEntity) (Object) this);
    ArmorStandEditor.remove((ServerPlayerEntity) (Object) this);
  }
}
