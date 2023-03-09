package me.roundaround.armorstands.client.gui.screen;

import java.util.Locale;

import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.RotateSliderWidget;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.network.packet.c2s.AdjustYawPacket;
import me.roundaround.armorstands.network.packet.c2s.SetYawPacket;
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

  private static final int BUTTON_WIDTH = 46;
  private static final int BUTTON_HEIGHT = 16;
  private static final int DIRECTION_BUTTON_WIDTH = 70;
  private static final int MINI_BUTTON_WIDTH = 24;
  private static final int MINI_BUTTON_HEIGHT = 16;
  private static final int SLIDER_WIDTH = 4 * MINI_BUTTON_WIDTH + 3 * BETWEEN_PAD;
  private static final int SLIDER_HEIGHT = 20;

  private LabelWidget playerFacingLabel;
  private LabelWidget playerRotationLabel;
  private LabelWidget standFacingLabel;
  private LabelWidget standRotationLabel;
  private RotateSliderWidget rotateSlider;

  public ArmorStandRotateScreen(ArmorStandScreenHandler handler, ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);
    this.supportsUndoRedo = true;
  }

  @Override
  protected void initLeft() {
    super.initLeft();

    initCurrentStatus();
    initSnapButtons();
    initFaceButtons();
  }

  private void initCurrentStatus() {
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
  }

  private void initSnapButtons() {
    addLabel(LabelWidget.builder(
        Text.translatable("armorstands.rotate.snap"),
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD
            - 3 * BUTTON_HEIGHT
            - 4 * BETWEEN_PAD
            - LabelWidget.HEIGHT_WITH_PADDING)
        .shiftForPadding()
        .justifiedLeft()
        .alignedBottom()
        .build());

    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD
            - 3 * BUTTON_HEIGHT
            - 3 * BETWEEN_PAD
            - LabelWidget.HEIGHT_WITH_PADDING,
        DIRECTION_BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.rotate.snap." + Direction.SOUTH.getName()),
        (button) -> {
          SetYawPacket.sendToServer(MathHelper.wrapDegrees(Direction.SOUTH.asRotation()));
        }));
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD + DIRECTION_BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - 3 * BUTTON_HEIGHT
            - 3 * BETWEEN_PAD
            - LabelWidget.HEIGHT_WITH_PADDING,
        DIRECTION_BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.rotate.snap." + Direction.NORTH.getName()),
        (button) -> {
          SetYawPacket.sendToServer(MathHelper.wrapDegrees(Direction.NORTH.asRotation()));
        }));

    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD
            - 2 * BUTTON_HEIGHT
            - 2 * BETWEEN_PAD
            - LabelWidget.HEIGHT_WITH_PADDING,
        DIRECTION_BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.rotate.snap." + Direction.EAST.getName()),
        (button) -> {
          SetYawPacket.sendToServer(MathHelper.wrapDegrees(Direction.EAST.asRotation()));
        }));
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD + DIRECTION_BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - 2 * BUTTON_HEIGHT
            - 2 * BETWEEN_PAD
            - LabelWidget.HEIGHT_WITH_PADDING,
        DIRECTION_BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.rotate.snap." + Direction.WEST.getName()),
        (button) -> {
          SetYawPacket.sendToServer(MathHelper.wrapDegrees(Direction.WEST.asRotation()));
        }));
  }

  private void initFaceButtons() {
    addLabel(LabelWidget.builder(
        Text.translatable("armorstands.rotate.face"),
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
        Text.translatable("armorstands.rotate.face.toward"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.FACE_TOWARD);
        }));
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.rotate.face.away"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.FACE_AWAY);
        }));
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD + 2 * (BUTTON_WIDTH + BETWEEN_PAD),
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.rotate.face.with"),
        (button) -> {
          UtilityActionPacket.sendToServer(UtilityAction.FACE_WITH);
        }));
  }

  @Override
  protected void initRight() {
    super.initRight();

    addRowOfButtons(RotateDirection.CLOCKWISE, 1);
    addRowOfButtons(RotateDirection.COUNTERCLOCKWISE, 0);

    this.rotateSlider = addDrawableChild(new RotateSliderWidget(
        this,
        this.width - SCREEN_EDGE_PAD - SLIDER_WIDTH,
        this.height - SCREEN_EDGE_PAD - SLIDER_HEIGHT,
        SLIDER_WIDTH,
        SLIDER_HEIGHT,
        this.armorStand));
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    this.playerFacingLabel.setText(getCurrentFacingText(this.client.player));
    this.playerRotationLabel.setText(getCurrentRotationText(this.client.player));
    this.standFacingLabel.setText(getCurrentFacingText(this.armorStand));
    this.standRotationLabel.setText(getCurrentRotationText(this.armorStand));
    this.rotateSlider.tick();
  }

  @Override
  public void updateYawOnClient(float yaw) {
    if (this.rotateSlider != null && this.rotateSlider.isPending(this)) {
      return;
    }

    super.updateYawOnClient(yaw);

    if (this.rotateSlider != null) {
      this.rotateSlider.setAngle(yaw);
    }
  }

  @Override
  public void onPong() {
    super.onPong();

    if (this.rotateSlider != null) {
      this.rotateSlider.onPong();
    }
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
    int refY = this.height - SCREEN_EDGE_PAD
        - SLIDER_HEIGHT - 2 * BETWEEN_PAD
        - MINI_BUTTON_HEIGHT
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
