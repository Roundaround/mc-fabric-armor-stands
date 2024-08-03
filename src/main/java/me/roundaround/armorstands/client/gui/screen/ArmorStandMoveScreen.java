package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.MoveMode;
import me.roundaround.armorstands.util.MoveUnits;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.FillerWidget;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.util.Spacing;
import me.roundaround.roundalib.client.gui.widget.drawable.LabelWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArmorStandMoveScreen extends AbstractArmorStandScreen {
  private static final int MINI_BUTTON_WIDTH = 28;
  private static final int BUTTON_WIDTH = 46;
  private static final int BUTTON_HEIGHT = 16;
  private static final int LARGE_BUTTON_WIDTH = 118;
  private static final List<Direction> DIRECTIONS = List.of(
      Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST);

  private final HashMap<Direction, LabelWidget> directionLabels = new HashMap<>();
  private final ArrayList<MoveButtonRef> moveButtons = new ArrayList<>();

  private LabelWidget playerPosLabel;
  private LabelWidget playerBlockLabel;
  private LabelWidget standPosLabel;
  private LabelWidget standBlockLabel;
  private Direction prevFacing;
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
  protected void populateLayout() {
    super.populateLayout();

    this.initTopLeft();
    this.initBottomLeft();
    this.initBottomRight();
  }

  private void initTopLeft() {
    LinearLayoutWidget labels = LinearLayoutWidget.vertical()
        .spacing(3 * GuiUtil.PADDING)
        .defaultOffAxisContentAlignStart();

    LinearLayoutWidget player = LinearLayoutWidget.vertical().spacing(1).defaultOffAxisContentAlignStart();
    player.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.current.player"))
        .bgColor(BACKGROUND_COLOR)
        .build());
    this.playerPosLabel = player.add(
        LabelWidget.builder(this.textRenderer, getCurrentPosText(this.getPlayer())).bgColor(BACKGROUND_COLOR).build());
    this.playerBlockLabel = player.add(LabelWidget.builder(this.textRenderer, getCurrentBlockPosText(this.getPlayer()))
        .bgColor(BACKGROUND_COLOR)
        .build());
    labels.add(player);

    LinearLayoutWidget stand = LinearLayoutWidget.vertical().spacing(1).defaultOffAxisContentAlignStart();
    stand.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.current.stand"))
        .bgColor(BACKGROUND_COLOR)
        .build());
    this.standPosLabel = stand.add(LabelWidget.builder(this.textRenderer, getCurrentPosText(this.getArmorStand()))
        .bgColor(BACKGROUND_COLOR)
        .build());
    this.standBlockLabel = stand.add(
        LabelWidget.builder(this.textRenderer, getCurrentBlockPosText(this.getArmorStand()))
            .bgColor(BACKGROUND_COLOR)
            .build());
    labels.add(stand);

    this.layout.topLeft.add(labels, (configurator) -> configurator.margin(Spacing.of(4 * GuiUtil.PADDING, 0, 0, 0)));
  }

  private void initBottomLeft() {
    LinearLayoutWidget snaps = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignStart();

    snaps.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.move.snap"))
        .bgColor(BACKGROUND_COLOR)
        .build());

    LinearLayoutWidget firstRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    firstRow.add(ButtonWidget.builder(Text.translatable("armorstands.move.snap.standing"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_STANDING)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    firstRow.add(ButtonWidget.builder(Text.translatable("armorstands.move.snap.sitting"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_SITTING)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    snaps.add(firstRow);

    LinearLayoutWidget secondRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    secondRow.add(ButtonWidget.builder(Text.translatable("armorstands.move.snap.corner"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CORNER)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    secondRow.add(ButtonWidget.builder(Text.translatable("armorstands.move.snap.center"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CENTER)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    secondRow.add(ButtonWidget.builder(Text.translatable("armorstands.move.snap.player"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_PLAYER)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    snaps.add(secondRow);

    this.layout.bottomLeft.add(snaps);
  }

  private void initBottomRight() {
    this.layout.bottomRight.defaultOffAxisContentAlignEnd();

    this.layout.bottomRight.add(CyclingButtonWidget.builder(MoveMode::getOptionValueText)
            .values(MoveMode.values())
            .initially(this.mode)
            .build(MoveMode.getOptionLabelText(), this::onMoveModeChange),
        (parent, self) -> self.setDimensions(LARGE_BUTTON_WIDTH, BUTTON_HEIGHT)
    );

    this.unitsButton = this.layout.bottomRight.add(CyclingButtonWidget.builder(MoveUnits::getOptionValueText)
            .values(MoveUnits.values())
            .initially(this.units)
            .build(MoveUnits.getOptionLabelText(), this::onMoveUnitsChange),
        (parent, self) -> self.setDimensions(LARGE_BUTTON_WIDTH, BUTTON_HEIGHT)
    );

    this.layout.bottomRight.add(FillerWidget.ofHeight(2 * GuiUtil.PADDING));

    this.facingLabel = this.layout.bottomRight.add(
        LabelWidget.builder(this.textRenderer, getFacingText(this.getCurrentFacing()))
            .bgColor(BACKGROUND_COLOR)
            .build());

    DIRECTIONS.forEach(this::initDirectionRow);
  }

  private void initDirectionRow(Direction direction) {
    LinearLayoutWidget row = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignCenter();

    LabelWidget label = LabelWidget.builder(this.textRenderer, this.mode.getDirectionText(direction))
        .bgColor(BACKGROUND_COLOR)
        .build();
    this.directionLabels.put(direction, label);
    row.add(label);

    row.add(this.createMoveButton(direction, 1));
    row.add(this.createMoveButton(direction, 2));
    row.add(this.createMoveButton(direction, 3));

    this.layout.bottomRight.add(row);
  }

  private ButtonWidget createMoveButton(Direction direction, int amount) {
    MoveButtonRef ref = new MoveButtonRef(direction, amount, this.mode, this.units);
    this.moveButtons.add(ref);
    return ref.getButton();
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    this.playerPosLabel.setText(getCurrentPosText(this.getPlayer()));
    this.playerBlockLabel.setText(getCurrentBlockPosText(this.getPlayer()));
    this.standPosLabel.setText(getCurrentPosText(this.getArmorStand()));
    this.standBlockLabel.setText(getCurrentBlockPosText(this.getArmorStand()));

    Direction currentFacing = this.getCurrentFacing();
    if (!currentFacing.equals(this.prevFacing)) {
      this.facingLabel.setText(getFacingText(this.getCurrentFacing()));
      this.layout.refreshPositions();
    }
    this.prevFacing = currentFacing;
  }

  private void onMoveModeChange(CyclingButtonWidget<MoveMode> button, MoveMode value) {
    this.mode = value;
    this.units = this.mode.getDefaultUnits();
    this.unitsButton.setValue(this.units);
    this.prevFacing = this.getCurrentFacing();
    this.facingLabel.setText(getFacingText(this.prevFacing));
    this.moveButtons.forEach((moveButton) -> {
      moveButton.setMode(this.mode);
      moveButton.setUnits(this.units);
    });

    this.directionLabels.forEach((direction, label) -> label.setText(this.mode.getDirectionText(direction)));
    this.layout.bottomRight.refreshPositions();
    this.layout.refreshPositions();
  }

  private void onMoveUnitsChange(CyclingButtonWidget<MoveUnits> button, MoveUnits value) {
    this.units = value;
    this.moveButtons.forEach((moveButton) -> moveButton.setUnits(this.units));
  }

  private Direction getCurrentFacing() {
    Entity entity = this.mode.equals(MoveMode.LOCAL_TO_STAND) ? this.getArmorStand() : this.getPlayer();
    return Direction.fromRotation(entity.getYaw());
  }

  private static class MoveButtonRef {
    private final ButtonWidget button;
    private final Direction direction;
    private final int amount;

    private MoveMode mode;
    private MoveUnits units;

    public MoveButtonRef(Direction direction, int amount, MoveMode mode, MoveUnits units) {
      this.direction = direction;
      this.amount = amount;
      this.mode = mode;
      this.units = units;

      this.button = ButtonWidget.builder(this.getMessage(), this::onPress)
          .size(MINI_BUTTON_WIDTH, BUTTON_HEIGHT)
          .build();
    }

    public ButtonWidget getButton() {
      return this.button;
    }

    public void setUnits(MoveUnits units) {
      this.units = units;
      this.button.setMessage(this.units.getButtonText(this.amount));
    }

    public void setMode(MoveMode mode) {
      this.mode = mode;
    }

    private Text getMessage() {
      return this.units.getButtonText(this.amount);
    }

    private void onPress(ButtonWidget button) {
      ClientNetworking.sendAdjustPosPacket(this.direction, this.amount, this.mode, this.units);
    }
  }
}
