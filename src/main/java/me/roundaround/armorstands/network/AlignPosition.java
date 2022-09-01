package me.roundaround.armorstands.network;

import java.util.Arrays;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.util.ArmorStandEditor;

public enum AlignPosition {
  EDGE("edge"),
  CENTER("center"),
  STANDING("standing"),
  SITTING("sitting"),
  BLOCK("block"),
  ITEM_UPRIGHT("item_upright"),
  ITEM_FLAT("item_flat"),
  TOOL_FLAT("tool_flat");

  private final String id;

  private AlignPosition(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }

  public void apply(ArmorStandEditor editor) {
    switch (this) {
      case EDGE:
        editor.alignHorizontalToEdge();
        break;
      case CENTER:
        editor.alignHorizontalToCenter();
        break;
      case STANDING:
        editor.snapToGround(false);
        break;
      case SITTING:
        editor.snapToGround(true);
        break;
      default:
    }
  }

  public static AlignPosition fromString(String value) {
    return Arrays.stream(AlignPosition.values())
        .filter((align) -> align.id.equals(value))
        .findFirst()
        .orElseGet(() -> {
          ArmorStandsMod.LOGGER.warn("Unknown id '{}'. Defaulting to CENTER.", value);
          return CENTER;
        });
  }
}
