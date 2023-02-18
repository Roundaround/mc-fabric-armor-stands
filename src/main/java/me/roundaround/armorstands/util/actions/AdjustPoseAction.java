package me.roundaround.armorstands.util.actions;

import java.util.Optional;

import me.roundaround.armorstands.util.EulerAngleParameter;
import me.roundaround.armorstands.util.PosePart;
import net.minecraft.entity.decoration.ArmorStandEntity;
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

  @Override
  public Text getName(ArmorStandEntity armorStand) {
    return Text.translatable("armorstands.action.adjustPose");
  }

  @Override
  public void apply(ArmorStandEntity armorStand) {
    originalValue = Optional.of(this.parameter.get(this.part.get(armorStand)));

    float value = this.argument;

    if (!this.absolute) {
      value += originalValue.get();
    }

    if (this.round) {
      value = Math.round(value);
    }

    this.part.set(armorStand, this.parameter.set(this.part.get(armorStand), value));
  }

  @Override
  public void undo(ArmorStandEntity armorStand) {
    if (originalValue.isEmpty()) {
      return;
    }
    this.part.set(armorStand, this.parameter.set(this.part.get(armorStand), originalValue.get()));
  }
}
