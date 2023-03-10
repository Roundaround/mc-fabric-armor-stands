package me.roundaround.armorstands.network;

import java.util.Arrays;

import net.minecraft.text.Text;

public enum ScreenType {
  UTILITIES("utilities", 0),
  MOVE("move", 1),
  ROTATE("rotate", 2),
  POSE("pose", 3),
  PRESETS("presets", 4),
  INVENTORY("inventory", 5);

  private String id;
  private int uIndex;

  private ScreenType(String id, int uIndex) {
    this.id = id;
    this.uIndex = uIndex;
  }

  public String getId() {
    return id;
  }

  public int getUIndex() {
    return uIndex;
  }

  public Text getDisplayName() {
    return Text.translatable("armorstands.screen." + id);
  }

  public boolean usesInventory() {
    return this == INVENTORY;
  }

  public ScreenType next() {
    int index = ordinal() + 1;
    if (index >= values().length) {
      index = 0;
    }
    return values()[index];
  }

  public ScreenType previous() {
    int index = ordinal() - 1;
    if (index < 0) {
      index = values().length - 1;
    }
    return values()[index];
  }

  public static ScreenType fromId(String id) {
    return Arrays.stream(values())
        .filter(type -> type.getId().equals(id))
        .findFirst()
        .orElse(null);
  }
}
