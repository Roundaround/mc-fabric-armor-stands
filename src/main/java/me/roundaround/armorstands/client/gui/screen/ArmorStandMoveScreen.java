package me.roundaround.armorstands.client.gui.screen;

import java.util.List;

import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.MoveButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ArmorStandMoveScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.move");
  public static final int U_INDEX = 1;

  private static final int MINI_BUTTON_WIDTH = 16;
  private static final int MINI_BUTTON_HEIGHT = 16;
  private static final int BUTTON_WIDTH = 46;
  private static final int BUTTON_HEIGHT = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  private LabelWidget playerPosLabel;
  private LabelWidget playerBlockPosLabel;
  private LabelWidget playerFacingLabel;
  private LabelWidget standPosLabel;
  private LabelWidget standBlockPosLabel;

  public ArmorStandMoveScreen(ArmorStandScreenHandler handler, ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);
    this.supportsUndoRedo = true;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.MOVE;
  }

  @Override
  public ScreenConstructor<?> getNextScreen() {
    return ArmorStandRotateScreen::new;
  }

  @Override
  public ScreenConstructor<?> getPreviousScreen() {
    return ArmorStandUtilitiesScreen::new;
  }

  @Override
  public void init() {
    super.init();

    addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.current.player"),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    this.playerPosLabel = addDrawable(LabelWidget.builder(
        getCurrentPosText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    this.playerBlockPosLabel = addDrawable(LabelWidget.builder(
        getCurrentBlockPosText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 2 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    this.playerFacingLabel = addDrawable(LabelWidget.builder(
        getCurrentFacingText(client.player),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 3 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.current.stand"),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 5 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    this.standPosLabel = addDrawable(LabelWidget.builder(
        getCurrentPosText(this.armorStand),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 6 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    this.standBlockPosLabel = addDrawable(LabelWidget.builder(
        getCurrentBlockPosText(this.armorStand),
        SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + 7 * LabelWidget.HEIGHT_WITH_PADDING)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());

    addDrawable(LabelWidget.builder(
        Text.translatable("armorstands.snap.label"),
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * (BUTTON_HEIGHT + BETWEEN_PAD))
        .shiftForPadding()
        .justifiedLeft()
        .alignedBottom()
        .build());
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * BUTTON_HEIGHT - BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.standing"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_STANDING);
        }));
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * BUTTON_HEIGHT - BETWEEN_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.sitting"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_SITTING);
        }));
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.corner"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CORNER);
        }));
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.center"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CENTER);
        }));
    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD + 2 * (BUTTON_WIDTH + BETWEEN_PAD),
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.snap.player"),
        (button) -> {
          ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_PLAYER);
        }));

    initNavigationButtons(List.of(
        ScreenFactory.create(
            ArmorStandUtilitiesScreen.TITLE,
            ArmorStandUtilitiesScreen.U_INDEX,
            ArmorStandUtilitiesScreen::new),
        ScreenFactory.create(
            ArmorStandMoveScreen.TITLE,
            ArmorStandMoveScreen.U_INDEX),
        ScreenFactory.create(
            ArmorStandRotateScreen.TITLE,
            ArmorStandRotateScreen.U_INDEX,
            ArmorStandRotateScreen::new),
        ScreenFactory.create(
            ArmorStandPoseScreen.TITLE,
            ArmorStandPoseScreen.U_INDEX,
            ArmorStandPoseScreen::new),
        ScreenFactory.create(
            ArmorStandPresetsScreen.TITLE,
            ArmorStandPresetsScreen.U_INDEX,
            ArmorStandPresetsScreen::new),
        ScreenFactory.create(
            ArmorStandInventoryScreen.TITLE,
            ArmorStandInventoryScreen.U_INDEX,
            ArmorStandInventoryScreen::new)));

    addRowOfButtons(Text.translatable("armorstands.move.up"), Direction.UP, 5);
    addRowOfButtons(Text.translatable("armorstands.move.down"), Direction.DOWN, 4);
    addRowOfButtons(Text.translatable("armorstands.move.south"), Direction.SOUTH, 3);
    addRowOfButtons(Text.translatable("armorstands.move.north"), Direction.NORTH, 2);
    addRowOfButtons(Text.translatable("armorstands.move.east"), Direction.EAST, 1);
    addRowOfButtons(Text.translatable("armorstands.move.west"), Direction.WEST, 0);
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    playerPosLabel.setText(getCurrentPosText(client.player));
    playerBlockPosLabel.setText(getCurrentBlockPosText(client.player));
    playerFacingLabel.setText(getCurrentFacingText(client.player));
    standPosLabel.setText(getCurrentPosText(this.armorStand));
    standBlockPosLabel.setText(getCurrentBlockPosText(this.armorStand));
  }

  private Text getCurrentPosText(Entity entity) {
    String xStr = String.format("%.2f", entity.getX());
    String yStr = String.format("%.2f", entity.getY());
    String zStr = String.format("%.2f", entity.getZ());
    return Text.translatable("armorstands.current.position", xStr, yStr, zStr);
  }

  private Text getCurrentBlockPosText(Entity entity) {
    BlockPos pos = entity.getBlockPos();
    return Text.translatable("armorstands.current.block", pos.getX(), pos.getY(), pos.getZ());
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

  private void addRowOfButtons(Text label, Direction direction, int index) {
    int refX = this.width - SCREEN_EDGE_PAD - MINI_BUTTON_WIDTH;
    int refY = this.height - SCREEN_EDGE_PAD - MINI_BUTTON_HEIGHT - index * (BETWEEN_PAD + MINI_BUTTON_HEIGHT);

    addDrawable(LabelWidget.builder(
        label,
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH) - 4,
        refY + MINI_BUTTON_HEIGHT / 2)
        .justifiedRight()
        .alignedMiddle()
        .build());

    MoveButtonWidget one = new MoveButtonWidget(
        refX - 2 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        direction,
        1);

    MoveButtonWidget three = new MoveButtonWidget(
        refX - 1 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        direction,
        3);

    MoveButtonWidget eight = new MoveButtonWidget(
        refX,
        refY,
        MINI_BUTTON_WIDTH,
        MINI_BUTTON_HEIGHT,
        direction,
        8);

    addDrawableChild(one);
    addDrawableChild(three);
    addDrawableChild(eight);
  }
}
