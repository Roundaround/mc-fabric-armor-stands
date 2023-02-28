package me.roundaround.armorstands.util.actions;

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

  public static AdjustPosAction relative(Direction direction, int amount) {
    return new AdjustPosAction(
        MoveAction.relative(new Vec3d(direction.getUnitVector()).multiply(getPixelAmount(amount)), true));
  }

  public static AdjustPosAction local(Direction direction, int amount, boolean localToPlayer) {
    return new AdjustPosAction(
        MoveAction.local(new Vec3d(direction.getUnitVector()).multiply(getBlockAmount(amount)), localToPlayer));
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

  private static double getPixelAmount(int amount) {
    // 1, 3, and 8 pixels
    switch (amount) {
      case 2:
        return 0.1875;
      case 3:
        return 0.5;
      default:
        return 0.0625;
    }
  }

  private static double getBlockAmount(int amount) {
    // 1/4, 1/3, and 1 block
    switch (amount) {
      case 2:
        return 1d / 3d;
      case 3:
        return 1;
      default:
        return 0.25;
    }
  }
}
