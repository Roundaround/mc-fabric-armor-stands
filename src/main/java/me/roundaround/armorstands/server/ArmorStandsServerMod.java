package me.roundaround.armorstands.server;

import me.roundaround.armorstands.server.config.ServerSideConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;

public class ArmorStandsServerMod implements DedicatedServerModInitializer {
  @Override
  public void onInitializeServer() {
    ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
      if (!(server instanceof MinecraftDedicatedServer dedicatedServer)) {
        return;
      }
      ServerSideConfig.create(dedicatedServer).init();
    });
  }
}
