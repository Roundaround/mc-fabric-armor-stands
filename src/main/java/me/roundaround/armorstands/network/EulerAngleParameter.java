package me.roundaround.armorstands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.EulerAngle;

import java.util.Arrays;
import java.util.function.IntFunction;

public enum EulerAngleParameter {
  PITCH(0, "pitch"), YAW(1, "yaw"), ROLL(2, "roll");

  public static final IntFunction<EulerAngleParameter> ID_TO_VALUE_FUNCTION = ValueLists.createIdToValueFunction(
      EulerAngleParameter::getId, values(), ValueLists.OutOfBoundsHandling.CLAMP);
  public static final PacketCodec<ByteBuf, EulerAngleParameter> PACKET_CODEC = PacketCodecs.indexed(
      ID_TO_VALUE_FUNCTION, EulerAngleParameter::getId);

  private final int id;
  private final String name;

  EulerAngleParameter(int id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  public int getId() {
    return this.id;
  }

  public Text getDisplayName() {
    return Text.translatable("armorstands.parameter." + name);
  }

  public float get(EulerAngle angle) {
    return switch (this) {
      case PITCH -> angle.getPitch();
      case YAW -> angle.getYaw();
      case ROLL -> angle.getRoll();
    };
  }

  public EulerAngle set(EulerAngle angle, float value) {
    return switch (this) {
      case PITCH -> new EulerAngle(value, angle.getYaw(), angle.getRoll());
      case YAW -> new EulerAngle(angle.getPitch(), value, angle.getRoll());
      case ROLL -> new EulerAngle(angle.getPitch(), angle.getYaw(), value);
    };
  }

  public static EulerAngleParameter fromString(String string) {
    return Arrays.stream(values())
        .filter(parameter -> parameter.name.equals(string))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown parameter: " + string));
  }
}
