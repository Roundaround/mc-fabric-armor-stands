package me.roundaround.armorstands.util;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;

public class Pose {
  private final EulerAngle head;
  private final EulerAngle body;
  private final EulerAngle rightArm;
  private final EulerAngle leftArm;
  private final EulerAngle rightLeg;
  private final EulerAngle leftLeg;

  public Pose(ArmorStandEntity armorStand) {
    this(
        armorStand.getHeadRotation(),
        armorStand.getBodyRotation(),
        armorStand.getRightArmRotation(),
        armorStand.getLeftArmRotation(),
        armorStand.getRightLegRotation(),
        armorStand.getLeftLegRotation());
  }

  public Pose(
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg) {
    this.head = head;
    this.body = body;
    this.rightArm = rightArm;
    this.leftArm = leftArm;
    this.rightLeg = rightLeg;
    this.leftLeg = leftLeg;
  }

  public void apply(ArmorStandEntity armorStand) {
    armorStand.setHeadRotation(head);
    armorStand.setBodyRotation(body);
    armorStand.setRightArmRotation(rightArm);
    armorStand.setLeftArmRotation(leftArm);
    armorStand.setRightLegRotation(rightLeg);
    armorStand.setLeftLegRotation(leftLeg);
  }
}
