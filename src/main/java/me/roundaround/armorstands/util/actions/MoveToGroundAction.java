package me.roundaround.armorstands.util.actions;

import java.util.Optional;

import me.roundaround.armorstands.util.ArmorStandHelper;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class MoveToGroundAction implements ArmorStandAction {
  private Optional<Vec3d> originalPosition = Optional.empty();
  private boolean sitting;

  private MoveToGroundAction(boolean sitting) {
    this.sitting = sitting;
  }

  public static MoveToGroundAction standing() {
    return new MoveToGroundAction(false);
  }

  public static MoveToGroundAction sitting() {
    return new MoveToGroundAction(true);
  }

  public static MoveToGroundAction create(boolean sitting) {
    return new MoveToGroundAction(sitting);
  }

  @Override
  public Text getName(ArmorStandEntity armorStand) {
    return Text.translatable("armorstands.action.moveToGround");
  }

  @Override
  public void apply(PlayerEntity player, ArmorStandEntity armorStand) {
    Optional<Vec3d> maybeGround = ArmorStandHelper.getGroundPos(
        armorStand,
        this.sitting);

    if (maybeGround.isPresent()) {
      originalPosition = Optional.of(armorStand.getPos());
      MoveAction.setPosition(armorStand, maybeGround.get());
    }
  }

  @Override
  public void undo(PlayerEntity player, ArmorStandEntity armorStand) {
    if (originalPosition.isEmpty()) {
      return;
    }

    MoveAction.setPosition(armorStand, originalPosition.get());
  }
}
