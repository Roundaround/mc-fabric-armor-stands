package me.roundaround.armorstands.network;

import java.util.Arrays;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;

public enum PosePart {
  HEAD("head"),
  BODY("body"),
  RIGHT_ARM("rightArm"),
  LEFT_ARM("leftArm"),
  RIGHT_LEG("rightLeg"),
  LEFT_LEG("leftLeg");

  private final String id;

  private PosePart(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }

  public EulerAngle get(ArmorStandEntity armorStand) {
    switch (this) {
      case HEAD:
        return armorStand.getHeadRotation();
      case BODY:
        return armorStand.getBodyRotation();
      case RIGHT_ARM:
        return armorStand.getRightArmRotation();
      case LEFT_ARM:
        return armorStand.getLeftArmRotation();
      case RIGHT_LEG:
        return armorStand.getRightLegRotation();
      case LEFT_LEG:
        return armorStand.getLeftLegRotation();
      default:
        throw new IllegalArgumentException("Unknown part: " + this);
    }
  }

  public void set(ArmorStandEntity armorStand, EulerAngle angle) {
    switch (this) {
      case HEAD:
        armorStand.setHeadRotation(angle);
        break;
      case BODY:
        armorStand.setBodyRotation(angle);
        break;
      case RIGHT_ARM:
        armorStand.setRightArmRotation(angle);
        break;
      case LEFT_ARM:
        armorStand.setLeftArmRotation(angle);
        break;
      case RIGHT_LEG:
        armorStand.setRightLegRotation(angle);
        break;
      case LEFT_LEG:
        armorStand.setLeftLegRotation(angle);
        break;
      default:
        throw new IllegalArgumentException("Unknown part: " + this);
    }
  }

  public void set(ArmorStandEntity armorStand, EulerAngleParameter parameter, float value) {
    set(armorStand, parameter.set(get(armorStand), value));
  }

  public static PosePart fromString(String value) {
    return Arrays.stream(PosePart.values())
        .filter((flag) -> flag.id.equals(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown part: " + value));
  }
}
