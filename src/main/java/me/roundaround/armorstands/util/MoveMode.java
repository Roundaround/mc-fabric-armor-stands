package me.roundaround.armorstands.util;

import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public enum MoveMode {
  RELATIVE("relative", false, false),
  LOCAL_TO_STAND("stand", true, false),
  LOCAL_TO_PLAYER("player", true, true);

  private final String id;
  private final boolean local;
  private final boolean player;

  private MoveMode(String id, boolean local, boolean player) {
    this.id = id;
    this.local = local;
    this.player = player;
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
    switch (this) {
      case RELATIVE:
        return MoveUnits.PIXELS;
      case LOCAL_TO_STAND:
      case LOCAL_TO_PLAYER:
        return MoveUnits.BLOCKS;
    }

    return MoveUnits.PIXELS;
  }

  public Text getOptionValueText() {
    return Text.translatable("armorstands.move.mode." + this.id);
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
      if (mode.getId().equals(id)) {
        return mode;
      }
    }

    return RELATIVE;
  }
}
