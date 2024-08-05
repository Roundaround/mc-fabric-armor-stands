package me.roundaround.armorstands.server;

import me.roundaround.armorstands.server.config.ServerSideConfig;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ArmorStandsServerMod implements DedicatedServerModInitializer {
  @Override
  public void onInitializeServer() {
    ServerSideConfig.getInstance().init();
  }
}
