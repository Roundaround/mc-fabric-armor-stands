package me.roundaround.armorstands.client.gui.screen;

import java.util.Arrays;

import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public enum ScreenFactory {
  UTILITIES(
      ArmorStandUtilitiesScreen.class,
      ArmorStandUtilitiesScreen::new,
      ArmorStandUtilitiesScreen.TITLE,
      ArmorStandUtilitiesScreen.U_INDEX),
  MOVE(
      ArmorStandMoveScreen.class,
      ArmorStandMoveScreen::new,
      ArmorStandMoveScreen.TITLE,
      ArmorStandMoveScreen.U_INDEX),
  ROTATE(
      ArmorStandRotateScreen.class,
      ArmorStandRotateScreen::new,
      ArmorStandRotateScreen.TITLE,
      ArmorStandRotateScreen.U_INDEX),
  POSE(
      ArmorStandPoseScreen.class,
      ArmorStandPoseScreen::new,
      ArmorStandPoseScreen.TITLE,
      ArmorStandPoseScreen.U_INDEX),
  PRESETS(
      ArmorStandPresetsScreen.class,
      ArmorStandPresetsScreen::new,
      ArmorStandPresetsScreen.TITLE,
      ArmorStandPresetsScreen.U_INDEX),
  INVENTORY(
      ArmorStandInventoryScreen.class,
      ArmorStandInventoryScreen::new,
      ArmorStandInventoryScreen.TITLE,
      ArmorStandInventoryScreen.U_INDEX);

  private final Class<? extends AbstractArmorStandScreen> clazz;
  private final ScreenConstructor<?> constructor;
  private final Text tooltip;
  private final int uIndex;

  private ScreenFactory(
      Class<? extends AbstractArmorStandScreen> clazz,
      ScreenConstructor<?> constructor,
      Text tooltip,
      int uIndex) {
    this.clazz = clazz;
    this.constructor = constructor;
    this.tooltip = tooltip;
    this.uIndex = uIndex;
  }

  public boolean matchesClass(Class<? extends AbstractArmorStandScreen> clazz) {
    return clazz == this.clazz;
  }

  public AbstractArmorStandScreen construct(
      ArmorStandScreenHandler handler,
      ArmorStandEntity armorStand) {
    return this.constructor.accept(handler, armorStand);
  }

  public Text getTooltip() {
    return this.tooltip;
  }

  public int getUIndex() {
    return this.uIndex;
  }

  public ScreenFactory getNext() {
    return values()[(ordinal() + 1) % values().length];
  }

  public ScreenFactory getPrevious() {
    return values()[(ordinal() - 1 + values().length) % values().length];
  }

  public static ScreenFactory getFactory(Class<? extends AbstractArmorStandScreen> clazz) {
    return Arrays.stream(values())
        .filter(factory -> factory.clazz == clazz)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No factory for " + clazz));
  }

  public static ScreenFactory getNext(Class<? extends AbstractArmorStandScreen> clazz) {
    return getFactory(clazz).getNext();
  }

  public static ScreenFactory getPrevious(Class<? extends AbstractArmorStandScreen> clazz) {
    return getFactory(clazz).getPrevious();
  }

  @FunctionalInterface
  public static interface ScreenConstructor<T extends AbstractArmorStandScreen> {
    public T accept(
        ArmorStandScreenHandler handler,
        ArmorStandEntity armorStand);
  }
}
