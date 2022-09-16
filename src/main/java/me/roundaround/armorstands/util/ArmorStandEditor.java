package me.roundaround.armorstands.util;

import java.util.Optional;
import java.util.Stack;

import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.util.actions.ArmorStandAction;
import me.roundaround.armorstands.util.actions.FlagAction;
import me.roundaround.armorstands.util.actions.MoveAction;
import me.roundaround.armorstands.util.actions.PoseAction;
import me.roundaround.armorstands.util.actions.RotateAction;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArmorStandEditor {
  private final ArmorStandEntity armorStand;
  private final SizeLimitedStack<ArmorStandAction> actions = new SizeLimitedStack<>(30);
  private final SizeLimitedStack<ArmorStandAction> undos = new SizeLimitedStack<>(30);

  public ArmorStandEditor(ArmorStandEntity armorStand) {
    this.armorStand = armorStand;
  }

  public ArmorStandEntity getArmorStand() {
    return armorStand;
  }

  public void applyAction(ArmorStandAction action) {
    action.apply(armorStand);
    actions.push(action);
    undos.clear();
  }

  public boolean undo() {
    if (actions.isEmpty()) {
      return false;
    }
    ArmorStandAction action = actions.pop();
    action.undo(armorStand);
    undos.push(action);
    return true;
  }

  public boolean redo() {
    if (undos.isEmpty()) {
      return false;
    }
    ArmorStandAction action = undos.pop();
    action.apply(armorStand);
    actions.push(action);
    return true;
  }

  public void movePos(Direction direction, int pixels) {
    movePos(new Vec3d(direction.getUnitVector()).multiply(pixels * 0.0625), true);
  }

  public void movePos(Vec3d amount) {
    movePos(amount, false);
  }

  public void movePos(Vec3d amount, boolean roundToPixel) {
    applyAction(MoveAction.relative(amount, roundToPixel));
  }

  public Vec3d getCornerPos() {
    Vec3d position = armorStand.getPos();
    return new Vec3d(Math.floor(position.x), position.y, Math.floor(position.z));
  }

  public Vec3d getCenterPos() {
    Vec3d position = armorStand.getPos();
    return new Vec3d(Math.round(position.x + 0.5) - 0.5, position.y, Math.round(position.z + 0.5) - 0.5);
  }

  public Optional<Vec3d> getStandingPos() {
    return getGroundPos(false);
  }

  public Optional<Vec3d> getStandingPos(boolean hasBasePlate) {
    return getGroundPos(false, hasBasePlate);
  }

  public Optional<Vec3d> getGroundPos(boolean sitting) {
    return getGroundPos(sitting, !armorStand.shouldHideBasePlate());
  }

  public Optional<Vec3d> getGroundPos(boolean sitting, boolean hasBasePlate) {
    Vec3d position = armorStand.getPos();

    World world = armorStand.getWorld();
    BlockPos blockPos = armorStand.getBlockPos().up(2);
    boolean failed = false;

    while (!world.isTopSolid(blockPos.down(), armorStand)) {
      blockPos = blockPos.down();

      if (world.isOutOfHeightLimit(blockPos)) {
        failed = true;
        break;
      }
    }

    if (failed) {
      return Optional.empty();
    }

    Vec3d newPosition = new Vec3d(position.x, blockPos.getY(), position.z);

    if (sitting) {
      newPosition = newPosition.subtract(0, 11 * 0.0625, 0);
    }

    if (!hasBasePlate) {
      newPosition = newPosition.subtract(0, 0.0625, 0);
    }

    return Optional.of(newPosition);
  }

  public void setPos(double x, double y, double z) {
    setPos(new Vec3d(x, y, z));
  }

  public void setPos(Vec3d position) {
    applyAction(MoveAction.absolute(position));
  }

  public void rotate(float rotation) {
    applyAction(RotateAction.relative(rotation));
  }

  public void setRotation(float rotation) {
    applyAction(RotateAction.absolute(rotation));
  }

  public void toggleFlag(ArmorStandFlag flag) {
    applyAction(FlagAction.toggle(flag));
  }

  public void setFlag(ArmorStandFlag flag, boolean value) {
    applyAction(FlagAction.set(flag, value));
  }

  public void setPose(Pose pose) {
    applyAction(PoseAction.fromPose(pose));
  }

  public void setPose(
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg) {
    setPose(new Pose(head, body, rightArm, leftArm, rightLeg, leftLeg));
  }

  private static class SizeLimitedStack<T> extends Stack<T> {
    private final int limit;

    public SizeLimitedStack(int limit) {
      this.limit = limit;
    }

    @Override
    public T push(T obj) {
      while (size() >= limit) {
        remove(0);
      }
      return super.push(obj);
    }
  }
}
