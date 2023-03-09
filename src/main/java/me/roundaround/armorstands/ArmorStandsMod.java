package me.roundaround.armorstands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.roundaround.armorstands.network.packet.c2s.AdjustPosPacket;
import me.roundaround.armorstands.network.packet.c2s.AdjustPosePacket;
import me.roundaround.armorstands.network.packet.c2s.AdjustYawPacket;
import me.roundaround.armorstands.network.packet.c2s.InitSlotsPacket;
import me.roundaround.armorstands.network.packet.c2s.SetFlagPacket;
import me.roundaround.armorstands.network.packet.c2s.SetPosePacket;
import me.roundaround.armorstands.network.packet.c2s.SetPosePresetPacket;
import me.roundaround.armorstands.network.packet.c2s.SetYawPacket;
import me.roundaround.armorstands.network.packet.c2s.UndoPacket;
import me.roundaround.armorstands.network.packet.c2s.UtilityActionPacket;
import me.roundaround.armorstands.server.ArmorStandUsers;
import me.roundaround.armorstands.server.command.ArmorStandsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class ArmorStandsMod implements ModInitializer {
  public static final String MOD_ID = "armorstands";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    registerReceivers();

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

  private static void registerReceivers() {
    InitSlotsPacket.registerServerReceiver();
    AdjustYawPacket.registerServerReceiver();
    SetYawPacket.registerServerReceiver();
    AdjustPosPacket.registerServerReceiver();
    UtilityActionPacket.registerServerReceiver();
    SetFlagPacket.registerServerReceiver();
    SetPosePacket.registerServerReceiver();
    SetPosePresetPacket.registerServerReceiver();
    AdjustPosePacket.registerServerReceiver();
    UndoPacket.registerServerReceiver();
  }
}
