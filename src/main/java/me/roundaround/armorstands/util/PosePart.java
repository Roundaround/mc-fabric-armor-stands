package me.roundaround.armorstands.util;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;

public enum PosePart {
  HEAD,
  BODY,
  RIGHT_ARM,
  LEFT_ARM,
  RIGHT_LEG,
  LEFT_LEG;

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
}
