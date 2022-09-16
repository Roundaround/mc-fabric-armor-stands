package me.roundaround.armorstands.network;

import java.util.Arrays;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.util.Pose;
import net.minecraft.text.Text;
import net.minecraft.util.math.EulerAngle;

public enum PosePreset {
  DEFAULT(
      "default",
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(-15f, 0f, 10f),
      new EulerAngle(-10f, 0f, -10f),
      new EulerAngle(1f, 0f, 1f),
      new EulerAngle(-1f, 0f, -1f)),
  ATTENTION(
      "attention",
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f)),
  WALKING(
      "walking",
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(20f, 0f, 10f),
      new EulerAngle(-20f, 0f, -10f),
      new EulerAngle(-20f, 0f, 0f),
      new EulerAngle(20f, 0f, 0f)),
  RUNNING(
      "running",
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(-40f, 0f, 10f),
      new EulerAngle(40f, 0f, -10f),
      new EulerAngle(-40f, 0f, 0f),
      new EulerAngle(40f, 0f, 0f)),
  SALUTE(
      "salute",
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(5f, 0f, 0f),
      new EulerAngle(-124f, -51f, -35f),
      new EulerAngle(29f, 0f, 25f),
      new EulerAngle(0f, -4f, -2f),
      new EulerAngle(0f, 4f, 2f)),
  POINTING(
      "pointing",
      new EulerAngle(0f, 20f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(-90f, 18f, 0f),
      new EulerAngle(0f, 0f, -10f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f)),
  FACEPALM(
      "facepalm",
      new EulerAngle(45f, -4f, 1f),
      new EulerAngle(10f, 0f, 0f),
      new EulerAngle(18f, -14f, 0f),
      new EulerAngle(-72f, 24f, 47f),
      new EulerAngle(25f, -2f, 0f),
      new EulerAngle(-4f, -6f, -2f)),
  BLOCKING(
      "blocking",
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(-20f, -20f, 0f),
      new EulerAngle(-50f, 50f, 0f),
      new EulerAngle(-20f, 0f, 0f),
      new EulerAngle(20f, 0f, 0f)),
  LAZING(
      "lazing",
      new EulerAngle(14f, -12f, 6f),
      new EulerAngle(5f, 0f, 0f),
      new EulerAngle(-40f, 20f, 0f),
      new EulerAngle(-4f, -20f, -10f),
      new EulerAngle(-88f, 71f, 0f),
      new EulerAngle(-88f, 46f, 0f)),
  LUNGEING(
      "lungeing",
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(15.0f, 0.0f, 0.0f),
      new EulerAngle(-60.0f, -10.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, -10.0f),
      new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(30.0f, 0.0f, 0.0f)),
  CONFUSED(
      "confused",
      new EulerAngle(0.0f, 30.0f, 0f),
      new EulerAngle(0.0f, 13.0f, 0.0f),
      new EulerAngle(-22.0f, 31.0f, 10.0f),
      new EulerAngle(145.0f, 22.0f, -49.0f),
      new EulerAngle(6.0f, -20.0f, 0.0f),
      new EulerAngle(-6.0f, 0.0f, 0.0f)),
  WINNING(
      "winning",
      new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(-120.0f, -10.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, -10.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(15.0f, 0.0f, 0.0f)),
  FORMAL(
      "formal",
      new EulerAngle(4.0f, 0.0f, 0.0f),
      new EulerAngle(4.0f, 0.0f, 0.0f),
      new EulerAngle(30.0f, 22.0f, -20.0f),
      new EulerAngle(30.0f, -20.0f, 21.0f),
      new EulerAngle(0.0f, 0.0f, 5.0f),
      new EulerAngle(0.0f, 0.0f, -5.0f)),
  SITTING(
      "sitting",
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(-80.0f, 20.0f, 0.0f),
      new EulerAngle(-80.0f, -20.0f, 0.0f),
      new EulerAngle(-90.0f, 10.0f, 0.0f),
      new EulerAngle(-90.0f, -10.0f, 0.0f)),
  SAD(
      "sad",
      new EulerAngle(63.0f, 0.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, 0.0f),
      new EulerAngle(-5.0f, 0.0f, 5.0f),
      new EulerAngle(-5.0f, 0.0f, -5.0f),
      new EulerAngle(-5.0f, -10.0f, 5.0f),
      new EulerAngle(-5.0f, 16.0f, -5.0f)),
  ARABESQUE(
      "arabesque",
      new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, 0.0f),
      new EulerAngle(-140.0f, -10.0f, 0.0f),
      new EulerAngle(70.0f, 0.0f, -10.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(75.0f, 0.0f, 0.0f)),
  JOYOUS(
      "joyous",
      new EulerAngle(-11.0f, 0.0f, 0.0f),
      new EulerAngle(-4.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 100.0f),
      new EulerAngle(0.0f, 0.0f, -100.0f),
      new EulerAngle(-8.0f, 0.0f, 60.0f),
      new EulerAngle(-8.0f, 0.0f, -60.0f)),
  CUPID(
      "cupid",
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(10.0f, 0.0f, 0.0f),
      new EulerAngle(-90.0f, -10.0f, 0.0f),
      new EulerAngle(-75.0f, 0.0f, 10.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(75.0f, 0.0f, 0.0f)),
  STARGAZING(
      "stargazing",
      new EulerAngle(-22.0f, 25.0f, 0.0f),
      new EulerAngle(-4.0f, 10.0f, 0.0f),
      new EulerAngle(-153.0f, 34.0f, -3.0f),
      new EulerAngle(4.0f, 18.0f, 0.0f),
      new EulerAngle(-4.0f, 17.0f, 2.0f),
      new EulerAngle(6.0f, 24.0f, 0.0f)),
  BLOCK(
      "block",
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(-15.0f, -45.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f)),
  ITEM(
      "item",
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(-90.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f));

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
    this.label = Text.translatable("armorstands.pose." + id);
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

  public Pose toPose() {
    return new Pose(head, body, rightArm, leftArm, rightLeg, leftLeg);
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
