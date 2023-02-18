package me.roundaround.armorstands.util;

import net.minecraft.util.math.EulerAngle;

public enum EulerAngleParameter {
  PITCH,
  YAW,
  ROLL;

  public float get(EulerAngle angle) {
    switch (this) {
      case PITCH:
        return angle.getPitch();
      case YAW:
        return angle.getYaw();
      case ROLL:
        return angle.getRoll();
      default:
        throw new IllegalArgumentException("Unknown parameter: " + this);
    }
  }

  public EulerAngle set(EulerAngle angle, float value) {
    switch (this) {
      case PITCH:
        return new EulerAngle(value, angle.getYaw(), angle.getRoll());
      case YAW:
        return new EulerAngle(angle.getPitch(), value, angle.getRoll());
      case ROLL:
        return new EulerAngle(angle.getPitch(), angle.getYaw(), value);
      default:
        throw new IllegalArgumentException("Unknown parameter: " + this);
    }
  }
}
