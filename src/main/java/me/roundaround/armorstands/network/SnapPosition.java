package me.roundaround.armorstands.network;

import java.util.Arrays;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.util.ArmorStandPositioning;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public enum SnapPosition {
  EDGE("edge"),
  CENTER("center"),
  PIXEL("pixel");

  private final String id;

  private SnapPosition(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }

  public void apply(Entity entity) {
    Vec3d position = entity.getPos();
    double x = position.x;
    double z = position.z;

    switch (this) {
      case EDGE:
        x = Math.floor(x);
        z = Math.floor(z);
        break;
      case CENTER:
        x = Math.round(x + 0.5) - 0.5;
        z = Math.round(z + 0.5) - 0.5;
        break;
      case PIXEL:
      default:
        x = Math.round(x * 16) / 16.0;
        z = Math.round(z * 16) / 16.0;
    }

    ArmorStandPositioning.setPosition(entity, x, z);
  }

  public static SnapPosition fromString(String value) {
    return Arrays.stream(SnapPosition.values())
        .filter((flag) -> flag.id.equals(value))
        .findFirst()
        .orElseGet(() -> {
          ArmorStandsMod.LOGGER.warn("Unknown id '{}'. Defaulting to pixel.", value);
          return PIXEL;
        });
  }
}
