package me.roundaround.armorstands.util;

import java.util.HashMap;
import java.util.Stack;
import java.util.UUID;

import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
import me.roundaround.armorstands.util.actions.AdjustPoseAction;
import me.roundaround.armorstands.util.actions.ArmorStandAction;
import me.roundaround.armorstands.util.actions.FlagAction;
import me.roundaround.armorstands.util.actions.MoveAction;
import me.roundaround.armorstands.util.actions.PoseAction;
import me.roundaround.armorstands.util.actions.RotateAction;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

public class ArmorStandEditor {
  private static HashMap<UUID, Pair<UUID, ArmorStandEditor>> editors = new HashMap<>();

  private final ArmorStandEntity armorStand;
  private final SizeLimitedStack<ArmorStandAction> actions = new SizeLimitedStack<>(30);
  private final SizeLimitedStack<ArmorStandAction> undos = new SizeLimitedStack<>(30);

  public static ArmorStandEditor get(ServerPlayerEntity player, ArmorStandEntity armorStand) {
    UUID uuid = player.getUuid();

    if (!editors.containsKey(uuid)) {
      editors.put(uuid, new Pair<>(armorStand.getUuid(), new ArmorStandEditor(armorStand)));
    }

    Pair<UUID, ArmorStandEditor> pair = editors.get(uuid);
    if (!pair.getLeft().equals(armorStand.getUuid())) {
      editors.put(uuid, new Pair<>(armorStand.getUuid(), new ArmorStandEditor(armorStand)));
    }

    return editors.get(uuid).getRight();
  }

  public static void remove(ServerPlayerEntity player) {
    editors.remove(player.getUuid());
  }

  private ArmorStandEditor(ArmorStandEntity armorStand) {
    this.armorStand = armorStand;
  }

  public ArmorStandEntity getArmorStand() {
    return this.armorStand;
  }

  public void applyAction(ArmorStandAction action) {
    if (action == null) {
      return;
    }
    action.apply(this.armorStand);
    this.actions.push(action);
    this.undos.clear();
  }

  public boolean undo() {
    if (this.actions.isEmpty()) {
      return false;
    }

    ArmorStandAction action = this.actions.pop();
    if (action == null) {
      return false;
    }

    action.undo(this.armorStand);
    this.undos.push(action);
    return true;
  }

  public boolean redo() {
    if (this.undos.isEmpty()) {
      return false;
    }

    ArmorStandAction action = this.undos.pop();
    if (action == null) {
      return false;
    }

    action.apply(this.armorStand);
    this.actions.push(action);
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

  public void adjustPose(PosePart part, EulerAngleParameter parameter, float amount) {
    applyAction(AdjustPoseAction.absolute(part, parameter, amount));
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
