package me.roundaround.armorstands;

import me.roundaround.armorstands.network.Networking;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.server.command.ArmorStandsCommand;
import me.roundaround.armorstands.server.network.ServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ArmorStandsMod implements ModInitializer {
  public static final String MOD_ID = "armorstands";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  public static final ExtendedScreenHandlerType<ArmorStandScreenHandler, ArmorStandScreenHandler.Data> ARMOR_STAND_SCREEN_HANDLER_TYPE = new ExtendedScreenHandlerType<>(
      ArmorStandScreenHandler::new, ArmorStandScreenHandler.Data.PACKET_CODEC);

  static {
    Registry.register(Registries.SCREEN_HANDLER, "armorstands:armor_stand", ARMOR_STAND_SCREEN_HANDLER_TYPE);
  }

  @Override
  public void onInitialize() {
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
