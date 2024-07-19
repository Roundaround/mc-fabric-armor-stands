package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.MoveMode;
import me.roundaround.armorstands.util.MoveUnits;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.layout.LinearLayoutWidget;
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
  private final ArrayList<MoveButtonRef> moveButtons = new ArrayList<>();

  private LabelWidget playerPosLabel;
  private LabelWidget playerBlockLabel;
  private LabelWidget standPosLabel;
  private LabelWidget standBlockLabel;
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

    this.initSnapButtons();
    this.initLabels();
    this.initCore();
  }

  private void initSnapButtons() {
    this.layout.bottomLeft.add(
        LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.move.snap")).build());

    LinearLayoutWidget firstRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    firstRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.move.snap.standing"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_STANDING)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    firstRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.move.snap.sitting"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_SITTING)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    this.layout.bottomLeft.add(firstRow);

    LinearLayoutWidget secondRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    secondRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.move.snap.corner"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CORNER)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    secondRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.move.snap.center"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CENTER)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    secondRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.move.snap.player"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_PLAYER)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    this.layout.bottomLeft.add(secondRow);
  }

  private void initLabels() {
    LinearLayoutWidget labels = LinearLayoutWidget.vertical().spacing(GuiUtil.PADDING).defaultOffAxisContentAlignEnd();

    LinearLayoutWidget player = LinearLayoutWidget.vertical().spacing(1).defaultOffAxisContentAlignEnd();
    player.add(
        LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.current.player")).alignRight().build());
    this.playerPosLabel = player.add(
        LabelWidget.builder(this.textRenderer, this.getCurrentPosText(this.client.player)).alignRight().build());
    this.playerBlockLabel = player.add(
        LabelWidget.builder(this.textRenderer, this.getCurrentBlockPosText(this.client.player)).alignRight().build());
    labels.add(player);

    LinearLayoutWidget stand = LinearLayoutWidget.vertical().spacing(1).defaultOffAxisContentAlignEnd();
    stand.add(
        LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.current.stand")).alignRight().build());
    this.standPosLabel = stand.add(
        LabelWidget.builder(this.textRenderer, this.getCurrentPosText(this.armorStand)).alignRight().build());
    this.standBlockLabel = stand.add(
        LabelWidget.builder(this.textRenderer, this.getCurrentBlockPosText(this.armorStand)).alignRight().build());
    labels.add(stand);

    this.layout.topRight.add(labels);
  }

  private void initCore() {
    this.layout.bottomRight.add(CyclingButtonWidget.builder(MoveMode::getOptionValueText)
        .values(MoveMode.values())
        .initially(this.mode)
        .build(MoveMode.getOptionLabelText(), (button, mode) -> {
          this.mode = mode;
          this.units = this.mode.getDefaultUnits();

          this.unitsButton.setValue(this.units);
          this.facingLabel.setText(this.getCurrentFacingText());
          this.directionLabels.forEach((direction, label) -> label.setText(this.mode.getDirectionText(direction)));
          this.moveButtons.forEach((moveButton) -> moveButton.setUnits(this.units));
        }));

    this.unitsButton = this.layout.bottomRight.add(CyclingButtonWidget.builder(MoveUnits::getOptionValueText)
        .values(MoveUnits.values())
        .initially(this.units)
        .build(MoveUnits.getOptionLabelText(), (button, units) -> {
          this.units = units;
          this.moveButtons.forEach((moveButton) -> moveButton.setUnits(this.units));
        }));

    this.facingLabel = this.layout.bottomRight.add(
        LabelWidget.builder(this.textRenderer, this.getCurrentFacingText()).build());

    Direction[] directions = new Direction[]{
        Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
    };
    for (Direction direction : directions) {
      LinearLayoutWidget row = LinearLayoutWidget.horizontal()
          .spacing(GuiUtil.PADDING)
          .mainAxisContentAlignEnd()
          .defaultOffAxisContentAlignCenter();

      LabelWidget label = LabelWidget.builder(this.textRenderer, this.mode.getDirectionText(direction)).build();
      this.directionLabels.put(direction, label);
      row.add(label);

      MoveButtonRef one = new MoveButtonRef(direction, 1, this.mode, this.units);
      this.moveButtons.add(one);
      row.add(one.getButton());

      this.layout.bottomRight.add(row);
    }
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    this.playerPosLabel.setText(this.getCurrentPosText(this.client.player));
    this.playerBlockLabel.setText(this.getCurrentBlockPosText(this.client.player));
    this.standPosLabel.setText(this.getCurrentPosText(this.armorStand));
    this.standBlockLabel.setText(this.getCurrentBlockPosText(this.armorStand));

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

  private Text getCurrentFacingText() {
    return this.getCurrentFacingText(this.mode.equals(MoveMode.LOCAL_TO_STAND) ? this.armorStand : this.client.player);
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

  private static class MoveButtonRef {
    private final ButtonWidget button;
    private final Direction direction;
    private final int amount;
    private final MoveMode mode;

    private MoveUnits units;

    public MoveButtonRef(Direction direction, int amount, MoveMode mode, MoveUnits units) {
      this.direction = direction;
      this.amount = amount;
      this.mode = mode;
      this.units = units;

      this.button = ButtonWidget.builder(this.getMessage(), this::onPress)
          .size(MINI_BUTTON_WIDTH, MINI_BUTTON_HEIGHT)
          .build();
    }

    public ButtonWidget getButton() {
      return this.button;
    }

    public void setUnits(MoveUnits units) {
      this.units = units;
      this.button.setMessage(this.units.getButtonText(this.amount));
    }

    private Text getMessage() {
      return this.units.getButtonText(this.amount);
    }

    private void onPress(ButtonWidget button) {
      ClientNetworking.sendAdjustPosPacket(this.direction, this.amount, this.mode, this.units);
    }
  }
}
