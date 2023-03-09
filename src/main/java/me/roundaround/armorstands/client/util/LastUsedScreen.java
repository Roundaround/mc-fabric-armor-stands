package me.roundaround.armorstands.client.util;

import java.util.Optional;

import me.roundaround.armorstands.client.gui.screen.ScreenFactory;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class LastUsedScreen {
  private static Optional<Integer> lastEditedArmorStandId = Optional.empty();
  private static Optional<ScreenFactory> lastUsedScreen = Optional.empty();

  public static ScreenFactory get(ArmorStandEntity armorStand, ScreenFactory defaultFactory) {
    if (lastEditedArmorStandId.isPresent()
        && lastEditedArmorStandId.get() == armorStand.getId()
        && lastUsedScreen.isPresent()) {
      return lastUsedScreen.get();
    }
    return defaultFactory;
  }

  public static void set(ScreenFactory screenFactory, ArmorStandEntity armorStand) {
    lastEditedArmorStandId = Optional.of(armorStand.getId());
    lastUsedScreen = Optional.of(screenFactory);
  }
}
