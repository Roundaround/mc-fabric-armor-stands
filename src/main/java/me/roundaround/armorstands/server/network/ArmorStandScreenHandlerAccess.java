package me.roundaround.armorstands.server.network;

import me.roundaround.armorstands.network.ScreenType;
import net.minecraft.entity.decoration.ArmorStandEntity;

public interface ArmorStandScreenHandlerAccess {
  default void openArmorStandScreen(ArmorStandEntity armorStand, ScreenType screenType) {
  }
}
