package me.roundaround.armorstands.util;

import java.util.Optional;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;

public class Pose implements ArmorStandApplyable {
  private final Optional<EulerAngle> optionalHead;
  private final Optional<EulerAngle> optionalBody;
  private final Optional<EulerAngle> optionalRightArm;
  private final Optional<EulerAngle> optionalLeftArm;
  private final Optional<EulerAngle> optionalRightLeg;
  private final Optional<EulerAngle> optionalLeftLeg;

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
    this(
        Optional.of(head),
        Optional.of(body),
        Optional.of(rightArm),
        Optional.of(leftArm),
        Optional.of(rightLeg),
        Optional.of(leftLeg));
  }

  public Pose(
      Optional<EulerAngle> optionalHead,
      Optional<EulerAngle> optionalBody,
      Optional<EulerAngle> optionalRightArm,
      Optional<EulerAngle> optionalLeftArm,
      Optional<EulerAngle> optionalRightLeg,
      Optional<EulerAngle> optionalLeftLeg) {
    this.optionalHead = optionalHead;
    this.optionalBody = optionalBody;
    this.optionalRightArm = optionalRightArm;
    this.optionalLeftArm = optionalLeftArm;
    this.optionalRightLeg = optionalRightLeg;
    this.optionalLeftLeg = optionalLeftLeg;
  }

  @Override
  public void apply(ArmorStandEntity armorStand) {
    optionalHead.ifPresent((head) -> armorStand.setHeadRotation(head));
    optionalBody.ifPresent((body) -> armorStand.setBodyRotation(body));
    optionalRightArm.ifPresent((rightArm) -> armorStand.setRightArmRotation(rightArm));
    optionalLeftArm.ifPresent((leftArm) -> armorStand.setLeftArmRotation(leftArm));
    optionalRightLeg.ifPresent((rightLeg) -> armorStand.setRightLegRotation(rightLeg));
    optionalLeftLeg.ifPresent((leftLeg) -> armorStand.setLeftLegRotation(leftLeg));
  }

  public Pose toPose(ArmorStandEntity armorStand) {
    return new Pose(
        optionalHead.orElse(armorStand.getHeadRotation()),
        optionalBody.orElse(armorStand.getBodyRotation()),
        optionalRightArm.orElse(armorStand.getRightArmRotation()),
        optionalLeftArm.orElse(armorStand.getLeftArmRotation()),
        optionalRightLeg.orElse(armorStand.getRightLegRotation()),
        optionalLeftLeg.orElse(armorStand.getLeftLegRotation()));
  }
}
