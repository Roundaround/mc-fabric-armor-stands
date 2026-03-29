package me.roundaround.armorstands.interfaces;

import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.gradle.api.annotation.InjectedInterface;
import net.minecraft.world.entity.decoration.ArmorStand;

@InjectedInterface
public interface ArmorStandScreenHandlerAccess {
  default void armorstands$openScreen(ArmorStand armorStand, ScreenType screenType) {
  }
}
