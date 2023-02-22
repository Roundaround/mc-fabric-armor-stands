package me.roundaround.armorstands.util;

import java.util.Arrays;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.util.Pose.PoseSupplier;
import net.minecraft.text.Text;
import net.minecraft.util.math.EulerAngle;

public enum PosePreset implements PoseSupplier {
  DEFAULT(
      "default",
      Source.VANILLA,
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(-15f, 0f, 10f),
      new EulerAngle(-10f, 0f, -10f),
      new EulerAngle(1f, 0f, 1f),
      new EulerAngle(-1f, 0f, -1f)),
  ATTENTION(
      "attention",
      Source.VANILLA_TWEAKS,
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f)),
  WALKING(
      "walking",
      Source.VANILLA_TWEAKS,
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(20f, 0f, 10f),
      new EulerAngle(-20f, 0f, -10f),
      new EulerAngle(-20f, 0f, 0f),
      new EulerAngle(20f, 0f, 0f)),
  RUNNING(
      "running",
      Source.VANILLA_TWEAKS,
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(-40f, 0f, 10f),
      new EulerAngle(40f, 0f, -10f),
      new EulerAngle(-40f, 0f, 0f),
      new EulerAngle(40f, 0f, 0f)),
  LUNGEING(
      "lungeing",
      Source.VANILLA_TWEAKS,
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(15.0f, 0.0f, 0.0f),
      new EulerAngle(-60.0f, -10.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, -10.0f),
      new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(30.0f, 0.0f, 0.0f)),
  POINTING(
      "pointing",
      Source.VANILLA_TWEAKS,
      new EulerAngle(0f, 20f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(-90f, 18f, 0f),
      new EulerAngle(0f, 0f, -10f),
      new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f)),
  FACEPALM(
      "facepalm",
      Source.VANILLA_TWEAKS,
      new EulerAngle(45f, -4f, 1f),
      new EulerAngle(10f, 0f, 0f),
      new EulerAngle(18f, -14f, 0f),
      new EulerAngle(-72f, 24f, 47f),
      new EulerAngle(25f, -2f, 0f),
      new EulerAngle(-4f, -6f, -2f)),
  BLOCKING(
      "blocking",
      Source.VANILLA_TWEAKS,
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(-20f, -20f, 0f),
      new EulerAngle(-50f, 50f, 0f),
      new EulerAngle(-20f, 0f, 0f),
      new EulerAngle(20f, 0f, 0f)),
  CONFUSED(
      "confused",
      Source.VANILLA_TWEAKS,
      new EulerAngle(0.0f, 30.0f, 0f),
      new EulerAngle(0.0f, 13.0f, 0.0f),
      new EulerAngle(-22.0f, 31.0f, 10.0f),
      new EulerAngle(145.0f, 22.0f, -49.0f),
      new EulerAngle(6.0f, -20.0f, 0.0f),
      new EulerAngle(-6.0f, 0.0f, 0.0f)),
  WINNING(
      "winning",
      Source.VANILLA_TWEAKS,
      new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(-120.0f, -10.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, -10.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(15.0f, 0.0f, 0.0f)),
  FORMAL(
      "formal",
      Source.VANILLA_TWEAKS,
      new EulerAngle(4.0f, 0.0f, 0.0f),
      new EulerAngle(4.0f, 0.0f, 0.0f),
      new EulerAngle(30.0f, 22.0f, -20.0f),
      new EulerAngle(30.0f, -20.0f, 21.0f),
      new EulerAngle(0.0f, 0.0f, 5.0f),
      new EulerAngle(0.0f, 0.0f, -5.0f)),
  SALUTE(
      "salute",
      Source.VANILLA_TWEAKS,
      new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(5f, 0f, 0f),
      new EulerAngle(-124f, -51f, -35f),
      new EulerAngle(29f, 0f, 25f),
      new EulerAngle(0f, -4f, -2f),
      new EulerAngle(0f, 4f, 2f)),
  SAD(
      "sad",
      Source.VANILLA_TWEAKS,
      new EulerAngle(63.0f, 0.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, 0.0f),
      new EulerAngle(-5.0f, 0.0f, 5.0f),
      new EulerAngle(-5.0f, 0.0f, -5.0f),
      new EulerAngle(-5.0f, -10.0f, 5.0f),
      new EulerAngle(-5.0f, 16.0f, -5.0f)),
  ARABESQUE(
      "arabesque",
      Source.VANILLA_TWEAKS,
      new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, 0.0f),
      new EulerAngle(-140.0f, -10.0f, 0.0f),
      new EulerAngle(70.0f, 0.0f, -10.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(75.0f, 0.0f, 0.0f)),
  CANCAN(
      "cancan",
      Source.BEDROCK,
      new EulerAngle(-5.0f, 18.0f, 0.0f),
      new EulerAngle(0.0f, 22.0f, 0.0f),
      new EulerAngle(0.0f, 84.0f, 111.0f),
      new EulerAngle(8.0f, 0.0f, -114.0f),
      new EulerAngle(0.0f, 23.0f, -13.0f),
      new EulerAngle(-111.0f, 55.0f, 0.0f)),
  JOYOUS(
      "joyous",
      Source.VANILLA_TWEAKS,
      new EulerAngle(-11.0f, 0.0f, 0.0f),
      new EulerAngle(-4.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 100.0f),
      new EulerAngle(0.0f, 0.0f, -100.0f),
      new EulerAngle(-8.0f, 0.0f, 60.0f),
      new EulerAngle(-8.0f, 0.0f, -60.0f)),
  CUPID(
      "cupid",
      Source.VANILLA_TWEAKS,
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(10.0f, 0.0f, 0.0f),
      new EulerAngle(-90.0f, -10.0f, 0.0f),
      new EulerAngle(-75.0f, 0.0f, 10.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(75.0f, 0.0f, 0.0f)),
  STARGAZING(
      "stargazing",
      Source.VANILLA_TWEAKS,
      new EulerAngle(-22.0f, 25.0f, 0.0f),
      new EulerAngle(-4.0f, 10.0f, 0.0f),
      new EulerAngle(-153.0f, 34.0f, -3.0f),
      new EulerAngle(4.0f, 18.0f, 0.0f),
      new EulerAngle(-4.0f, 17.0f, 2.0f),
      new EulerAngle(6.0f, 24.0f, 0.0f)),
  GUARD(
      "guard",
      Source.BEDROCK,
      new EulerAngle(-5.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 2.0f),
      new EulerAngle(-60.0f, 20.0f, -10.0f),
      new EulerAngle(10.0f, 0.0f, -5.0f),
      new EulerAngle(3.0f, 3.0f, 3.0f),
      new EulerAngle(-3.0f, -3.0f, -3.0f)),
  BRANDISH(
      "brandish",
      Source.BEDROCK,
      new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, -2.0f),
      new EulerAngle(-110.0f, 50.0f, 0.0f),
      new EulerAngle(20.0f, 0.0f, -10.0f),
      new EulerAngle(-5.0f, 3.0f, 3.0f),
      new EulerAngle(5.0f, -3.0f, -3.0f)),
  SPEAKING(
      "speaking",
      Source.BEDROCK,
      new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(-110.0f, 35.0f, 0.0f),
      new EulerAngle(-110.0f, -35.0f, 0.0f),
      new EulerAngle(-5.0f, 3.0f, 3.0f),
      new EulerAngle(5.0f, -3.0f, -3.0f)),
  INSPIRING(
      "inspiring",
      Source.BEDROCK,
      new EulerAngle(-4.0f, 67.0f, 0.0f),
      new EulerAngle(0.0f, 8.0f, 0.0f),
      new EulerAngle(-99.0f, 63.0f, 0.0f),
      new EulerAngle(16.0f, 32.0f, -8.0f),
      new EulerAngle(4.0f, 63.0f, 8.0f),
      new EulerAngle(0.0f, -75.0f, -8.0f)),
  THANKING(
      "thanking",
      Source.BEDROCK,
      new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(-110.0f, -35.0f, 0.0f),
      new EulerAngle(-110.0f, 35.0f, 0.0f),
      new EulerAngle(-5.0f, 3.0f, 3.0f),
      new EulerAngle(5.0f, -3.0f, -3.0f)),
  DABBING(
      "dabbing",
      Source.BEDROCK,
      new EulerAngle(16.0f, 20.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(246.0f, 0.0f, 89.0f),
      new EulerAngle(4.0f, 8.0f, 237.0f),
      new EulerAngle(8.0f, 20.0f, 4.0f),
      new EulerAngle(-14.0f, -18.0f, -16.0f)),
  PLEDGEING(
      "pledgeing",
      Source.BEDROCK,
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(-70.0f, -40.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, -5.0f),
      new EulerAngle(1.0f, 0.0f, 1.0f),
      new EulerAngle(-1.0f, 0.0f, -1.0f)),
  CONTEMPLATING(
      "contemplating",
      Source.BEDROCK,
      new EulerAngle(15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 2.0f),
      new EulerAngle(-60.0f, -20.0f, -10.0f),
      new EulerAngle(-30.0f, 15.0f, 15.0f),
      new EulerAngle(1.0f, 0.0f, 1.0f),
      new EulerAngle(-1.0f, 0.0f, -1.0f)),
  ZOMBIE(
      "zombie",
      Source.BEDROCK,
      new EulerAngle(-10.0f, 0.0f, -5.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(-100.0f, 0.0f, 0.0f),
      new EulerAngle(-105.0f, 0.0f, 0.0f),
      new EulerAngle(-46.0f, 0.0f, 0.0f),
      new EulerAngle(7.0f, 0.0f, 0.0f)),
  SITTING(
      "sitting",
      Source.VANILLA_TWEAKS,
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(-80.0f, 20.0f, 0.0f),
      new EulerAngle(-80.0f, -20.0f, 0.0f),
      new EulerAngle(-90.0f, 10.0f, 0.0f),
      new EulerAngle(-90.0f, -10.0f, 0.0f)),
  LAZING(
      "lazing",
      Source.VANILLA_TWEAKS,
      new EulerAngle(14f, -12f, 6f),
      new EulerAngle(5f, 0f, 0f),
      new EulerAngle(-40f, 20f, 0f),
      new EulerAngle(-4f, -20f, -10f),
      new EulerAngle(-88f, 71f, 0f),
      new EulerAngle(-88f, 46f, 0f));

  private final String id;
  private final Text label;
  private final Source source;
  private final EulerAngle head;
  private final EulerAngle body;
  private final EulerAngle rightArm;
  private final EulerAngle leftArm;
  private final EulerAngle rightLeg;
  private final EulerAngle leftLeg;

  private PosePreset(
      String id,
      Source source,
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg) {
    this.id = id;
    this.label = Text.translatable("armorstands.preset." + id);
    this.source = source;
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

  @Override
  public Pose toPose() {
    return new Pose(head, body, rightArm, leftArm, rightLeg, leftLeg);
  }

  public Text getLabel() {
    return label;
  }

  public Source getSource() {
    return source;
  }

  public EulerAngle getHead() {
    return head;
  }

  public EulerAngle getBody() {
    return body;
  }

  public EulerAngle getRightArm() {
    return rightArm;
  }

  public EulerAngle getLeftArm() {
    return leftArm;
  }

  public EulerAngle getRightLeg() {
    return rightLeg;
  }

  public EulerAngle getLeftLeg() {
    return leftLeg;
  }

  public static PosePreset fromString(String value) {
    return Arrays.stream(PosePreset.values())
        .filter((pose) -> pose.id.equals(value))
        .findFirst()
        .orElseGet(() -> {
          ArmorStandsMod.LOGGER.warn("Unknown id '{}'. Defaulting to DEFAULT.", Source.BEDROCK, value);
          return DEFAULT;
        });
  }

  public static enum Source {
    VANILLA("vanilla"),
    VANILLA_TWEAKS("vanillaTweaks"),
    BEDROCK("bedrock");

    private final String id;

    private Source(String id) {
      this.id = id;
    }

    public Text getDisplayName() {
      return Text.translatable("armorstands.source." + id);
    }
  }
}
