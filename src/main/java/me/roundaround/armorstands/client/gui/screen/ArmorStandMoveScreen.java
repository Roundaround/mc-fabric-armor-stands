package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.MoveButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.MoveMode;
import me.roundaround.armorstands.util.MoveUnits;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;

public class ArmorStandMoveScreen extends AbstractArmorStandScreen {
  private static final int MINI_BUTTON_WIDTH = 28;
  private static final int MINI_BUTTON_HEIGHT = 16;
  private static final int BUTTON_WIDTH = 46;
  private static final int BUTTON_HEIGHT = 16;

  private final HashMap<Direction, LabelWidget> directionLabels = new HashMap<>();
  private final ArrayList<MoveButtonWidget> moveButtons = new ArrayList<>();

  private LabelWidget playerPosLabel;
  private LabelWidget playerBlockPosLabel;
  private LabelWidget standPosLabel;
  private LabelWidget standBlockPosLabel;
  private LabelWidget facingLabel;
  private CyclingButtonWidget<MoveUnits> unitsButton;
  private MoveMode mode = MoveMode.RELATIVE;
  private MoveUnits units = MoveUnits.PIXELS;

  public ArmorStandMoveScreen(ArmorStandScreenHandler handler) {
    super(handler, ScreenType.MOVE.getDisplayName());
    this.supportsUndoRedo = true;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.MOVE;
  }

  @Override
  protected void initStart() {
    super.initStart();

    directionLabels.clear();
    moveButtons.clear();
  }

  @Override
  protected void initLeft() {
    super.initLeft();

    initCurrentStatus();
    initSnapButtons();
  }

  private void initCurrentStatus() {
    addLabel(LabelWidget.builder(Text.translatable("armorstands.current.player"), SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + LabelWidget.HEIGHT_WITH_PADDING
    ).alignedTop().justifiedLeft().shiftForPadding().build());

    this.playerPosLabel = addLabel(LabelWidget.builder(getCurrentPosText(client.player), SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + 2 * LabelWidget.HEIGHT_WITH_PADDING
    ).alignedTop().justifiedLeft().shiftForPadding().build());

    this.playerBlockPosLabel = addLabel(LabelWidget.builder(getCurrentBlockPosText(client.player), SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + 3 * LabelWidget.HEIGHT_WITH_PADDING
    ).alignedTop().justifiedLeft().shiftForPadding().build());

    addLabel(LabelWidget.builder(Text.translatable("armorstands.current.stand"), SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + 5 * LabelWidget.HEIGHT_WITH_PADDING
    ).alignedTop().justifiedLeft().shiftForPadding().build());

    this.standPosLabel = addLabel(LabelWidget.builder(getCurrentPosText(this.armorStand), SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + 6 * LabelWidget.HEIGHT_WITH_PADDING
    ).alignedTop().justifiedLeft().shiftForPadding().build());

    this.standBlockPosLabel = addLabel(LabelWidget.builder(getCurrentBlockPosText(this.armorStand), SCREEN_EDGE_PAD,
        SCREEN_EDGE_PAD + IconButtonWidget.HEIGHT + 7 * LabelWidget.HEIGHT_WITH_PADDING
    ).alignedTop().justifiedLeft().shiftForPadding().build());
  }

  private void initSnapButtons() {
    addLabel(LabelWidget.builder(Text.translatable("armorstands.move.snap"), SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * (BUTTON_HEIGHT + BETWEEN_PAD)
    ).shiftForPadding().justifiedLeft().alignedBottom().build());

    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.move.snap.standing"),
            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_STANDING)
        )
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(SCREEN_EDGE_PAD, this.height - SCREEN_EDGE_PAD - 2 * BUTTON_HEIGHT - BETWEEN_PAD)
        .build());
    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.move.snap.sitting"),
            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_SITTING)
        )
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD,
            this.height - SCREEN_EDGE_PAD - 2 * BUTTON_HEIGHT - BETWEEN_PAD
        )
        .build());
    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.move.snap.corner"),
            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CORNER)
        )
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(SCREEN_EDGE_PAD, this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT)
        .build());
    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.move.snap.center"),
            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CENTER)
        )
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(SCREEN_EDGE_PAD + BUTTON_WIDTH + BETWEEN_PAD, this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT)
        .build());
    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.move.snap.player"),
            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_PLAYER)
        )
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(SCREEN_EDGE_PAD + 2 * (BUTTON_WIDTH + BETWEEN_PAD), this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT)
        .build());
  }

  @Override
  protected void initRight() {
    super.initRight();

    int topOfMoveButtons = this.height - SCREEN_EDGE_PAD - 6 * MINI_BUTTON_HEIGHT - 5 * BETWEEN_PAD;

    addDrawableChild(CyclingButtonWidget.builder(MoveMode::getOptionValueText)
        .values(MoveMode.values())
        .initially(this.mode)
        .build(this.width - SCREEN_EDGE_PAD - 120,
            topOfMoveButtons - LabelWidget.HEIGHT_WITH_PADDING - 3 * BETWEEN_PAD - 2 * MINI_BUTTON_HEIGHT, 120,
            MINI_BUTTON_HEIGHT, MoveMode.getOptionLabelText(), (button, mode) -> {
              this.mode = mode;
              this.units = this.mode.getDefaultUnits();

              this.facingLabel.setText(
                  getCurrentFacingText(this.mode.equals(MoveMode.LOCAL_TO_STAND) ? this.armorStand : client.player));
              this.directionLabels.forEach((direction, label) -> {
                label.setText(this.mode.getDirectionText(direction));
              });

              this.unitsButton.setValue(this.units);
              this.moveButtons.forEach((moveButton) -> {
                moveButton.setUnits(this.units);
              });
            }
        ));

    this.unitsButton = addDrawableChild(CyclingButtonWidget.builder(MoveUnits::getOptionValueText)
        .values(MoveUnits.values())
        .initially(this.units)
        .build(this.width - SCREEN_EDGE_PAD - 120,
            topOfMoveButtons - LabelWidget.HEIGHT_WITH_PADDING - 2 * BETWEEN_PAD - MINI_BUTTON_HEIGHT, 120,
            MINI_BUTTON_HEIGHT, MoveUnits.getOptionLabelText(), (button, units) -> {
              this.units = units;
              this.moveButtons.forEach((moveButton) -> {
                moveButton.setUnits(this.units);
              });
            }
        ));

    this.facingLabel = addLabel(LabelWidget.builder(
        getCurrentFacingText(this.mode.equals(MoveMode.LOCAL_TO_STAND) ? this.armorStand : this.client.player),
        this.width - SCREEN_EDGE_PAD, topOfMoveButtons - BETWEEN_PAD
    ).shiftForPadding().alignedBottom().justifiedRight().build());

    Direction[] directions = new Direction[]{
        Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
    };
    for (int i = directions.length - 1; i >= 0; i--) {
      addRowOfButtons(directions[directions.length - i - 1], i);
    }
  }

  private void addRowOfButtons(Direction direction, int index) {
    int refX = this.width - SCREEN_EDGE_PAD;
    int refY = this.height - SCREEN_EDGE_PAD - MINI_BUTTON_HEIGHT - index * (BETWEEN_PAD + MINI_BUTTON_HEIGHT);

    directionLabels.put(direction, addLabel(
        LabelWidget.builder(this.mode.getDirectionText(direction), refX - 3 * (BETWEEN_PAD + MINI_BUTTON_WIDTH),
            refY + MINI_BUTTON_HEIGHT / 2
        ).justifiedRight().alignedMiddle().shiftForPadding().build()));

    moveButtons.add(addDrawableChild(
        new MoveButtonWidget(refX - 3 * MINI_BUTTON_WIDTH - 2 * BETWEEN_PAD, refY, MINI_BUTTON_WIDTH,
            MINI_BUTTON_HEIGHT, direction, 1, this.mode, this.units
        )));

    moveButtons.add(addDrawableChild(
        new MoveButtonWidget(refX - 2 * MINI_BUTTON_WIDTH - BETWEEN_PAD, refY, MINI_BUTTON_WIDTH, MINI_BUTTON_HEIGHT,
            direction, 2, this.mode, this.units
        )));

    moveButtons.add(addDrawableChild(
        new MoveButtonWidget(refX - MINI_BUTTON_WIDTH, refY, MINI_BUTTON_WIDTH, MINI_BUTTON_HEIGHT, direction, 3,
            this.mode, this.units
        )));
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    this.playerPosLabel.setText(getCurrentPosText(this.client.player));
    this.playerBlockPosLabel.setText(getCurrentBlockPosText(this.client.player));
    this.standPosLabel.setText(getCurrentPosText(this.armorStand));
    this.standBlockPosLabel.setText(getCurrentBlockPosText(this.armorStand));

    this.facingLabel.setText(
        getCurrentFacingText(this.mode.equals(MoveMode.LOCAL_TO_STAND) ? this.armorStand : this.client.player));
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
      default -> "posX";
    };
    Text towards = Text.translatable("armorstands.current.facing." + towardsI18n);
    return Text.translatable("armorstands.current.facing", currentFacing.toString(), towards.getString());
  }
}
