package me.roundaround.armorstands.util.actions;

import java.util.Optional;

import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class AdjustPoseAction implements ArmorStandAction {
  private final PosePart part;
  private final EulerAngleParameter parameter;
  private final float argument;
  private final boolean absolute;
  private final boolean round;
  private Optional<Float> originalValue = Optional.empty();

  private AdjustPoseAction(
      PosePart part,
      EulerAngleParameter parameter,
      float argument,
      boolean absolute,
      boolean round) {
    this.part = part;
    this.parameter = parameter;
    this.argument = argument;
    this.absolute = absolute;
    this.round = round;
  }

  public static AdjustPoseAction absolute(
      PosePart part,
      EulerAngleParameter parameter,
      float argument) {
    return new AdjustPoseAction(part, parameter, argument, true, false);
  }

  @Override
  public Text getName(ArmorStandEntity armorStand) {
    return Text.translatable("armorstands.action.adjustPose");
  }

  @Override
  public void apply(PlayerEntity player, ArmorStandEntity armorStand) {
    this.originalValue = Optional.of(this.parameter.get(this.part.get(armorStand)));

    float value = this.argument;

    if (!this.absolute) {
      value += this.originalValue.get();
    }

    if (this.round) {
      value = Math.round(value);
    }

    this.part.set(armorStand, this.parameter, value);
  }

  @Override
  public void undo(PlayerEntity player, ArmorStandEntity armorStand) {
    if (this.originalValue.isEmpty()) {
      return;
    }
    this.part.set(armorStand, this.parameter, this.originalValue.get());
  }
}
