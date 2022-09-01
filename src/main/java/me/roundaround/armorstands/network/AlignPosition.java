package me.roundaround.armorstands.network;

import java.util.Arrays;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.util.ArmorStandPositioning;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

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

  public void apply(Entity entity) {
    Vec3d position = entity.getPos();

    switch (this) {
      case EDGE:
        ArmorStandPositioning.setPosition(
            entity,
            Math.floor(position.x),
            Math.floor(position.z));
        break;
      case CENTER:
        ArmorStandPositioning.setPosition(
            entity,
            Math.round(position.x + 0.5) - 0.5,
            Math.round(position.z + 0.5) - 0.5);
        break;
      case STANDING:
        ArmorStandPositioning.lowerDownToBlock(entity);
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
