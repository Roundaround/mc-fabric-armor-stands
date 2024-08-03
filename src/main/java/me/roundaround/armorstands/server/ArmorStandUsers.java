package me.roundaround.armorstands.server;

import me.roundaround.armorstands.network.Networking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;

public class ArmorStandUsers {
  private static final File FILE = new File("armorstandsusers.json");

  public static final int PERMISSION_LEVEL = 2;
  public static final Whitelist WHITELIST = new Whitelist(FILE);

  public static boolean isExplicitlyListed(ServerPlayerEntity player) {
    return WHITELIST.isAllowed(player.getGameProfile());
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean canEditArmorStands(PlayerEntity playerEntity) {
    if (!(playerEntity instanceof ServerPlayerEntity player)) {
      return false;
    }

    if (!ServerPlayNetworking.canSend(player, Networking.PongS2C.ID)) {
      return false;
    }

    MinecraftServer server = player.getServer();
    if (server instanceof MinecraftDedicatedServer dedicatedServer) {
      ServerPropertiesHandler propertiesHandler = dedicatedServer.getProperties();
      if (!propertiesHandler.getEnforceArmorStandPermissions()) {
        return true;
      }
    }

    if (player.hasPermissionLevel(PERMISSION_LEVEL) || server.isSingleplayer()) {
      return true;
    }

    return isExplicitlyListed(player);
  }

  public static String[] getNames() {
    return WHITELIST.getNames();
  }
}
