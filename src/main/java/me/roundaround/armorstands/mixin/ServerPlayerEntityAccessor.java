package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccessor {
  @Accessor("screenHandlerSyncId")
  public int getScreenHandlerSyncId();

  @Invoker("incrementScreenHandlerSyncId")
  public void invokeIncrementScreenHandlerSyncId();

  @Invoker("onScreenHandlerOpened")
  public void invokeOnScreenHandlerOpened(ScreenHandler screenHandler);
}
