package me.roundaround.armorstands.network;

import io.netty.buffer.ByteBuf;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.mixin.ArmorStandEntityAccessor;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

public enum ArmorStandFlag {
  UNKNOWN(0, "unknown", false),
  HIDE_BASE_PLATE(1, "base", true),
  SHOW_ARMS(2, "arms", false),
  SMALL(3, "small", false),
  NO_GRAVITY(4, "gravity", true),
  INVISIBLE(5, "visible", false),
  NAME(6, "name", false),
  INVULNERABLE(7, "invulnerable", false),
  LOCK_INVENTORY(8, "inventory", false);

  public static final IntFunction<ArmorStandFlag> ID_TO_VALUE_FUNCTION = ValueLists.createIdToValueFunction(
      ArmorStandFlag::getId, values(), ValueLists.OutOfBoundsHandling.ZERO);
  public static final PacketCodec<ByteBuf, ArmorStandFlag> PACKET_CODEC = PacketCodecs.indexed(
      ID_TO_VALUE_FUNCTION, ArmorStandFlag::getId);

  // Magic number from Vanilla Tweaks armor stand data pack. ¯\_(ツ)_/¯
  private static final int ALL_SLOTS_DISABLED = 4144959;

  private final int id;
  private final String name;
  private final boolean invertControl;

  ArmorStandFlag(int id, String name, boolean invertControl) {
    this.id = id;
    this.name = name;
    this.invertControl = invertControl;
  }

  @Override
  public String toString() {
    return this.name;
  }

  public int getId() {
    return this.id;
  }

  public Text getDisplayName() {
    return Text.translatable("armorstands.flags." + this.name);
  }

  public boolean invertControl() {
    return this.invertControl;
  }

  public boolean getValue(ArmorStandEntity armorStand) {
    ArmorStandEntityAccessor accessor = (ArmorStandEntityAccessor) armorStand;

    return switch (this) {
      case HIDE_BASE_PLATE -> armorStand.shouldHideBasePlate();
      case SHOW_ARMS -> armorStand.shouldShowArms();
      case SMALL -> armorStand.isSmall();
      case NO_GRAVITY -> armorStand.hasNoGravity();
      case INVISIBLE -> armorStand.isInvisible();
      case NAME -> armorStand.isCustomNameVisible();
      case INVULNERABLE -> armorStand.isInvulnerable();
      case LOCK_INVENTORY -> accessor.getDisabledSlots() == ALL_SLOTS_DISABLED;
      default -> false;
    };
  }

  public void setValue(ArmorStandEntity armorStand, boolean value) {
    ArmorStandEntityAccessor accessor = (ArmorStandEntityAccessor) armorStand;

    switch (this) {
      case HIDE_BASE_PLATE:
        accessor.invokeSetHideBasePlate(value);
        break;
      case SHOW_ARMS:
        accessor.invokeSetShowArms(value);
        break;
      case SMALL:
        accessor.invokeSetSmall(value);
        break;
      case NO_GRAVITY:
        armorStand.setNoGravity(value);

        Vec3d vel = armorStand.getVelocity();
        armorStand.setVelocity(vel.x, 0, vel.z);

        if (!value) {
          armorStand.noClip = false;
          Vec3d pos = armorStand.getPos();
          armorStand.refreshPositionAndAngles(pos.x, pos.y + 0.01, pos.z, armorStand.getYaw(), armorStand.getPitch());
          armorStand.move(MovementType.SELF, new Vec3d(0, -0.009, 0));
        }
        break;
      case INVISIBLE:
        armorStand.setInvisible(value);
        break;
      case NAME:
        armorStand.setCustomNameVisible(value);
        break;
      case INVULNERABLE:
        armorStand.setInvulnerable(value);
        break;
      case LOCK_INVENTORY:
        accessor.setDisabledSlots(value ? ALL_SLOTS_DISABLED : 0);
        break;
      default:
        // Do nothing for unknown
        ArmorStandsMod.LOGGER.warn("Tried to set value to flag {}. Ignoring.", name());
    }
  }

  public static ArmorStandFlag fromString(String value) {
    return Arrays.stream(ArmorStandFlag.values())
        .filter((flag) -> flag.name.equals(value))
        .findFirst()
        .orElseGet(() -> {
          ArmorStandsMod.LOGGER.warn("Unknown flag id '{}'. Returning UNKNOWN.", value);
          return UNKNOWN;
        });
  }

  public static List<ArmorStandFlag> getFlags() {
    return Arrays.stream(values()).filter((flag) -> flag != UNKNOWN).toList();
  }
}
