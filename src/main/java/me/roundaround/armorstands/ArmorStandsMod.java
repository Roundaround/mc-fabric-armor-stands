package me.roundaround.armorstands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.roundaround.armorstands.network.ServerNetworking;
import me.roundaround.armorstands.server.ArmorStandUsers;
import me.roundaround.armorstands.server.command.ArmorStandsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class ArmorStandsMod implements ModInitializer {
  public static final String MOD_ID = "armorstands";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    ServerNetworking.registerReceivers();

    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
      if (environment.dedicated) {
        ArmorStandsCommand.register(dispatcher);

        try {
          ArmorStandUsers.WHITELIST.load();
        } catch (Exception exception) {
          LOGGER.warn("Failed to load armor stand users: ", exception);
        }
      }
    });
  }
}
