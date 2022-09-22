package me.roundaround.armorstands.util.actions;

import me.roundaround.armorstands.util.Pose;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class PoseAction extends SimpleArmorStandAction<Pose> {
  private PoseAction(Pose pose) {
    super(pose);
  }

  public static PoseAction fromPose(Pose pose) {
    return new PoseAction(pose);
  }

  @Override
  protected Pose get(ArmorStandEntity armorStand) {
    return new Pose(armorStand);
  }
}
