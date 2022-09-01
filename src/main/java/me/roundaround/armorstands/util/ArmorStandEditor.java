package me.roundaround.armorstands.util;

import java.util.Stack;

import me.roundaround.armorstands.util.actions.ArmorStandAction;
import me.roundaround.armorstands.util.actions.MoveAction;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ArmorStandEditor {
  private final ArmorStandEntity armorStand;
  private final SizeLimitedStack<ArmorStandAction> actions = new SizeLimitedStack<>(30);
  private final SizeLimitedStack<ArmorStandAction> undos = new SizeLimitedStack<>(30);

  public ArmorStandEditor(ArmorStandEntity armorStand) {
    this.armorStand = armorStand;
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
    MoveAction action = MoveAction.relative(amount, roundToPixel);
    action.apply(armorStand);
    actions.push(action);
  }

  public void alignHorizontalToEdge() {
    Vec3d position = armorStand.getPos();
    setPos(Math.floor(position.x), position.y, Math.floor(position.z));
  }

  public void alignHorizontalToCenter() {
    Vec3d position = armorStand.getPos();
    setPos(Math.round(position.x + 0.5) - 0.5, position.y, Math.round(position.z + 0.5) - 0.5);
  }

  public void snapToGround(boolean sitting) {
    Vec3d position = armorStand.getPos();
    position = new Vec3d(position.x, armorStand.getBlockY(), position.z);

    // TODO: Adjust for sitting

    setPos(position);
  }

  public void setPos(double x, double y, double z) {
    movePos(new Vec3d(x, y, z));
  }

  public void setPos(Vec3d position) {
    MoveAction action = MoveAction.absolute(position);
    action.apply(armorStand);
    actions.push(action);
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
