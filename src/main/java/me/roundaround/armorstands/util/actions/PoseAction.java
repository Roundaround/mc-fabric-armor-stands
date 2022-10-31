package me.roundaround.armorstands.util.actions;

import me.roundaround.armorstands.util.Pose;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class PoseAction extends SimpleArmorStandAction<Pose> {
  private PoseAction(Pose pose) {
    super(pose);
  }

  public static PoseAction fromPose(Pose pose) {
    return new PoseAction(pose);
  }

  @Override
  public Text getName(ArmorStandEntity armorStand) {
    return Text.translatable("armorstands.action.pose");
  }

  @Override
  protected Pose get(ArmorStandEntity armorStand) {
    return new Pose(armorStand);
  }
}
