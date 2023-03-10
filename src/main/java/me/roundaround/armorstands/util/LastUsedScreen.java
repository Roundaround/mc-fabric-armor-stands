package me.roundaround.armorstands.util;

import java.util.HashMap;
import java.util.UUID;

import me.roundaround.armorstands.network.ScreenType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class LastUsedScreen {
  private static final HashMap<UUID, Entry> lastUsedScreens = new HashMap<>();

  public static void set(ServerPlayerEntity player, ArmorStandEntity armorStand, ScreenType screenType) {
    lastUsedScreens.put(player.getUuid(), new Entry(armorStand, screenType));
  }

  public static ScreenType get(ServerPlayerEntity player, ArmorStandEntity armorStand) {
    if (!lastUsedScreens.containsKey(player.getUuid())) {
      return null;
    }

    Entry entry = lastUsedScreens.get(player.getUuid());
    return entry.matches(armorStand, entry.screenType) ? entry.screenType : null;
  }

  public static ScreenType getOrDefault(
      ServerPlayerEntity player,
      ArmorStandEntity armorStand,
      ScreenType defaultScreenType) {
    ScreenType screenType = get(player, armorStand);
    return screenType == null ? defaultScreenType : screenType;
  }

  public static void remove(ServerPlayerEntity player) {
    lastUsedScreens.remove(player.getUuid());
  }

  private static class Entry {
    private final UUID armorStandUuid;
    private final ScreenType screenType;

    private Entry(ArmorStandEntity armorStand, ScreenType screenType) {
      this.armorStandUuid = armorStand.getUuid();
      this.screenType = screenType;
    }

    public boolean matches(ArmorStandEntity armorStand, ScreenType screenType) {
      return this.armorStandUuid.equals(armorStand.getUuid()) && this.screenType == screenType;
    }
  }
}
