package me.roundaround.armorstands.util;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Stack;
import java.util.UUID;

import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.ServerNetworking;
import me.roundaround.armorstands.util.actions.ArmorStandAction;
import me.roundaround.armorstands.util.actions.FlagAction;
import me.roundaround.armorstands.util.actions.MoveAction;
import me.roundaround.armorstands.util.actions.PoseAction;
import me.roundaround.armorstands.util.actions.RotateAction;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

public class ArmorStandEditor {
  private final ServerPlayerEntity player;
  private final ArmorStandEntity armorStand;
  private final SizeLimitedStack<ArmorStandAction> actions = new SizeLimitedStack<>(30);
  private final SizeLimitedStack<ArmorStandAction> undos = new SizeLimitedStack<>(30);

  private static final HashMap<UUID, PlayerEditors> EDITORS = new HashMap<>();

  public static ArmorStandEditor getEditor(ServerPlayerEntity player, ArmorStandEntity armorStand) {
    UUID playerUuid = player.getUuid();

    if (!EDITORS.containsKey(playerUuid)) {
      EDITORS.put(playerUuid, new PlayerEditors(player));
    }

    return EDITORS.get(playerUuid).get(armorStand);
  }

  public static void clearEditors(ServerPlayerEntity player) {
    if (!EDITORS.containsKey(player.getUuid())) {
      return;
    }
    EDITORS.remove(player.getUuid());
  }

  private ArmorStandEditor(ServerPlayerEntity player, ArmorStandEntity armorStand) {
    this.player = player;
    this.armorStand = armorStand;
  }

  public ArmorStandEntity getArmorStand() {
    return this.armorStand;
  }

  public void applyAction(ArmorStandAction action) {
    action.apply(this.armorStand);
    this.actions.push(action);
    this.undos.clear();
    ServerNetworking.sendClientUpdatePacket(this.player, this.armorStand);
  }

  public boolean undo() {
    if (this.actions.isEmpty()) {
      return false;
    }
    ArmorStandAction action = this.actions.pop();
    action.undo(this.armorStand);
    this.undos.push(action);
    ServerNetworking.sendClientUpdatePacket(this.player, this.armorStand);
    return true;
  }

  public boolean redo() {
    if (this.undos.isEmpty()) {
      return false;
    }
    ArmorStandAction action = this.undos.pop();
    action.apply(this.armorStand);
    this.actions.push(action);
    ServerNetworking.sendClientUpdatePacket(this.player, this.armorStand);
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

  private static class PlayerEditors {
    private final ServerPlayerEntity player;
    private final HashMap<UUID, ArmorStandEditor> editors = new HashMap<>();
    private final ArrayDeque<UUID> insertionOrder = new ArrayDeque<>();

    public PlayerEditors(ServerPlayerEntity player) {
      this.player = player;
    }

    public ArmorStandEditor get(ArmorStandEntity armorStand) {
      UUID uuid = armorStand.getUuid();
      if (!editors.containsKey(uuid)) {
        add(armorStand);
      }
      return editors.get(uuid);
    }

    private void add(ArmorStandEntity armorStand) {
      ArmorStandEditor editor = new ArmorStandEditor(player, armorStand);
      UUID uuid = armorStand.getUuid();

      editors.put(uuid, editor);
      insertionOrder.add(uuid);
      if (insertionOrder.size() > 10) {
        editors.remove(insertionOrder.remove());
      }
    }
  }
}
