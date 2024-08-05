package me.roundaround.armorstands.server;

import me.roundaround.armorstands.network.Networking;
import me.roundaround.armorstands.server.config.ServerSideConfig;
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

  public static boolean isInAllowlist(ServerPlayerEntity player) {
    return WHITELIST.isAllowed(player.getGameProfile());
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean canEditArmorStands(PlayerEntity playerEntity) {
    MinecraftServer server = playerEntity.getServer();
    if (!(server instanceof MinecraftDedicatedServer)) {
      return true;
    }

    if (!(playerEntity instanceof ServerPlayerEntity player)) {
      return false;
    }

    if (!ServerPlayNetworking.canSend(player, Networking.PongS2C.ID)) {
      return false;
    }

    ServerSideConfig config = ServerSideConfig.getInstance();
    if (!config.enforcePermissions.getValue()) {
      return true;
    }

    if (config.opsHavePermissions.getValue() && player.hasPermissionLevel(PERMISSION_LEVEL)) {
      return true;
    }

    return config.allowedUsers.getValue().contains(player.getGameProfile().getId().toString());
  }

  public static String[] getNames() {
    return WHITELIST.getNames();
  }
}
