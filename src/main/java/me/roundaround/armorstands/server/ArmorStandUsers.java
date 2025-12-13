package me.roundaround.armorstands.server;

import me.roundaround.armorstands.network.Networking;
import me.roundaround.armorstands.roundalib.config.option.StringListConfigOption;
import me.roundaround.armorstands.server.config.ServerSideConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.DefaultPermissions;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.NameToIdCache;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArmorStandUsers {
  private ArmorStandUsers() {
  }

  public static final int PERMISSION_LEVEL = 2;

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean canEditArmorStands(ServerPlayerEntity player) {
    MinecraftServer server = player.getEntityWorld().getServer();
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

    if (config.opsHavePermissions.getValue() && player.getPermissions().hasPermission(DefaultPermissions.GAMEMASTERS)) {
      return true;
    }

    return contains(player.getPlayerConfigEntry());
  }

  public static boolean contains(PlayerConfigEntry player) {
    return ServerSideConfig.getInstance().allowedUsers.getValue().contains(player.id().toString());
  }

  public static void add(PlayerConfigEntry player) {
    StringListConfigOption allowedUsers = ServerSideConfig.getInstance().allowedUsers;
    String toAdd = player.id().toString();
    if (allowedUsers.getValue().stream().noneMatch((uuid) -> uuid.equalsIgnoreCase(toAdd))) {
      allowedUsers.add(toAdd);
      if (allowedUsers.isDirty()) {
        ServerSideConfig.getInstance().writeToStore();
      }
    }
  }

  public static void remove(PlayerConfigEntry player) {
    StringListConfigOption allowedUsers = ServerSideConfig.getInstance().allowedUsers;
    allowedUsers.remove(player.id().toString());
    if (allowedUsers.isDirty()) {
      ServerSideConfig.getInstance().writeToStore();
    }
  }

  public static void reload() {
    ServerSideConfig.getInstance().syncWithStore();
  }

  public static Collection<String> listNames(MinecraftServer server) {
    NameToIdCache cache = server.getApiServices().nameToIdCache();
    return ServerSideConfig.getInstance().allowedUsers.getValue().stream().map((rawUuid) -> {
      UUID uuid;
      try {
        uuid = UUID.fromString(rawUuid);
      } catch (IllegalArgumentException unused) {
        return null;
      }

      Optional<PlayerConfigEntry> opt = cache.getByUuid(uuid);
      return opt.map(PlayerConfigEntry::name).orElse(null);
    }).filter(Objects::nonNull).collect(Collectors.toSet());
  }
}
