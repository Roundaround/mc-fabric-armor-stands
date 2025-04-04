package me.roundaround.armorstands.util;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EulerAngle;

import java.util.Optional;

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
        armorStand.getLeftLegRotation()
    );
  }

  public Pose(
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg
  ) {
    this(
        Optional.of(head),
        Optional.of(body),
        Optional.of(rightArm),
        Optional.of(leftArm),
        Optional.of(rightLeg),
        Optional.of(leftLeg)
    );
  }

  public Pose(
      Optional<EulerAngle> optionalHead,
      Optional<EulerAngle> optionalBody,
      Optional<EulerAngle> optionalRightArm,
      Optional<EulerAngle> optionalLeftArm,
      Optional<EulerAngle> optionalRightLeg,
      Optional<EulerAngle> optionalLeftLeg
  ) {
    this.optionalHead = optionalHead;
    this.optionalBody = optionalBody;
    this.optionalRightArm = optionalRightArm;
    this.optionalLeftArm = optionalLeftArm;
    this.optionalRightLeg = optionalRightLeg;
    this.optionalLeftLeg = optionalLeftLeg;
  }

  @Override
  public void apply(PlayerEntity player, ArmorStandEntity armorStand) {
    optionalHead.ifPresent(armorStand::setHeadRotation);
    optionalBody.ifPresent(armorStand::setBodyRotation);
    optionalRightArm.ifPresent(armorStand::setRightArmRotation);
    optionalLeftArm.ifPresent(armorStand::setLeftArmRotation);
    optionalRightLeg.ifPresent(armorStand::setRightLegRotation);
    optionalLeftLeg.ifPresent(armorStand::setLeftLegRotation);
  }

  public EulerAngle getHead() {
    return optionalHead.orElse(PosePreset.DEFAULT.getHead());
  }

  public EulerAngle getBody() {
    return optionalBody.orElse(PosePreset.DEFAULT.getBody());
  }

  public EulerAngle getRightArm() {
    return optionalRightArm.orElse(PosePreset.DEFAULT.getRightArm());
  }

  public EulerAngle getLeftArm() {
    return optionalLeftArm.orElse(PosePreset.DEFAULT.getLeftArm());
  }

  public EulerAngle getRightLeg() {
    return optionalRightLeg.orElse(PosePreset.DEFAULT.getRightLeg());
  }

  public EulerAngle getLeftLeg() {
    return optionalLeftLeg.orElse(PosePreset.DEFAULT.getLeftLeg());
  }

  public Pose mirror() {
    return new Pose(
        optionalHead.map((head) -> new EulerAngle(head.pitch(), -head.yaw(), -head.roll())),
        optionalBody.map((body) -> new EulerAngle(body.pitch(), -body.yaw(), -body.roll())),
        optionalLeftArm.map((leftArm) -> new EulerAngle(leftArm.pitch(), -leftArm.yaw(), -leftArm.roll())),
        optionalRightArm.map((rightArm) -> new EulerAngle(rightArm.pitch(), -rightArm.yaw(), -rightArm.roll())),
        optionalLeftLeg.map((leftLeg) -> new EulerAngle(leftLeg.pitch(), -leftLeg.yaw(), -leftLeg.roll())),
        optionalRightLeg.map((rightLeg) -> new EulerAngle(rightLeg.pitch(), -rightLeg.yaw(), -rightLeg.roll()))
    );
  }

  @FunctionalInterface
  public static interface PoseSupplier {
    public Pose toPose();
  }
}
