package me.roundaround.armorstands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.roundaround.armorstands.network.ServerNetworking;
import net.fabricmc.api.ModInitializer;

public final class ArmorStandsMod implements ModInitializer {
  public static final String MOD_ID = "armorstands";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    ServerNetworking.registerReceivers();
  }
}
