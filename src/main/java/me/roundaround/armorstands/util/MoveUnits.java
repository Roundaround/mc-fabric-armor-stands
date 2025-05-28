package me.roundaround.armorstands.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum MoveUnits {
  PIXELS(0, "pixels"), BLOCKS(1, "blocks");

  public static final IntFunction<MoveUnits> ID_TO_VALUE_FUNCTION = ValueLists.createIdToValueFunction(
      MoveUnits::getIndex,
      values(),
      ValueLists.OutOfBoundsHandling.CLAMP
  );
  public static final PacketCodec<ByteBuf, MoveUnits> PACKET_CODEC = PacketCodecs.indexed(
      ID_TO_VALUE_FUNCTION,
      MoveUnits::getIndex
  );

  private final int index;
  private final String id;

  MoveUnits(int index, String id) {
    this.index = index;
    this.id = id;
  }

  public int getIndex() {
    return this.index;
  }

  public String getId() {
    return this.id;
  }

  public Text getOptionValueText() {
    return Text.translatable("armorstands.move.units." + this.id);
  }

  public Text getButtonText(int amount) {
    return Text.translatable("armorstands.move." + this.id + "." + amount);
  }

  public double getAmount(int amount) {
    return switch (this) {
      case PIXELS -> getPixelAmount(amount);
      case BLOCKS -> getBlockAmount(amount);
    };
  }

  private static double getPixelAmount(int amount) {
    // 1, 3, and 8 pixels
    return switch (amount) {
      case 2 -> 0.1875;
      case 3 -> 0.5;
      default -> 0.0625;
    };
  }

  private static double getBlockAmount(int amount) {
    // 1/4, 1/3, and 1 block
    return switch (amount) {
      case 2 -> 1d / 3d;
      case 3 -> 1;
      default -> 0.25;
    };
  }

  public static Text getOptionLabelText() {
    return Text.translatable("armorstands.move.units");
  }

  public static MoveUnits fromId(String id) {
    for (MoveUnits units : MoveUnits.values()) {
      if (units.getId().equals(id)) {
        return units;
      }
    }

    return PIXELS;
  }
}
