package me.roundaround.armorstands.client.gui.page;

import java.util.Locale;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.MiniButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.UtilityAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class ArmorStandRotatePage extends AbstractArmorStandPage {
  private static final int MINI_BUTTON_WIDTH = 24;
  private static final int MINI_BUTTON_HEIGHT = 16;
  private static final int BUTTON_WIDTH = 46;
  private static final int BUTTON_HEIGHT = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  private LabelWidget playerFacingLabel;
  private LabelWidget playerRotationLabel;
  private LabelWidget standFacingLabel;
  private LabelWidget standRotationLabel;

  public ArmorStandRotatePage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen, Text.translatable("armorstands.page.rotate"), 2);
  }

  @Override
  public void tick() {
    playerFacingLabel.setText(getCurrentFacingText(client.player));
    playerRotationLabel.setText(getCurrentRotationText(client.player));
    standFacingLabel.setText(getCurrentFacingText(screen.getArmorStand()));
    standRotationLabel.setText(getCurrentRotationText(screen.getArmorStand()));
  }

  @Override
  public void preInit() {
    screen.addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.current.player"),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding());

    playerFacingLabel = LabelWidget.builder(
        getCurrentFacingText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build();
    screen.addDrawable(playerFacingLabel);

    playerRotationLabel = LabelWidget.builder(
        getCurrentRotationText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 2 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build();
    screen.addDrawable(playerRotationLabel);

    screen.addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.face.label"),
        SCREEN_EDGE_PAD,
        screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT - BETWEEN_PAD)
        .shiftForPadding()
        .justifiedLeft()
        .alignedBottom());
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD,
        screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.face.toward"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.FACE_TOWARD);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.face.away"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.FACE_AWAY);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        SCREEN_EDGE_PAD + 2 * (BUTTON_WIDTH + BETWEEN_PAD),
        screen.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.face.with"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.FACE_WITH);
        }));
  }

  @Override
  public void postInit() {
    screen.addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.current.stand"),
        screen.width - SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD)
        .alignedTop()
        .justifiedRight()
        .shiftForPadding());

    standFacingLabel = LabelWidget.builder(
        getCurrentFacingText(screen.getArmorStand()),
        screen.width - SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedRight()
        .shiftForPadding()
        .build();
    screen.addDrawable(standFacingLabel);

    standRotationLabel = LabelWidget.builder(
        getCurrentRotationText(screen.getArmorStand()),
        screen.width - SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 2 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedRight()
        .shiftForPadding()
        .build();
    screen.addDrawable(standRotationLabel);

    addRowOfButtons(RotateDirection.CLOCKWISE, 1);
    addRowOfButtons(RotateDirection.COUNTERCLOCKWISE, 0);
  }

  private Text getCurrentFacingText(Entity entity) {
    float currentRotation = entity.getYaw();
    Direction currentFacing = Direction.fromRotation(currentRotation);
    String towardsI18n = switch (currentFacing) {
      case NORTH -> "negZ";
      case SOUTH -> "posZ";
      case WEST -> "negX";
      case EAST -> "posX";
      default -> "posX";
    };
    Text towards = Text.translatable("armorstands.current.facing." + towardsI18n);
    return Text.translatable("armorstands.current.facing", currentFacing, towards.getString());
  }

  private Text getCurrentRotationText(Entity entity) {
    float currentRotation = entity.getYaw();
    return Text.translatable("armorstands.current.rotation",
        String.format(Locale.ROOT, "%.1f", Float.valueOf(MathHelper.wrapDegrees(currentRotation))));
  }

  private void addRowOfButtons(RotateDirection direction, int index) {
    int refX = screen.width - SCREEN_EDGE_PAD - MINI_BUTTON_WIDTH;
    int refY = screen.height - SCREEN_EDGE_PAD - MINI_BUTTON_HEIGHT
        - index * (2 * BETWEEN_PAD + MINI_BUTTON_HEIGHT + LabelWidget.HEIGHT_WITH_PADDING);
    String modifier = direction.equals(RotateDirection.CLOCKWISE) ? "+" : "-";

    screen.addDrawable(LabelWidget.builder(
        direction.getLabel(),
        screen.width - SCREEN_EDGE_PAD,
        refY - BETWEEN_PAD)
        .justifiedRight()
        .alignedBottom()
        .shiftForPadding());
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 3 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "1"),
        (button) -> {
          ClientNetworking.sendAdjustYawPacket(direction.offset() * 1);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "5"),
        (button) -> {
          ClientNetworking.sendAdjustYawPacket(direction.offset() * 5);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX - 1 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "15"),
        (button) -> {
          ClientNetworking.sendAdjustYawPacket(direction.offset() * 15);
        }));
    screen.addDrawableChild(new MiniButtonWidget(
        refX,
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "45"),
        (button) -> {
          ClientNetworking.sendAdjustYawPacket(direction.offset() * 45);
        }));
  }

  public static enum RotateDirection {
    CLOCKWISE(1, "armorstands.rotate.clockwise"),
    COUNTERCLOCKWISE(-1, "armorstands.rotate.counter");

    private final int offset;
    private final Text label;

    private RotateDirection(int offset, String i18n) {
      this.offset = offset;
      label = Text.translatable(i18n);
    }

    public int offset() {
      return offset;
    }

    public Text getLabel() {
      return label;
    }

    public String toString() {
      return label.getString();
    }

    public RotateDirection getOpposite() {
      return this == CLOCKWISE ? COUNTERCLOCKWISE : CLOCKWISE;
    }
  }
}
