package me.roundaround.armorstands.util.actions;

import me.roundaround.armorstands.util.MoveUnits;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AdjustPosAction implements ArmorStandAction {
  private final MoveAction moveAction;

  private AdjustPosAction(MoveAction moveAction) {
    this.moveAction = moveAction;
  }

  public static AdjustPosAction relative(Direction direction, int amount, MoveUnits units) {
    return new AdjustPosAction(
        MoveAction.relative(
            new Vec3d(direction.getUnitVector()).multiply(units.getAmount(amount)),
            true));
  }

  public static AdjustPosAction local(Direction direction, int amount, MoveUnits units, boolean localToPlayer) {
    return new AdjustPosAction(
        MoveAction.local(
            new Vec3d(direction.getUnitVector()).multiply(units.getAmount(amount)),
            localToPlayer));
  }

  @Override
  public Text getName(ArmorStandEntity armorStand) {
    return this.moveAction.getName(armorStand);
  }

  @Override
  public void apply(PlayerEntity player, ArmorStandEntity armorStand) {
    this.moveAction.apply(player, armorStand);
  }

  @Override
  public void undo(PlayerEntity player, ArmorStandEntity armorStand) {
    this.moveAction.undo(player, armorStand);
  }
}
