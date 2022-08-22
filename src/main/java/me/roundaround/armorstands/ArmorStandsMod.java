package me.roundaround.armorstands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.roundaround.armorstands.network.ServerNetworking;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.screen.ScreenHandlerType;

public final class ArmorStandsMod implements ModInitializer {
  public static final String MOD_ID = "armorstands";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  public static final ScreenHandlerType<ArmorStandScreenHandler> ARMOR_STAND_SCREEN_HANDLER = ScreenHandlerType.register("armor_stand", ArmorStandScreenHandler::new);

  @Override
  public void onInitialize() {
    ServerNetworking.registerReceivers();
  }
}
