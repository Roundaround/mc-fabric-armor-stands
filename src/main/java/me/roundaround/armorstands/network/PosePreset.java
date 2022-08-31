package me.roundaround.armorstands.network;

import java.util.Arrays;

import me.roundaround.armorstands.ArmorStandsMod;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.EulerAngle;

public enum PosePreset {
  ATTENTION(
      "armorstands.pose.attention",
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f)),
  WALKING(
      "armorstands.pose.walking",
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(20f, 0f, 10f),
      new EulerAngle(-20f, 0f, -10f),
      new EulerAngle(-20f, 0f, 0f),
      new EulerAngle(20f, 0f, 0f)),
  RUNNING(
      "armorstands.pose.running",
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(-40f, 0f, 10f),
      new EulerAngle(40f, 0f, -10f),
      new EulerAngle(-40f, 0f, 0f),
      new EulerAngle(40f, 0f, 0f));

  private final String id;
  private final Text label;
  private final EulerAngle head;
  private final EulerAngle body;
  private final EulerAngle rightArm;
  private final EulerAngle leftArm;
  private final EulerAngle rightLeg;
  private final EulerAngle leftLeg;

  private PosePreset(
      String id,
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg) {
    this.id = id;
    this.label = Text.translatable(id);
    this.head = head;
    this.body = body;
    this.rightArm = rightArm;
    this.leftArm = leftArm;
    this.rightLeg = rightLeg;
    this.leftLeg = leftLeg;
  }

  @Override
  public String toString() {
    return id;
  }

  public void apply(ArmorStandEntity entity) {
    entity.setHeadRotation(head);
    entity.setBodyRotation(body);
    entity.setRightArmRotation(rightArm);
    entity.setLeftArmRotation(leftArm);
    entity.setRightLegRotation(rightLeg);
    entity.setLeftLegRotation(leftLeg);
  }

  public static PosePreset fromString(String value) {
    return Arrays.stream(PosePreset.values())
        .filter((pose) -> pose.id.equals(value))
        .findFirst()
        .orElseGet(() -> {
          ArmorStandsMod.LOGGER.warn("Unknown id '{}'. Defaulting to ATTENTION.", value);
          return ATTENTION;
        });
  }
}
