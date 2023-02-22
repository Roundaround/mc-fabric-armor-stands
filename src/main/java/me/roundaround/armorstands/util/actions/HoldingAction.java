package me.roundaround.armorstands.util.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.util.ArmorStandHelper;
import me.roundaround.armorstands.util.Pose;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

public class HoldingAction extends ComboAction {
  private static final EulerAngle ROT_EMPTY = new EulerAngle(0.0f, 0.0f, 0.0f);
  private static final Vec3d POS_FLOATING_ITEM = new Vec3d(0.36, -1.41, -0.5625);
  private static final EulerAngle ROT_FLOATING_ITEM = new EulerAngle(-90.0f, 0.0f, 0.0f);
  private static final Vec3d POS_FLAT_ITEM = new Vec3d(0.385, -0.78, -0.295);
  private static final EulerAngle ROT_FLAT_ITEM = new EulerAngle(0.0f, 0.0f, 0.0f);
  private static final Vec3d POS_BLOCK = new Vec3d(0.5725, -0.655, 0.352);
  private static final EulerAngle ROT_BLOCK = new EulerAngle(-15.0f, 135.0f, 0.0f);
  private static final Vec3d POS_TOOL = new Vec3d(-0.17, -1.285, -0.44);
  private static final EulerAngle ROT_TOOL = new EulerAngle(-10.0f, 0.0f, -90.0f);

  private HoldingAction(Collection<ArmorStandAction> actions) {
    super(Text.translatable("armorstands.action.holding"), actions);
  }

  private static HoldingAction create(ArmorStandEntity armorStand, boolean small, Vec3d pos, EulerAngle rot) {
    ArrayList<ArmorStandAction> actions = new ArrayList<>();
    actions.add(FlagAction.set(ArmorStandFlag.VISIBLE, true));
    actions.add(FlagAction.set(ArmorStandFlag.BASE, false));
    actions.add(FlagAction.set(ArmorStandFlag.GRAVITY, false));
    actions.add(FlagAction.set(ArmorStandFlag.NAME, false));
    actions.add(FlagAction.set(ArmorStandFlag.SMALL, small));

    Optional<Vec3d> maybeGround = ArmorStandHelper.getStandingPos(armorStand);
    if (maybeGround.isPresent()) {
      Vec3d position = maybeGround.get();
      position.add(small ? pos.multiply(0.5) : pos);
      actions.add(MoveAction.absolute(position));
    } else {
      actions.add(MoveAction.relative(small ? pos.multiply(0.5) : pos));
    }

    actions.add(PoseAction.fromPose(new Pose(
        ROT_EMPTY,
        ROT_EMPTY,
        rot,
        ROT_EMPTY,
        ROT_EMPTY,
        ROT_EMPTY)));

    return new HoldingAction(actions);
  }

  public static HoldingAction floatingItem(ArmorStandEntity armorStand) {
    return floatingItem(armorStand, false);
  }

  public static HoldingAction floatingItem(ArmorStandEntity armorStand, boolean small) {
    return create(armorStand, small, POS_FLOATING_ITEM, ROT_FLOATING_ITEM);
  }

  public static HoldingAction flatItem(ArmorStandEntity armorStand) {
    return flatItem(armorStand, false);
  }

  public static HoldingAction flatItem(ArmorStandEntity armorStand, boolean small) {
    return create(armorStand, small, POS_FLAT_ITEM, ROT_FLAT_ITEM);
  }

  public static HoldingAction block(ArmorStandEntity armorStand) {
    return block(armorStand, false);
  }

  public static HoldingAction block(ArmorStandEntity armorStand, boolean small) {
    return create(armorStand, small, POS_BLOCK, ROT_BLOCK);
  }

  public static HoldingAction tool(ArmorStandEntity armorStand) {
    return tool(armorStand, false);
  }

  public static HoldingAction tool(ArmorStandEntity armorStand, boolean small) {
    return create(armorStand, small, POS_TOOL, ROT_TOOL);
  }
}
