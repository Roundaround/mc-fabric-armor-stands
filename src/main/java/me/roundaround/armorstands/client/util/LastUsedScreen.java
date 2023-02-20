package me.roundaround.armorstands.client.util;

import java.util.Optional;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen.ScreenConstructor;
import me.roundaround.armorstands.client.gui.screen.ArmorStandInventoryScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandMoveScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandPoseScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandPresetsScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandRotateScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandUtilitiesScreen;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class LastUsedScreen {
  private static Optional<Integer> lastEditedArmorStandId = Optional.empty();
  private static Optional<ScreenType> lastUsedScreen = Optional.empty();

  public static AbstractArmorStandScreen get(ArmorStandScreenHandler handler, ArmorStandEntity armorStand) {
    if (lastEditedArmorStandId.isPresent()
        && lastEditedArmorStandId.get() == armorStand.getId()
        && lastUsedScreen.isPresent()) {
      return lastUsedScreen.get().create(handler, armorStand);
    }
    return ScreenType.UTILITIES.create(handler, armorStand);
  }

  public static void set(AbstractArmorStandScreen screen) {
    lastEditedArmorStandId = Optional.of(screen.getArmorStand().getId());
    lastUsedScreen = Optional.of(screen.getScreenType());
  }

  public static enum ScreenType {
    UTILITIES(ArmorStandUtilitiesScreen::new),
    MOVE(ArmorStandMoveScreen::new),
    ROTATE(ArmorStandRotateScreen::new),
    POSE(ArmorStandPoseScreen::new),
    PRESETS(ArmorStandPresetsScreen::new),
    INVENTORY(ArmorStandInventoryScreen::new);

    private final ScreenConstructor<?> constructor;

    private ScreenType(ScreenConstructor<?> constructor) {
      this.constructor = constructor;
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractArmorStandScreen> T create(ArmorStandScreenHandler handler, ArmorStandEntity armorStand) {
      return (T) constructor.accept(handler, armorStand);
    }
  }
}
