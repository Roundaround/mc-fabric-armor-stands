package me.roundaround.armorstands.server;

import com.mojang.authlib.GameProfile;
import me.roundaround.armorstands.network.Networking;
import me.roundaround.armorstands.roundalib.config.option.StringListConfigOption;
import me.roundaround.armorstands.server.config.ServerSideConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArmorStandUsers {
  private ArmorStandUsers() {
  }

  public static final int PERMISSION_LEVEL = 2;

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean canEditArmorStands(ServerPlayerEntity player) {
    MinecraftServer server = player.getServer();
    if (!(server instanceof MinecraftDedicatedServer)) {
      return true;
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

    return contains(player.getGameProfile());
  }

  public static boolean contains(GameProfile profile) {
    return ServerSideConfig.getInstance().allowedUsers.getValue().contains(profile.getId().toString());
  }

  public static void add(GameProfile profile) {
    StringListConfigOption allowedUsers = ServerSideConfig.getInstance().allowedUsers;
    String toAdd = profile.getId().toString();
    if (allowedUsers.getValue().stream().noneMatch((uuid) -> uuid.equalsIgnoreCase(toAdd))) {
      allowedUsers.add(toAdd);
    }
  }

  public static void remove(GameProfile profile) {
    ServerSideConfig.getInstance().allowedUsers.remove(profile.getId().toString());
  }

  public static void reload() {
    ServerSideConfig.getInstance().syncWithStore();
  }

  public static Collection<String> getNamesAndUuids(MinecraftServer server) {
    List<String> uuids = ServerSideConfig.getInstance().allowedUsers.getValue();
    List<String> values = new ArrayList<>(uuids);
    PlayerManager playerManager = server.getPlayerManager();
    values.addAll(uuids.stream()
        .map(playerManager::getPlayer)
        .filter(Objects::nonNull)
        .map(PlayerEntity::getGameProfile)
        .map(GameProfile::getName)
        .collect(Collectors.toSet()));
    return values;
  }
}
