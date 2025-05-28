package me.roundaround.armorstands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.EulerAngle;

import java.util.function.IntFunction;

public enum EulerAngleParameter {
  PITCH(0, "pitch"), YAW(1, "yaw"), ROLL(2, "roll");

  public static final IntFunction<EulerAngleParameter> ID_TO_VALUE_FUNCTION =
      ValueLists.createIdToValueFunction(EulerAngleParameter::getIndex,
      values(),
      ValueLists.OutOfBoundsHandling.CLAMP
  );
  public static final PacketCodec<ByteBuf, EulerAngleParameter> PACKET_CODEC = PacketCodecs.indexed(
      ID_TO_VALUE_FUNCTION,
      EulerAngleParameter::getIndex
  );

  private final int index;
  private final String id;

  EulerAngleParameter(int index, String id) {
    this.index = index;
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }

  public int getIndex() {
    return this.index;
  }

  public Text getDisplayName() {
    return Text.translatable("armorstands.parameter." + id);
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
}
