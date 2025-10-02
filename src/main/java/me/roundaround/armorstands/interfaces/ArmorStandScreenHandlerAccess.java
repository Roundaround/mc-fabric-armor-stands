package me.roundaround.armorstands.interfaces;

import me.roundaround.armorstands.network.ScreenType;
import net.minecraft.entity.decoration.ArmorStandEntity;

public interface ArmorStandScreenHandlerAccess {
  default void armorstands$openScreen(ArmorStandEntity armorStand, ScreenType screenType) {
  }
}
