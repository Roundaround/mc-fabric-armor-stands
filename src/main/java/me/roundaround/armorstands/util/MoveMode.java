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
      MoveMode::getIndex,
      values(),
      ValueLists.OutOfBoundsHandling.CLAMP
  );
  public static final PacketCodec<ByteBuf, MoveMode> PACKET_CODEC = PacketCodecs.indexed(
      ID_TO_VALUE_FUNCTION,
      MoveMode::getIndex
  );

  private final int index;
  private final String id;
  private final boolean local;
  private final boolean player;

  MoveMode(int index, String id, boolean local, boolean player) {
    this.index = index;
    this.id = id;
    this.local = local;
    this.player = player;
  }

  public int getIndex() {
    return this.index;
  }

  public String getId() {
    return this.id;
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
    return Text.translatable("armorstands.move.mode." + this.id);
  }

  public Text getDirectionText(Direction direction) {
    if (this.equals(RELATIVE)) {
      return Text.translatable("armorstands.move." + direction.getId());
    } else {
      return Text.translatable("armorstands.move.local." + direction.getId());
    }
  }

  public static Text getOptionLabelText() {
    return Text.translatable("armorstands.move.mode");
  }

  public static MoveMode fromId(String id) {
    for (MoveMode mode : MoveMode.values()) {
      if (mode.getId().equals(id)) {
        return mode;
      }
    }

    return RELATIVE;
  }
}
