package me.roundaround.armorstands.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.Direction;

import java.util.function.IntFunction;

public enum MoveMode {
  RELATIVE(0, "relative", false, false),
  LOCAL_TO_STAND(1, "stand", true, false),
  LOCAL_TO_PLAYER(2, "player", true, true);

  public static final IntFunction<MoveMode> ID_TO_VALUE_FUNCTION = ValueLists.createIdToValueFunction(
      MoveMode::getId, values(), ValueLists.OutOfBoundsHandling.CLAMP);
  public static final PacketCodec<ByteBuf, MoveMode> PACKET_CODEC = PacketCodecs.indexed(
      ID_TO_VALUE_FUNCTION, MoveMode::getId);

  private final int id;
  private final String name;
  private final boolean local;
  private final boolean player;

  MoveMode(int id, String name, boolean local, boolean player) {
    this.id = id;
    this.name = name;
    this.local = local;
    this.player = player;
  }

  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public boolean isLocal() {
    return this.local;
  }

  public boolean isLocalToPlayer() {
    return this.player;
  }

  public MoveUnits getDefaultUnits() {
    return switch (this) {
      case RELATIVE -> MoveUnits.PIXELS;
      case LOCAL_TO_STAND, LOCAL_TO_PLAYER -> MoveUnits.BLOCKS;
    };

  }

  public Text getOptionValueText() {
    return Text.translatable("armorstands.move.mode." + this.name);
  }

  public Text getDirectionText(Direction direction) {
    if (this.equals(RELATIVE)) {
      return Text.translatable("armorstands.move." + direction.getName());
    } else {
      return Text.translatable("armorstands.move.local." + direction.getName());
    }
  }

  public static Text getOptionLabelText() {
    return Text.translatable("armorstands.move.mode");
  }

  public static MoveMode fromId(String id) {
    for (MoveMode mode : MoveMode.values()) {
      if (mode.getName().equals(id)) {
        return mode;
      }
    }

    return RELATIVE;
  }
}
