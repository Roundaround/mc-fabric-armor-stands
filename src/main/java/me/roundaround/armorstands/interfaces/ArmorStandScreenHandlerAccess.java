package me.roundaround.armorstands.interfaces;

import me.roundaround.armorstands.network.ScreenType;
import net.minecraft.world.entity.decoration.ArmorStand;

public interface ArmorStandScreenHandlerAccess {
  default void armorstands$openScreen(ArmorStand armorStand, ScreenType screenType) {
  }
}
