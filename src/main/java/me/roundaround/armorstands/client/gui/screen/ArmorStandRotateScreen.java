package me.roundaround.armorstands.client.gui.screen;

import java.util.Locale;

import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.network.packet.c2s.AdjustYawPacket;
import me.roundaround.armorstands.network.packet.c2s.UtilityActionPacket;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class ArmorStandRotateScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.rotate");
  public static final int U_INDEX = 2;

  private static final int MINI_BUTTON_WIDTH = 24;
  private static final int MINI_BUTTON_HEIGHT = 16;
  private static final int BUTTON_WIDTH = 46;
  private static final int BUTTON_HEIGHT = 16;

  private LabelWidget playerFacingLabel;
  private LabelWidget playerRotationLabel;
  private LabelWidget standFacingLabel;
  private LabelWidget standRotationLabel;

  public ArmorStandRotateScreen(ArmorStandScreenHandler handler, ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);
    this.supportsUndoRedo = true;
  }

  @Override
  protected void initLeft() {
    super.initLeft();

    addLabel(LabelWidget.builder(
        Text.translatable("armorstands.current.player"),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    this.playerFacingLabel = addLabel(LabelWidget.builder(
        getCurrentFacingText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + 2 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    this.playerRotationLabel = addLabel(LabelWidget.builder(
        getCurrentRotationText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + 3 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    addLabel(LabelWidget.builder(
        Text.translatable("armorstands.current.stand"),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + 5 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    this.standFacingLabel = addLabel(LabelWidget.builder(
        getCurrentFacingText(this.armorStand),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + 6 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    this.standRotationLabel = addLabel(LabelWidget.builder(
        getCurrentRotationText(this.armorStand),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + 7 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    addLabel(LabelWidget.builder(
        Text.translatable("armorstands.face.label"),
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT - BETWEEN_PAD)
        .shiftForPadding()
        .justifiedLeft()
        .alignedBottom()
        .build());
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.face.toward"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.FACE_TOWARD);
        }));
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.face.away"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.FACE_AWAY);
        }));
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD + 2 * (BUTTON_WIDTH + BETWEEN_PAD),
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.face.with"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.FACE_WITH);
        }));
  }

  @Override
  protected void initRight() {
    super.initRight();

    addRowOfButtons(RotateDirection.CLOCKWISE, 1);
    addRowOfButtons(RotateDirection.COUNTERCLOCKWISE, 0);
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    playerFacingLabel.setText(getCurrentFacingText(client.player));
    playerRotationLabel.setText(getCurrentRotationText(client.player));
    standFacingLabel.setText(getCurrentFacingText(this.armorStand));
    standRotationLabel.setText(getCurrentRotationText(this.armorStand));
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
    int refX = this.width - SCREEN_EDGE_PAD - MINI_BUTTON_WIDTH;
    int refY = this.height - SCREEN_EDGE_PAD - MINI_BUTTON_HEIGHT
        - index * (2 * BETWEEN_PAD + MINI_BUTTON_HEIGHT + LabelWidget.HEIGHT_WITH_PADDING);
    String modifier = direction.equals(RotateDirection.CLOCKWISE) ? "+" : "-";

    addLabel(LabelWidget.builder(
        direction.getLabel(),
        this.width - SCREEN_EDGE_PAD,
        refY - BETWEEN_PAD)
        .justifiedRight()
        .alignedBottom()
        .shiftForPadding()
        .build());
    addDrawableChild(new ButtonWidget(
        refX - 3 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "1"),
        (button) -> {
          AdjustYawPacket.sendToServer(direction.offset() * 1);
        }));
    addDrawableChild(new ButtonWidget(
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "5"),
        (button) -> {
          AdjustYawPacket.sendToServer(direction.offset() * 5);
        }));
    addDrawableChild(new ButtonWidget(
        refX - 1 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "15"),
        (button) -> {
          AdjustYawPacket.sendToServer(direction.offset() * 15);
        }));
    addDrawableChild(new ButtonWidget(
        refX,
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        Text.literal(modifier + "45"),
        (button) -> {
          AdjustYawPacket.sendToServer(direction.offset() * 45);
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
