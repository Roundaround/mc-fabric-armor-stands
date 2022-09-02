package me.roundaround.armorstands.util.actions;

import java.util.Optional;

import me.roundaround.armorstands.util.Pose;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class PoseAction implements ArmorStandAction {
  private final Pose pose;
  private Optional<Pose> originalPose = Optional.empty();

  private PoseAction(Pose pose) {
    this.pose = pose;
  }

  public static PoseAction fromPose(Pose pose) {
    return new PoseAction(pose);
  }

  @Override
  public void apply(ArmorStandEntity armorStand) {
    originalPose = Optional.of(new Pose(armorStand));
    pose.apply(armorStand);
  }

  @Override
  public void undo(ArmorStandEntity armorStand) {
    if (originalPose.isEmpty()) {
      return;
    }
    originalPose.get().apply(armorStand);
  }
}
