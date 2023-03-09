package me.roundaround.armorstands.client.util;

import java.util.Optional;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.gui.screen.ScreenFactory;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class LastUsedScreen {
  private static Optional<Integer> lastEditedArmorStandId = Optional.empty();
  private static Optional<ScreenFactory> lastUsedScreen = Optional.empty();

  public static AbstractArmorStandScreen get(ArmorStandScreenHandler handler, ArmorStandEntity armorStand) {
    if (lastEditedArmorStandId.isPresent()
        && lastEditedArmorStandId.get() == armorStand.getId()
        && lastUsedScreen.isPresent()) {
      return lastUsedScreen.get().construct(handler, armorStand);
    }
    return ScreenFactory.UTILITIES.construct(handler, armorStand);
  }

  public static void set(ScreenFactory screenFactory, ArmorStandEntity armorStand) {
    lastEditedArmorStandId = Optional.of(armorStand.getId());
    lastUsedScreen = Optional.of(screenFactory);
  }
}
