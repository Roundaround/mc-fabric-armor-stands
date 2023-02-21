package me.roundaround.armorstands.network;

import java.util.Arrays;

import net.minecraft.text.Text;
import net.minecraft.util.math.EulerAngle;

public enum EulerAngleParameter {
  PITCH("pitch"),
  YAW("yaw"),
  ROLL("roll");

  private final String id;

  private EulerAngleParameter(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }

  public Text getDisplayName() {
    return Text.translatable("armorstands.parameter." + id);
  }

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

  public static EulerAngleParameter fromString(String string) {
    return Arrays.stream(values())
        .filter(parameter -> parameter.id.equals(string))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown parameter: " + string));
  }
}
