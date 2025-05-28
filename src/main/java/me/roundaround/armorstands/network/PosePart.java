package me.roundaround.armorstands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.EulerAngle;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public enum PosePart {
  HEAD(0, "head"),
  BODY(1, "body"),
  RIGHT_ARM(2, "rightArm"),
  LEFT_ARM(3, "leftArm"),
  RIGHT_LEG(4, "rightLeg"),
  LEFT_LEG(5, "leftLeg");

  public static final IntFunction<PosePart> ID_TO_VALUE_FUNCTION = ValueLists.createIdToValueFunction(
      PosePart::getIndex,
      values(),
      ValueLists.OutOfBoundsHandling.CLAMP
  );
  public static final PacketCodec<ByteBuf, PosePart> PACKET_CODEC = PacketCodecs.indexed(
      ID_TO_VALUE_FUNCTION,
      PosePart::getIndex
  );

  private final int index;
  private final String id;

  PosePart(int index, String id) {
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
    return Text.translatable("armorstands.part." + id);
  }

  public EulerAngle get(ArmorStandEntity armorStand) {
    return switch (this) {
      case HEAD -> armorStand.getHeadRotation();
      case BODY -> armorStand.getBodyRotation();
      case RIGHT_ARM -> armorStand.getRightArmRotation();
      case LEFT_ARM -> armorStand.getLeftArmRotation();
      case RIGHT_LEG -> armorStand.getRightLegRotation();
      case LEFT_LEG -> armorStand.getLeftLegRotation();
    };
  }

  public void set(ArmorStandEntity armorStand, EulerAngle angle) {
    Consumer<EulerAngle> consumer = switch (this) {
      case HEAD -> armorStand::setHeadRotation;
      case BODY -> armorStand::setBodyRotation;
      case RIGHT_ARM -> armorStand::setRightArmRotation;
      case LEFT_ARM -> armorStand::setLeftArmRotation;
      case RIGHT_LEG -> armorStand::setRightLegRotation;
      case LEFT_LEG -> armorStand::setLeftLegRotation;
    };
    consumer.accept(angle);
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
