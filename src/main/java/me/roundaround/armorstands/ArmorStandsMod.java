package me.roundaround.armorstands;

import me.roundaround.armorstands.client.ClientSideConfig;
import me.roundaround.armorstands.generated.Constants;
import me.roundaround.armorstands.network.Networking;
import me.roundaround.armorstands.server.command.ArmorStandsCommand;
import me.roundaround.armorstands.server.network.ServerNetworking;
import me.roundaround.gradle.api.annotation.Entrypoint;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entrypoint(Entrypoint.MAIN)
public final class ArmorStandsMod implements ModInitializer {
  public static final String MOD_ID = "armorstands";
  public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_ID);

  @Override
  public void onInitialize() {
    ClientSideConfig.getInstance().init();

    Networking.registerS2CPayloads();
    Networking.registerC2SPayloads();

    ServerNetworking.registerReceivers();

    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
      if (environment.dedicated) {
        ArmorStandsCommand.register(dispatcher);
      }
    });
  }
}
