package me.roundaround.armorstands.network;

import java.util.Arrays;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.mixin.ArmorStandEntityAccessor;
import net.minecraft.entity.decoration.ArmorStandEntity;

public enum ArmorStandFlag {
  BASE("base"),
  ARMS("arms"),
  SMALL("small"),
  GRAVITY("gravity"),
  VISIBLE("visible"),
  NAME("name"),
  LOCKED("locked"),
  UNKNOWN("unknown");

  private final String id;

  private ArmorStandFlag(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }

  public boolean getValue(ArmorStandEntity armorStand) {
    switch (this) {
      case BASE:
        return armorStand.shouldHideBasePlate();
      case ARMS:
        return armorStand.shouldShowArms();
      case SMALL:
        return armorStand.isSmall();
      case GRAVITY:
        return armorStand.hasNoGravity();
      case VISIBLE:
        return armorStand.isInvisible();
      case NAME:
        return armorStand.isCustomNameVisible();
      case LOCKED:
        return armorStand.isInvulnerable();
      default:
        return false;
    }
  }

  public void setValue(ArmorStandEntity armorStand, boolean value) {
    ArmorStandEntityAccessor accessor = (ArmorStandEntityAccessor) armorStand;

    switch (this) {
      case BASE:
        accessor.invokeSetHideBasePlate(value);
        break;
      case ARMS:
        accessor.invokeSetShowArms(value);
        break;
      case SMALL:
        accessor.invokeSetSmall(value);
        break;
      case GRAVITY:
        armorStand.setNoGravity(value);
        break;
      case VISIBLE:
        armorStand.setInvisible(value);
        break;
      case NAME:
        armorStand.setCustomNameVisible(value);
        break;
      case LOCKED:
        armorStand.setInvulnerable(value);
        accessor.invokeSetMarker(value);
      default:
        // Do nothing for unknown
        ArmorStandsMod.LOGGER.warn("Tried to set value to flag {}. Ignoring.", name());
    }
  }

  public static ArmorStandFlag fromString(String value) {
    return Arrays.stream(ArmorStandFlag.values())
        .filter((flag) -> flag.id.equals(value))
        .findFirst()
        .orElseGet(() -> {
          ArmorStandsMod.LOGGER.warn("Unknown flag id '{}'. Returning UNKNOWN.", value);
          return UNKNOWN;
        });
  }
}
