package me.roundaround.armorstands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

import java.util.Arrays;
import java.util.function.IntFunction;

public enum ScreenType {
  UTILITIES(0, "utilities"),
  MOVE(1, "move"),
  ROTATE(2, "rotate"),
  POSE(3, "pose"),
  PRESETS(4, "presets"),
  INVENTORY(5, "inventory");

  public static final IntFunction<ScreenType> ID_TO_VALUE_FUNCTION = ValueLists.createIdToValueFunction(
      ScreenType::getId, values(), ValueLists.OutOfBoundsHandling.ZERO);
  public static final PacketCodec<ByteBuf, ScreenType> PACKET_CODEC = PacketCodecs.indexed(
      ID_TO_VALUE_FUNCTION, ScreenType::getId);

  private final int id;
  private final String name;

  ScreenType(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public int getUIndex() {
    return this.id;
  }

  public Text getDisplayName() {
    return Text.translatable("armorstands.screen." + name);
  }

  public boolean usesInventory() {
    return this == INVENTORY;
  }

  public ScreenType next() {
    int index = ordinal() + 1;
    if (index >= values().length) {
      index = 0;
    }
    return values()[index];
  }

  public ScreenType previous() {
    int index = ordinal() - 1;
    if (index < 0) {
      index = values().length - 1;
    }
    return values()[index];
  }

  public static ScreenType fromId(String id) {
    return Arrays.stream(values()).filter(type -> type.getName().equals(id)).findFirst().orElse(null);
  }
}
