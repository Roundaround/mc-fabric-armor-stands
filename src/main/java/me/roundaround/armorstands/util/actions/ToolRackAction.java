package me.roundaround.armorstands.util.actions;

import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.util.Pose;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;

import java.util.ArrayList;
import java.util.Collection;

public class ToolRackAction extends ComboAction {
  private ToolRackAction(Collection<ArmorStandAction> actions) {
    super(Text.translatable("armorstands.action.toolRack"), actions);
  }

  public static ToolRackAction create(ArmorStandEntity armorStand) {
    BlockPos hookPos = findTripwireHook(armorStand);

    if (hookPos == null) {
      return null;
    }

    ArrayList<ArmorStandAction> actions = new ArrayList<>();

    actions.add(FlagAction.set(ArmorStandFlag.INVISIBLE, true));
    actions.add(FlagAction.set(ArmorStandFlag.HIDE_BASE_PLATE, true));
    actions.add(FlagAction.set(ArmorStandFlag.NO_GRAVITY, true));
    actions.add(FlagAction.set(ArmorStandFlag.NAME, false));
    actions.add(FlagAction.set(ArmorStandFlag.SMALL, false));
    actions.add(RotateAction.absolute(armorStand.getWorld()
        .getBlockState(hookPos)
        .get(TripwireHookBlock.FACING)
        .asRotation()));
    actions.add(ScaleAction.absolute(1f));
    actions.add(MoveAction.absolute(hookPos.getX() + 0.5, hookPos.getY() - 1, hookPos.getZ() + 0.5));
    actions.add(MoveAction.local(-0.17, 0.24, -0.05));
    actions.add(PoseAction.fromPose(new Pose(
        new EulerAngle(0f, 0.001f, 0f),
        new EulerAngle(0f, 0.001f, 0f),
        new EulerAngle(-100f, 90f, 180f),
        new EulerAngle(0f, 0f, 0f),
        new EulerAngle(0f, 0f, 0f),
        new EulerAngle(0f, 0f, 0f)
    )));

    return new ToolRackAction(actions);
  }

  private static BlockPos findTripwireHook(ArmorStandEntity armorStand) {
    BlockPos pos = armorStand.getBlockPos();
    BlockPos.Mutable mutable = new BlockPos.Mutable();

    for (int i = 1; i <= 3; i++) {
      mutable.set(pos.getX(), pos.getY() + i, pos.getZ());
      BlockState state = armorStand.getWorld().getBlockState(mutable);
      if (state.isOf(Blocks.TRIPWIRE_HOOK)) {
        return mutable.toImmutable();
      }
    }

    return null;
  }
}
