package me.roundaround.armorstands.util.actions;

import me.roundaround.armorstands.util.Pose;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class PoseAction implements ArmorStandAction {
  private final Pose pose;
  private Pose originalPose;

  private PoseAction(Pose pose) {
    this.pose = pose;
  }

  public static PoseAction fromPose(Pose pose) {
    return new PoseAction(pose);
  }

  @Override
  public void apply(ArmorStandEntity armorStand) {
    originalPose = new Pose(armorStand);
    pose.apply(armorStand);
  }

  @Override
  public void undo(ArmorStandEntity armorStand) {
    if (originalPose == null) {
      return;
    }
    originalPose.apply(armorStand);
  }
}
