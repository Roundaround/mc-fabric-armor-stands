package me.roundaround.armorstands.server;

import java.io.File;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.Whitelist;
import net.minecraft.server.network.ServerPlayerEntity;

public class ArmorStandUsers {
  private static final File FILE = new File("armorstands.json");

  public static final int PERMISSION_LEVEL = 2;
  public static final Whitelist WHITELIST = new Whitelist(FILE);

  public static boolean isExplicitlyListed(ServerPlayerEntity player) {
    return WHITELIST.isAllowed(player.getGameProfile());
  }

  public static boolean canEditArmorStands(PlayerEntity player) {
    if (!(player instanceof ServerPlayerEntity)) {
      return false;
    }

    if (player.hasPermissionLevel(PERMISSION_LEVEL)) {
      return true;
    }

    return isExplicitlyListed((ServerPlayerEntity) player);
  }

  public static String[] getNames() {
    return WHITELIST.getNames();
  }
}
