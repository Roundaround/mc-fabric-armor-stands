package me.roundaround.armorstands.util.actions;

import java.util.List;

import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.util.Pose;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

public class HoldingAction extends ComboAction {
  private static final EulerAngle ROT_EMPTY = new EulerAngle(0f, 0f, 0f);
  private static final Vec3d POS_UPRIGHT_ITEM = new Vec3d(0.36, -1.41, -0.5625);
  private static final EulerAngle ROT_UPRIGHT_ITEM = new EulerAngle(-90f, 0f, 0f);
  private static final Vec3d POS_FLAT_ITEM = new Vec3d(0.385, -0.78, -0.295);
  private static final EulerAngle ROT_FLAT_ITEM = new EulerAngle(0f, 0f, 0f);
  private static final Vec3d POS_BLOCK = new Vec3d(0.5725, -0.655, 0.352);
  private static final EulerAngle ROT_BLOCK = new EulerAngle(-15f, 135f, 0f);
  private static final Vec3d POS_TOOL = new Vec3d(-0.17, -1.285, -0.44);
  private static final EulerAngle ROT_TOOL = new EulerAngle(-10f, 0f, -90f);

  private HoldingAction(ArmorStandEntity armorStand, boolean small, Vec3d pos, EulerAngle rot) {
    super(Text.translatable("armorstands.action.holding"), List.of(
        FlagAction.set(ArmorStandFlag.INVISIBLE, true),
        FlagAction.set(ArmorStandFlag.HIDE_BASE_PLATE, true),
        FlagAction.set(ArmorStandFlag.NAME, false),
        FlagAction.set(ArmorStandFlag.SMALL, small),
        SnapToGroundAction.standing(),
        MoveAction.local(small ? pos.multiply(0.5) : pos),
        PoseAction.fromPose(new Pose(
            ROT_EMPTY,
            ROT_EMPTY,
            rot,
            ROT_EMPTY,
            ROT_EMPTY,
            ROT_EMPTY))));
  }

  public static HoldingAction uprightItem(ArmorStandEntity armorStand) {
    return uprightItem(armorStand, false);
  }

  public static HoldingAction uprightItem(ArmorStandEntity armorStand, boolean small) {
    return new HoldingAction(armorStand, small, POS_UPRIGHT_ITEM, ROT_UPRIGHT_ITEM);
  }

  public static HoldingAction flatItem(ArmorStandEntity armorStand) {
    return flatItem(armorStand, false);
  }

  public static HoldingAction flatItem(ArmorStandEntity armorStand, boolean small) {
    return new HoldingAction(armorStand, small, POS_FLAT_ITEM, ROT_FLAT_ITEM);
  }

  public static HoldingAction block(ArmorStandEntity armorStand) {
    return block(armorStand, false);
  }

  public static HoldingAction block(ArmorStandEntity armorStand, boolean small) {
    return new HoldingAction(armorStand, small, POS_BLOCK, ROT_BLOCK);
  }

  public static HoldingAction tool(ArmorStandEntity armorStand) {
    return tool(armorStand, false);
  }

  public static HoldingAction tool(ArmorStandEntity armorStand, boolean small) {
    return new HoldingAction(armorStand, small, POS_TOOL, ROT_TOOL);
  }
}
