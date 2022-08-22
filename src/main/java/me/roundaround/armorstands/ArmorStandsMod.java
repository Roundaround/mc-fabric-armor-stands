package me.roundaround.armorstands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.roundaround.armorstands.network.ServerNetworking;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;

public final class ArmorStandsMod implements ModInitializer {
  public static final String MOD_ID = "armorstands";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  public static ScreenHandlerType<ArmorStandScreenHandler> ARMOR_STAND_SCREEN_HANDLER;

  @Override
  public void onInitialize() {
    Registry.register(Registry.SCREEN_HANDLER, "armor_stand", new ScreenHandlerType<>(ArmorStandScreenHandler::new));

    ServerNetworking.registerReceivers();
  }
}
