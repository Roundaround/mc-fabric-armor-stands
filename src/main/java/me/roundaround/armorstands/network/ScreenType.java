package me.roundaround.armorstands.network;

import io.netty.buffer.ByteBuf;
import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.asset.icon.CustomIcon;
import me.roundaround.roundalib.asset.icon.Icon;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

import java.util.Arrays;
import java.util.function.IntFunction;

public enum ScreenType {
  UTILITIES(0, "utilities", new CustomIcon("flag", 20)),
  MOVE(1, "move", BuiltinIcon.MOVE_18),
  ROTATE(2, "rotate", BuiltinIcon.ROTATE_18),
  POSE(3, "pose", BuiltinIcon.SLIDERS_18),
  PRESETS(4, "presets", new CustomIcon("pose", 20)),
  INVENTORY(5, "inventory", new CustomIcon("inventory", 20));

  public static final IntFunction<ScreenType> ID_TO_VALUE_FUNCTION = ValueLists.createIdToValueFunction(
      ScreenType::getId, values(), ValueLists.OutOfBoundsHandling.ZERO);
  public static final PacketCodec<ByteBuf, ScreenType> PACKET_CODEC = PacketCodecs.indexed(
      ID_TO_VALUE_FUNCTION, ScreenType::getId);

  private final int id;
  private final String name;
  private final Icon icon;

  ScreenType(int id, String name, Icon icon) {
    this.id = id;
    this.name = name;
    this.icon = icon;
  }

  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public Icon getIcon() {
    return this.icon;
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
