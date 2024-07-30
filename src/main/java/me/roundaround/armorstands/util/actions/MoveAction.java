package me.roundaround.armorstands.util.actions;

import me.roundaround.armorstands.util.ArmorStandHelper;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class MoveAction implements ArmorStandAction {
  private final Vec3d argument;
  private final MoveType moveType;
  private final boolean roundToPixel;
  private final boolean localToPlayer;
  private Optional<Vec3d> originalPosition = Optional.empty();

  private MoveAction(Vec3d argument, MoveType moveType, boolean roundToPixel, boolean localToPlayer) {
    this.argument = argument;
    this.moveType = moveType;
    this.roundToPixel = roundToPixel;
    this.localToPlayer = localToPlayer;
  }

  public static MoveAction absolute(Vec3d position) {
    return new MoveAction(position, MoveType.ABSOLUTE, false, false);
  }

  public static MoveAction absolute(double x, double y, double z) {
    return absolute(new Vec3d(x, y, z));
  }

  public static MoveAction relative(Vec3d position, boolean roundToPixel) {
    return new MoveAction(position, MoveType.RELATIVE, roundToPixel, false);
  }

  public static MoveAction relative(double x, double y, double z, boolean roundToPixel) {
    return relative(new Vec3d(x, y, z), roundToPixel);
  }

  public static MoveAction relative(Vec3d position) {
    return relative(position, false);
  }

  public static MoveAction relative(double x, double y, double z) {
    return relative(x, y, z, false);
  }

  public static MoveAction local(Vec3d position) {
    return new MoveAction(position, MoveType.LOCAL, false, false);
  }

  public static MoveAction local(double x, double y, double z) {
    return local(new Vec3d(x, y, z));
  }

  public static MoveAction local(Vec3d position, boolean localToPlayer) {
    return new MoveAction(position, MoveType.LOCAL, false, localToPlayer);
  }

  public static MoveAction local(double x, double y, double z, boolean localToPlayer) {
    return local(new Vec3d(x, y, z), localToPlayer);
  }

  @Override
  public Text getName(ArmorStandEntity armorStand) {
    return Text.translatable("armorstands.action.move");
  }

  @Override
  public void apply(PlayerEntity player, ArmorStandEntity armorStand) {
    this.originalPosition = Optional.of(armorStand.getPos());

    Vec3d position = switch (this.moveType) {
      case ABSOLUTE -> this.argument;
      case RELATIVE -> this.originalPosition.get().add(this.argument);
      case LOCAL -> this.originalPosition.get()
          .add(ArmorStandHelper.localToRelative(this.localToPlayer ? player : armorStand, this.argument));
    };

    if (this.roundToPixel) {
      double x = Math.round(position.x * 16) / 16.0;
      double y = Math.round(position.y * 16) / 16.0;
      double z = Math.round(position.z * 16) / 16.0;
      position = new Vec3d(x, y, z);
    }

    setPosition(armorStand, position);
  }

  @Override
  public void undo(PlayerEntity player, ArmorStandEntity armorStand) {
    if (this.originalPosition.isEmpty()) {
      return;
    }

    setPosition(armorStand, this.originalPosition.get());
  }

  public static void setPosition(ArmorStandEntity armorStand, Vec3d position) {
    setPosition(armorStand, position.x, position.y, position.z);
  }

  public static void setPosition(ArmorStandEntity armorStand, double x, double y, double z) {
    // Adjust y pos up just the tiniest bit if gravity is enabled to prevent the
    // stand from falling through the floor when rounding errors occur.
    boolean hasGravity = !armorStand.hasNoGravity();
    double currY = armorStand.getY();
    if (hasGravity && Math.abs(y - currY) > MathHelper.EPSILON) {
      y += 0.001;
    }

    armorStand.updateTrackedPosition(x, y, z);
    armorStand.setPosition(new Vec3d(x, y, z));
  }

  public enum MoveType {
    ABSOLUTE("absolute"), RELATIVE("relative"), LOCAL("local");

    private final String id;

    MoveType(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }

    public static MoveType fromId(String id) {
      for (MoveType type : values()) {
        if (type.id.equals(id)) {
          return type;
        }
      }

      return ABSOLUTE;
    }
  }
}
