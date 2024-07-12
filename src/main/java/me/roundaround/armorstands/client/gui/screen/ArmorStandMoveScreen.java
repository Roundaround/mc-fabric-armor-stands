package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.MoveButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.MoveMode;
import me.roundaround.armorstands.util.MoveUnits;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
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
    ArrayList<Text> playerLines = new ArrayList<>();
    playerLines.add(Text.translatable("armorstands.current.player"));
    playerLines.add(this.getCurrentPosText(this.client.player));
    playerLines.add(this.getCurrentBlockPosText(this.client.player));
    this.addDrawable(LabelWidget.builder(this.textRenderer, playerLines)
        .refPosition(this.width, 0)
        .alignedTop()
        .justifiedRight()
        .build());

    ArrayList<Text> standLines = new ArrayList<>();
    standLines.add(Text.translatable("armorstands.current.stand"));
    standLines.add(this.getCurrentPosText(this.armorStand));
    standLines.add(this.getCurrentBlockPosText(this.armorStand));
    this.addDrawable(LabelWidget.builder(this.textRenderer, standLines)
        .refPosition(this.width, 0)
        .alignedTop()
        .justifiedRight()
        .build());

    addLabel(LabelWidget.builder(Text.translatable("armorstands.current.stand"), GuiUtil.PADDING,
        GuiUtil.PADDING + IconButtonWidget.HEIGHT + 5 * LabelWidget.HEIGHT_WITH_PADDING
    ).alignedTop().justifiedLeft().shiftForPadding().build());

    this.standPosLabel = addLabel(LabelWidget.builder(getCurrentPosText(this.armorStand), GuiUtil.PADDING,
        GuiUtil.PADDING + IconButtonWidget.HEIGHT + 6 * LabelWidget.HEIGHT_WITH_PADDING
    ).alignedTop().justifiedLeft().shiftForPadding().build());

    this.standBlockPosLabel = addLabel(LabelWidget.builder(getCurrentBlockPosText(this.armorStand), GuiUtil.PADDING,
        GuiUtil.PADDING + IconButtonWidget.HEIGHT + 7 * LabelWidget.HEIGHT_WITH_PADDING
    ).alignedTop().justifiedLeft().shiftForPadding().build());
  }

  private void initSnapButtons() {
    addLabel(LabelWidget.builder(Text.translatable("armorstands.move.snap"), GuiUtil.PADDING,
        this.height - GuiUtil.PADDING - 2 * (BUTTON_HEIGHT + (GuiUtil.PADDING / 2))
    ).shiftForPadding().justifiedLeft().alignedBottom().build());

    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.move.snap.standing"),
            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_STANDING)
        )
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(GuiUtil.PADDING, this.height - GuiUtil.PADDING - 2 * BUTTON_HEIGHT - (GuiUtil.PADDING / 2))
        .build());
    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.move.snap.sitting"),
            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_SITTING)
        )
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(GuiUtil.PADDING + BUTTON_WIDTH + (GuiUtil.PADDING / 2),
            this.height - GuiUtil.PADDING - 2 * BUTTON_HEIGHT - (GuiUtil.PADDING / 2)
        )
        .build());
    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.move.snap.corner"),
            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CORNER)
        )
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(GuiUtil.PADDING, this.height - GuiUtil.PADDING - BUTTON_HEIGHT)
        .build());
    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.move.snap.center"),
            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_CENTER)
        )
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(GuiUtil.PADDING + BUTTON_WIDTH + (GuiUtil.PADDING / 2), this.height - GuiUtil.PADDING - BUTTON_HEIGHT)
        .build());
    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.move.snap.player"),
            (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.SNAP_PLAYER)
        )
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(GuiUtil.PADDING + 2 * (BUTTON_WIDTH + (GuiUtil.PADDING / 2)), this.height - GuiUtil.PADDING - BUTTON_HEIGHT)
        .build());
  }

  @Override
  protected void initRight() {
    super.initRight();

    int topOfMoveButtons = this.height - GuiUtil.PADDING - 6 * MINI_BUTTON_HEIGHT - 5 * (GuiUtil.PADDING / 2);

    addDrawableChild(CyclingButtonWidget.builder(MoveMode::getOptionValueText)
        .values(MoveMode.values())
        .initially(this.mode)
        .build(this.width - GuiUtil.PADDING - 120,
            topOfMoveButtons - LabelWidget.HEIGHT_WITH_PADDING - 3 * (GuiUtil.PADDING / 2) - 2 * MINI_BUTTON_HEIGHT, 120,
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
        .build(this.width - GuiUtil.PADDING - 120,
            topOfMoveButtons - LabelWidget.HEIGHT_WITH_PADDING - 2 * (GuiUtil.PADDING / 2) - MINI_BUTTON_HEIGHT, 120,
            MINI_BUTTON_HEIGHT, MoveUnits.getOptionLabelText(), (button, units) -> {
              this.units = units;
              this.moveButtons.forEach((moveButton) -> {
                moveButton.setUnits(this.units);
              });
            }
        ));

    this.facingLabel = addLabel(LabelWidget.builder(
        getCurrentFacingText(this.mode.equals(MoveMode.LOCAL_TO_STAND) ? this.armorStand : this.client.player),
        this.width - GuiUtil.PADDING, topOfMoveButtons - (GuiUtil.PADDING / 2)
    ).shiftForPadding().alignedBottom().justifiedRight().build());

    Direction[] directions = new Direction[]{
        Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
    };
    for (int i = directions.length - 1; i >= 0; i--) {
      addRowOfButtons(directions[directions.length - i - 1], i);
    }
  }

  private void addRowOfButtons(Direction direction, int index) {
    int refX = this.width - GuiUtil.PADDING;
    int refY = this.height - GuiUtil.PADDING - MINI_BUTTON_HEIGHT - index * ((GuiUtil.PADDING / 2) + MINI_BUTTON_HEIGHT);

    directionLabels.put(direction, addLabel(
        LabelWidget.builder(this.mode.getDirectionText(direction), refX - 3 * ((GuiUtil.PADDING / 2) + MINI_BUTTON_WIDTH),
            refY + MINI_BUTTON_HEIGHT / 2
        ).justifiedRight().alignedMiddle().shiftForPadding().build()));

    moveButtons.add(addDrawableChild(
        new MoveButtonWidget(refX - 3 * MINI_BUTTON_WIDTH - 2 * (GuiUtil.PADDING / 2), refY, MINI_BUTTON_WIDTH,
            MINI_BUTTON_HEIGHT, direction, 1, this.mode, this.units
        )));

    moveButtons.add(addDrawableChild(
        new MoveButtonWidget(refX - 2 * MINI_BUTTON_WIDTH - (GuiUtil.PADDING / 2), refY, MINI_BUTTON_WIDTH, MINI_BUTTON_HEIGHT,
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
