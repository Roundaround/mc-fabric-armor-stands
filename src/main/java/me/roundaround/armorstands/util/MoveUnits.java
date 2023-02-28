package me.roundaround.armorstands.util;

import net.minecraft.text.Text;

public enum MoveUnits {
  PIXELS("pixels"),
  BLOCKS("blocks");

  private final String id;

  private MoveUnits(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }

  public Text getOptionValueText() {
    return Text.translatable("armorstands.move.units." + this.id);
  }

  public Text getButtonText(int amount) {
    return Text.translatable("armorstands.move." + this.id + "." + amount);
  }

  public double getAmount(int amount) {
    switch (this) {
      case PIXELS:
        return getPixelAmount(amount);
      case BLOCKS:
        return getBlockAmount(amount);
    }

    return 0;
  }

  private static double getPixelAmount(int amount) {
    // 1, 3, and 8 pixels
    switch (amount) {
      case 2:
        return 0.1875;
      case 3:
        return 0.5;
      default:
        return 0.0625;
    }
  }

  private static double getBlockAmount(int amount) {
    // 1/4, 1/3, and 1 block
    switch (amount) {
      case 2:
        return 1d / 3d;
      case 3:
        return 1;
      default:
        return 0.25;
    }
  }

  public static Text getOptionLabelText() {
    return Text.translatable("armorstands.move.units");
  }

  public static MoveUnits fromId(String id) {
    for (MoveUnits units : MoveUnits.values()) {
      if (units.getId().equals(id)) {
        return units;
      }
    }

    return PIXELS;
  }
}
