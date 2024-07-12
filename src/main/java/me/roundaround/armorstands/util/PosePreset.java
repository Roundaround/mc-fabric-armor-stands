package me.roundaround.armorstands.util;

import io.netty.buffer.ByteBuf;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.util.Pose.PoseSupplier;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.math.EulerAngle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PosePreset implements PoseSupplier {
  DEFAULT("default", Source.VANILLA, Category.STANDING, new EulerAngle(0f, 0f, 0f), new EulerAngle(0f, 0f, 0f),
      new EulerAngle(-15f, 0f, 10f), new EulerAngle(-10f, 0f, -10f), new EulerAngle(1f, 0f, 1f),
      new EulerAngle(-1f, 0f, -1f)
  ),
  ATTENTION("attention", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f), new EulerAngle(0f, 0f, 0f), new EulerAngle(0f, 0f, 0f),
      new EulerAngle(0f, 0f, 0f), new EulerAngle(0f, 0f, 0f)
  ),
  CONFIDENT("confident", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(-10f, 20f, 0f),
      new EulerAngle(-2f, 0f, 0f), new EulerAngle(5f, 0f, 0f), new EulerAngle(5f, 0f, 0f), new EulerAngle(16f, 2f, 10f),
      new EulerAngle(0f, -10f, -4f)
  ),
  WALKING("walking", Source.VANILLA_TWEAKS, Category.MOVING, new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f), new EulerAngle(20f, 0f, 10f), new EulerAngle(-20f, 0f, -10f),
      new EulerAngle(-20f, 0f, 0f), new EulerAngle(20f, 0f, 0f)
  ),
  RUNNING("running", Source.VANILLA_TWEAKS, Category.MOVING, new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f), new EulerAngle(-40f, 0f, 10f), new EulerAngle(40f, 0f, -10f),
      new EulerAngle(-40f, 0f, 0f), new EulerAngle(40f, 0f, 0f)
  ),
  LUNGING("lunging", Source.VANILLA_TWEAKS, Category.MOVING, new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(15.0f, 0.0f, 0.0f), new EulerAngle(-60.0f, -10.0f, 0.0f), new EulerAngle(10.0f, 0.0f, -10.0f),
      new EulerAngle(-15.0f, 0.0f, 0.0f), new EulerAngle(30.0f, 0.0f, 0.0f)
  ),
  POINTING("pointing", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(0f, 20f, 0f),
      new EulerAngle(0f, 0.001f, 0f), new EulerAngle(-90f, 18f, 0f), new EulerAngle(0f, 0f, -10f),
      new EulerAngle(0f, 0f, 0f), new EulerAngle(0f, 0f, 0f)
  ),
  FACEPALM("facepalm", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(45f, -4f, 1f),
      new EulerAngle(10f, 0f, 0f), new EulerAngle(18f, -14f, 0f), new EulerAngle(-72f, 24f, 47f),
      new EulerAngle(25f, -2f, 0f), new EulerAngle(-4f, -6f, -2f)
  ),
  BLOCKING("blocking", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(0f, 0.001f, 0f),
      new EulerAngle(0f, 0.001f, 0f), new EulerAngle(-20f, -20f, 0f), new EulerAngle(-50f, 50f, 0f),
      new EulerAngle(-20f, 0f, 0f), new EulerAngle(20f, 0f, 0f)
  ),
  CONFUSED("confused", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(0.0f, 30.0f, 0f),
      new EulerAngle(0.0f, 13.0f, 0.0f), new EulerAngle(-22.0f, 31.0f, 10.0f), new EulerAngle(145.0f, 22.0f, -49.0f),
      new EulerAngle(6.0f, -20.0f, 0.0f), new EulerAngle(-6.0f, 0.0f, 0.0f)
  ),
  WINNING("winning", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.001f, 0.0f), new EulerAngle(-120.0f, -10.0f, 0.0f), new EulerAngle(10.0f, 0.0f, -10.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f), new EulerAngle(15.0f, 0.0f, 0.0f)
  ),
  FORMAL("formal", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(4.0f, 0.0f, 0.0f),
      new EulerAngle(4.0f, 0.0f, 0.0f), new EulerAngle(30.0f, 22.0f, -20.0f), new EulerAngle(30.0f, -20.0f, 21.0f),
      new EulerAngle(0.0f, 0.0f, 5.0f), new EulerAngle(0.0f, 0.0f, -5.0f)
  ),
  SALUTE("salute", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(0f, 0.001f, 0f), new EulerAngle(5f, 0f, 0f),
      new EulerAngle(-124f, -51f, -35f), new EulerAngle(29f, 0f, 25f), new EulerAngle(0f, -4f, -2f),
      new EulerAngle(0f, 4f, 2f)
  ),
  SAD("sad", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(63.0f, 0.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, 0.0f), new EulerAngle(-5.0f, 0.0f, 5.0f), new EulerAngle(-5.0f, 0.0f, -5.0f),
      new EulerAngle(-5.0f, -10.0f, 5.0f), new EulerAngle(-5.0f, 16.0f, -5.0f)
  ),
  ARABESQUE("arabesque", Source.VANILLA_TWEAKS, Category.DYNAMIC, new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(10.0f, 0.0f, 0.0f), new EulerAngle(-140.0f, -10.0f, 0.0f), new EulerAngle(70.0f, 0.0f, -10.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f), new EulerAngle(75.0f, 0.0f, 0.0f)
  ),
  CANCAN("cancan", Source.BEDROCK, Category.DYNAMIC, new EulerAngle(-5.0f, 18.0f, 0.0f),
      new EulerAngle(0.0f, 22.0f, 0.0f), new EulerAngle(0.0f, 84.0f, 111.0f), new EulerAngle(8.0f, 0.0f, -114.0f),
      new EulerAngle(0.0f, 23.0f, -13.0f), new EulerAngle(-111.0f, 55.0f, 0.0f)
  ),
  JOYOUS("joyous", Source.VANILLA_TWEAKS, Category.DYNAMIC, new EulerAngle(-11.0f, 0.0f, 0.0f),
      new EulerAngle(-4.0f, 0.0f, 0.0f), new EulerAngle(0.0f, 0.0f, 100.0f), new EulerAngle(0.0f, 0.0f, -100.0f),
      new EulerAngle(-8.0f, 0.0f, 60.0f), new EulerAngle(-8.0f, 0.0f, -60.0f)
  ),
  CUPID("cupid", Source.VANILLA_TWEAKS, Category.DYNAMIC, new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(10.0f, 0.0f, 0.0f), new EulerAngle(-90.0f, -10.0f, 0.0f), new EulerAngle(-75.0f, 0.0f, 10.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f), new EulerAngle(75.0f, 0.0f, 0.0f)
  ),
  STARGAZING("stargazing", Source.VANILLA_TWEAKS, Category.STANDING, new EulerAngle(-22.0f, 25.0f, 0.0f),
      new EulerAngle(-4.0f, 10.0f, 0.0f), new EulerAngle(-153.0f, 34.0f, -3.0f), new EulerAngle(4.0f, 18.0f, 0.0f),
      new EulerAngle(-4.0f, 17.0f, 2.0f), new EulerAngle(6.0f, 24.0f, 0.0f)
  ),
  GUARD("guard", Source.BEDROCK, Category.STANDING, new EulerAngle(-5.0f, 0.0f, 0.0f), new EulerAngle(0.0f, 0.0f, 2.0f),
      new EulerAngle(-60.0f, 20.0f, -10.0f), new EulerAngle(10.0f, 0.0f, -5.0f), new EulerAngle(3.0f, 3.0f, 3.0f),
      new EulerAngle(-3.0f, -3.0f, -3.0f)
  ),
  BRANDISH("brandish", Source.BEDROCK, Category.STANDING, new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, -2.0f), new EulerAngle(-110.0f, 50.0f, 0.0f), new EulerAngle(20.0f, 0.0f, -10.0f),
      new EulerAngle(-5.0f, 3.0f, 3.0f), new EulerAngle(5.0f, -3.0f, -3.0f)
  ),
  SPEAKING("speaking", Source.BEDROCK, Category.STANDING, new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f), new EulerAngle(-110.0f, 35.0f, 0.0f), new EulerAngle(-110.0f, -35.0f, 0.0f),
      new EulerAngle(-5.0f, 3.0f, 3.0f), new EulerAngle(5.0f, -3.0f, -3.0f)
  ),
  INSPIRING("inspiring", Source.BEDROCK, Category.STANDING, new EulerAngle(-4.0f, 67.0f, 0.0f),
      new EulerAngle(0.0f, 8.0f, 0.0f), new EulerAngle(-99.0f, 63.0f, 0.0f), new EulerAngle(16.0f, 32.0f, -8.0f),
      new EulerAngle(4.0f, 63.0f, 8.0f), new EulerAngle(0.0f, -75.0f, -8.0f)
  ),
  THANKING("thanking", Source.BEDROCK, Category.STANDING, new EulerAngle(-15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f), new EulerAngle(-110.0f, -35.0f, 0.0f), new EulerAngle(-110.0f, 35.0f, 0.0f),
      new EulerAngle(-5.0f, 3.0f, 3.0f), new EulerAngle(5.0f, -3.0f, -3.0f)
  ),
  DABBING("dabbing", Source.BEDROCK, Category.DYNAMIC, new EulerAngle(16.0f, 20.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f), new EulerAngle(246.0f, 0.0f, 89.0f), new EulerAngle(4.0f, 8.0f, 237.0f),
      new EulerAngle(8.0f, 20.0f, 4.0f), new EulerAngle(-14.0f, -18.0f, -16.0f)
  ),
  PLEDGING("pledging", Source.BEDROCK, Category.STANDING, new EulerAngle(0.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f), new EulerAngle(-70.0f, -40.0f, 0.0f), new EulerAngle(10.0f, 0.0f, -5.0f),
      new EulerAngle(1.0f, 0.0f, 1.0f), new EulerAngle(-1.0f, 0.0f, -1.0f)
  ),
  CONTEMPLATING("contemplating", Source.BEDROCK, Category.STANDING, new EulerAngle(15.0f, 0.0f, 0.0f),
      new EulerAngle(0.0f, 0.0f, 2.0f), new EulerAngle(-60.0f, -20.0f, -10.0f), new EulerAngle(-30.0f, 15.0f, 15.0f),
      new EulerAngle(1.0f, 0.0f, 1.0f), new EulerAngle(-1.0f, 0.0f, -1.0f)
  ),
  ZOMBIE("zombie", Source.BEDROCK, Category.MOVING, new EulerAngle(-10.0f, 0.0f, -5.0f),
      new EulerAngle(0.0f, 0.0f, 0.0f), new EulerAngle(-100.0f, 0.0f, 0.0f), new EulerAngle(-105.0f, 0.0f, 0.0f),
      new EulerAngle(-46.0f, 0.0f, 0.0f), new EulerAngle(7.0f, 0.0f, 0.0f)
  ),
  SITTING("sitting", Source.VANILLA_TWEAKS, Category.SITTING, new EulerAngle(0.0f, 0.001f, 0.0f),
      new EulerAngle(0.0f, 0.001f, 0.0f), new EulerAngle(-80.0f, 20.0f, 0.0f), new EulerAngle(-80.0f, -20.0f, 0.0f),
      new EulerAngle(-90.0f, 10.0f, 0.0f), new EulerAngle(-90.0f, -10.0f, 0.0f)
  ),
  LAZING("lazing", Source.VANILLA_TWEAKS, Category.SITTING, new EulerAngle(14f, -12f, 6f), new EulerAngle(5f, 0f, 0f),
      new EulerAngle(-40f, 20f, 0f), new EulerAngle(-4f, -20f, -10f), new EulerAngle(-88f, 71f, 0f),
      new EulerAngle(-88f, 46f, 0f)
  ),
  JUMP_ATTACK("jumpAttack", Source.ROUNDAROUND, Category.DYNAMIC, new EulerAngle(-43f, 0f, 0f),
      new EulerAngle(4f, 0f, 0f), new EulerAngle(-144f, -10f, 28f), new EulerAngle(-129f, 47f, -7f),
      new EulerAngle(30f, 0f, 20f), new EulerAngle(30f, 0f, -20f)
  ),
  STEWARD("steward", Source.ROUNDAROUND, Category.STANDING, new EulerAngle(-3f, 0f, 0f), new EulerAngle(0f, 0f, 2f),
      new EulerAngle(-105f, -45f, 180f), new EulerAngle(-2f, 0f, -20f), new EulerAngle(0f, 0f, 5f),
      new EulerAngle(0f, 0f, -5f)
  ),
  SCARECROW("scarecrow", Source.ROUNDAROUND, Category.DYNAMIC, new EulerAngle(8f, 26f, -6f), new EulerAngle(0f, 0f, 5f),
      new EulerAngle(-15f, 0f, 79f), new EulerAngle(-16f, 0f, -40f), new EulerAngle(0f, 0f, -18f),
      new EulerAngle(16f, 20f, 0f)
  ),
  PRETTY("pretty", Source.ROUNDAROUND, Category.SITTING, new EulerAngle(-17f, 0f, 0f), new EulerAngle(0f, 0f, -8f),
      new EulerAngle(-12f, 20f, 0f), new EulerAngle(130f, -14f, -3f), new EulerAngle(-100f, -44f, 0f),
      new EulerAngle(-90f, -10f, 0f)
  ),
  DRUM_MAJOR("drumMajor", Source.ROUNDAROUND, Category.DYNAMIC, new EulerAngle(-13f, 0f, 0f),
      new EulerAngle(9f, 0f, 0f), new EulerAngle(110f, 125f, 180f), new EulerAngle(-10f, 0f, -20f),
      new EulerAngle(-94f, 0f, 0f), new EulerAngle(15f, 0f, 0f)
  ),
  FISHING("fishing", Source.ROUNDAROUND, Category.SITTING, new EulerAngle(-15f, 0f, -12f), new EulerAngle(-8f, 0f, 0f),
      new EulerAngle(-68f, -24f, 0f), new EulerAngle(-80f, 44f, 0f), new EulerAngle(-90f, 20f, 0f),
      new EulerAngle(-90f, -20f, 0f)
  );

  public static final PacketCodec<ByteBuf, PosePreset> PACKET_CODEC = PacketCodec.tuple(
      PacketCodecs.STRING, PosePreset::getId, PosePreset::fromString);

  private final String id;
  private final Text label;
  private final Source source;
  private final Category category;
  private final EulerAngle head;
  private final EulerAngle body;
  private final EulerAngle rightArm;
  private final EulerAngle leftArm;
  private final EulerAngle rightLeg;
  private final EulerAngle leftLeg;

  PosePreset(
      String id,
      Source source,
      Category category,
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg
  ) {
    this.id = id;
    this.label = Text.translatable("armorstands.preset." + id);
    this.source = source;
    this.category = category;
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

  public String getId() {
    return this.id;
  }

  @Override
  public Pose toPose() {
    return new Pose(head, body, rightArm, leftArm, rightLeg, leftLeg);
  }

  public Text getDisplayName() {
    return label;
  }

  public Source getSource() {
    return source;
  }

  public Category getCategory() {
    return category;
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
    return Arrays.stream(PosePreset.values()).filter((pose) -> pose.id.equals(value)).findFirst().orElseGet(() -> {
      ArmorStandsMod.LOGGER.warn("Unknown id '{}'. Defaulting to DEFAULT.", Source.BEDROCK, value);
      return DEFAULT;
    });
  }

  public static List<PosePreset> getPresets() {
    return getPresets(Source.ALL, Category.ALL);
  }

  public static List<PosePreset> getPresets(Source source, Category category) {
    return Arrays.stream(PosePreset.values())
        .filter((pose) -> pose.getSource().matches(source) && pose.getCategory().matches(category))
        .sorted((a, b) -> {
          if (category != Category.ALL) {
            int sourceCompare = a.getSource().compareTo(b.getSource());
            if (sourceCompare != 0) {
              return sourceCompare;
            }
          } else {
            int categoryCompare = a.getCategory().compareTo(b.getCategory());
            if (categoryCompare != 0) {
              return categoryCompare;
            }
          }

          return a.getDisplayName().getString().compareTo(b.getDisplayName().getString());
        })
        .collect(Collectors.toList());
  }

  public enum Source {
    ALL("all"), VANILLA("vanilla"), VANILLA_TWEAKS("vanillaTweaks"), BEDROCK("bedrock"), ROUNDAROUND("roundaround");

    private final String id;

    Source(String id) {
      this.id = id;
    }

    public Text getDisplayName() {
      return Text.translatable("armorstands.source." + id);
    }

    public boolean matches(Source source) {
      return this == ALL || source == ALL || this == source;
    }

    public static List<Source> getSources() {
      return Arrays.stream(Source.values()).filter((source) -> {
        return source == Source.ALL ||
            Arrays.stream(PosePreset.values()).anyMatch((pose) -> pose.getSource() == source);
      }).collect(Collectors.toList());
    }
  }

  public enum Category {
    ALL("all"), STANDING("standing"), MOVING("moving"), DYNAMIC("dynamic"), SITTING("sitting"), OTHER("other");

    private final String id;

    Category(String id) {
      this.id = id;
    }

    public Text getDisplayName() {
      return Text.translatable("armorstands.category." + id);
    }

    public boolean matches(Category category) {
      return this == ALL || category == ALL || this == category;
    }

    public static List<Category> getCategories() {
      return Arrays.stream(Category.values())
          .filter((category) -> category == Category.ALL ||
              Arrays.stream(PosePreset.values()).anyMatch((pose) -> pose.getCategory() == category))
          .collect(Collectors.toList());
    }
  }
}
