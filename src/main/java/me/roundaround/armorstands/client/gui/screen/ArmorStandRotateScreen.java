package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.client.gui.widget.RotateSliderWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.FillerWidget;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.util.Spacing;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.Locale;

public class ArmorStandRotateScreen extends AbstractArmorStandScreen {
  private static final int BUTTON_WIDTH = 46;
  private static final int BUTTON_HEIGHT = 16;
  private static final int DIRECTION_BUTTON_WIDTH = 70;
  private static final int MINI_BUTTON_WIDTH = 24;
  private static final int TINY_BUTTON_WIDTH = 16;
  private static final int SLIDER_WIDTH = 4 * MINI_BUTTON_WIDTH + 3 * (GuiUtil.PADDING / 2);

  private LabelWidget playerFacingLabel;
  private LabelWidget playerRotationLabel;
  private LabelWidget standFacingLabel;
  private LabelWidget standRotationLabel;
  private RotateSliderWidget rotateSlider;

  public ArmorStandRotateScreen(ArmorStandScreenHandler handler) {
    super(handler, ScreenType.ROTATE.getDisplayName());
    this.supportsUndoRedo = true;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.ROTATE;
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
    player.add(me.roundaround.roundalib.client.gui.widget.LabelWidget.builder(this.textRenderer,
        Text.translatable("armorstands.current.player")
    ).build());
    this.playerFacingLabel = player.add(
        me.roundaround.roundalib.client.gui.widget.LabelWidget.builder(this.textRenderer,
            this.getCurrentFacingText(this.getPlayer())
        ).build());
    this.playerRotationLabel = player.add(
        me.roundaround.roundalib.client.gui.widget.LabelWidget.builder(this.textRenderer,
            this.getCurrentRotationText(this.getPlayer())
        ).build());
    labels.add(player);

    LinearLayoutWidget stand = LinearLayoutWidget.vertical().spacing(1).defaultOffAxisContentAlignStart();
    stand.add(me.roundaround.roundalib.client.gui.widget.LabelWidget.builder(this.textRenderer,
        Text.translatable("armorstands.current.stand")
    ).build());
    this.standFacingLabel = stand.add(me.roundaround.roundalib.client.gui.widget.LabelWidget.builder(this.textRenderer,
        this.getCurrentFacingText(this.armorStand)
    ).build());
    this.standRotationLabel = stand.add(
        me.roundaround.roundalib.client.gui.widget.LabelWidget.builder(this.textRenderer,
            this.getCurrentRotationText(this.armorStand)
        ).build());
    labels.add(stand);

    this.layout.topLeft.add(labels, (configurator) -> configurator.margin(Spacing.of(4 * GuiUtil.PADDING, 0, 0, 0)));
  }

  private void initBottomLeft() {
    LinearLayoutWidget snaps = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignStart();

    snaps.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.rotate.snap")).build());

    LinearLayoutWidget firstRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    firstRow.add(ButtonWidget.builder(Text.translatable("armorstands.rotate.snap." + Direction.SOUTH.getName()),
        (button) -> ClientNetworking.sendSetYawPacket(MathHelper.wrapDegrees(Direction.SOUTH.asRotation()))
    ).size(DIRECTION_BUTTON_WIDTH, BUTTON_HEIGHT).build());
    firstRow.add(ButtonWidget.builder(Text.translatable("armorstands.rotate.snap." + Direction.NORTH.getName()),
        (button) -> ClientNetworking.sendSetYawPacket(MathHelper.wrapDegrees(Direction.NORTH.asRotation()))
    ).size(DIRECTION_BUTTON_WIDTH, BUTTON_HEIGHT).build());
    snaps.add(firstRow);

    LinearLayoutWidget secondRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    secondRow.add(ButtonWidget.builder(Text.translatable("armorstands.rotate.snap." + Direction.EAST.getName()),
        (button) -> ClientNetworking.sendSetYawPacket(MathHelper.wrapDegrees(Direction.EAST.asRotation()))
    ).size(DIRECTION_BUTTON_WIDTH, BUTTON_HEIGHT).build());
    secondRow.add(ButtonWidget.builder(Text.translatable("armorstands.rotate.snap." + Direction.WEST.getName()),
        (button) -> ClientNetworking.sendSetYawPacket(MathHelper.wrapDegrees(Direction.WEST.asRotation()))
    ).size(DIRECTION_BUTTON_WIDTH, BUTTON_HEIGHT).build());
    snaps.add(secondRow);

    this.layout.bottomLeft.add(snaps);

    LinearLayoutWidget faces = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignStart();

    faces.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.rotate.face")).build());

    LinearLayoutWidget buttonRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    buttonRow.add(ButtonWidget.builder(Text.translatable("armorstands.rotate.face.toward"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.FACE_TOWARD)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    buttonRow.add(ButtonWidget.builder(Text.translatable("armorstands.rotate.face.away"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.FACE_AWAY)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    buttonRow.add(ButtonWidget.builder(Text.translatable("armorstands.rotate.face.with"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.FACE_WITH)
    ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    faces.add(buttonRow);

    this.layout.bottomLeft.add(faces, (configurator) -> configurator.margin(Spacing.of(GuiUtil.PADDING, 0, 0, 0)));
  }

  private void initBottomRight() {
    this.layout.bottomRight.spacing(2 * GuiUtil.PADDING);

    initRotateRow(RotateDirection.CLOCKWISE);
    initRotateRow(RotateDirection.COUNTERCLOCKWISE);

    this.layout.bottomRight.add(FillerWidget.ofHeight(2 * GuiUtil.PADDING));

    LinearLayoutWidget rotateSection = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignEnd();
    LinearLayoutWidget firstRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);

    firstRow.add(
        LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.rotate")).build(), (parent, self) -> {
          self.setWidth(SLIDER_WIDTH - 3 * (TINY_BUTTON_WIDTH + parent.getSpacing()));
        });
    firstRow.add(ButtonWidget.builder(Text.literal("-"), (button) -> this.rotateSlider.decrement())
        .size(TINY_BUTTON_WIDTH, BUTTON_HEIGHT)
        .tooltip(Tooltip.of(Text.translatable("armorstands.rotate.subtract")))
        .build());
    firstRow.add(ButtonWidget.builder(Text.literal("+"), (button) -> this.rotateSlider.increment())
        .size(TINY_BUTTON_WIDTH, BUTTON_HEIGHT)
        .tooltip(Tooltip.of(Text.translatable("armorstands.rotate.add")))
        .build());
    firstRow.add(ButtonWidget.builder(Text.literal("0"), (button) -> this.rotateSlider.zero())
        .size(TINY_BUTTON_WIDTH, BUTTON_HEIGHT)
        .tooltip(Tooltip.of(Text.translatable("armorstands.rotate.zero")))
        .build());

    rotateSection.add(firstRow);
    this.rotateSlider = rotateSection.add(new RotateSliderWidget(this, SLIDER_WIDTH, BUTTON_HEIGHT, this.armorStand));
    this.layout.bottomRight.add(rotateSection);
  }

  private void initRotateRow(RotateDirection direction) {
    LinearLayoutWidget block = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignEnd();

    block.add(LabelWidget.builder(this.textRenderer, direction.getLabel()).build());

    LinearLayoutWidget row = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignCenter();

    String modifier = direction.getModifier();

    row.add(ButtonWidget.builder(Text.literal(modifier + "1"),
            (button) -> ClientNetworking.sendAdjustYawPacket(direction.offset())
        )
        .size(MINI_BUTTON_WIDTH, BUTTON_HEIGHT)
        .build());
    row.add(ButtonWidget.builder(Text.literal(modifier + "5"),
            (button) -> ClientNetworking.sendAdjustYawPacket(direction.offset() * 5)
        )
        .size(MINI_BUTTON_WIDTH, BUTTON_HEIGHT)
        .build());
    row.add(ButtonWidget.builder(Text.literal(modifier + "15"),
            (button) -> ClientNetworking.sendAdjustYawPacket(direction.offset() * 15)
        )
        .size(MINI_BUTTON_WIDTH, BUTTON_HEIGHT)
        .build());
    row.add(ButtonWidget.builder(Text.literal(modifier + "45"),
            (button) -> ClientNetworking.sendAdjustYawPacket(direction.offset() * 45)
        )
        .size(MINI_BUTTON_WIDTH, BUTTON_HEIGHT)
        .build());

    block.add(row);
    this.layout.bottomRight.add(block);
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    this.playerFacingLabel.setText(getCurrentFacingText(this.getPlayer()));
    this.playerRotationLabel.setText(getCurrentRotationText(this.getPlayer()));
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
      default -> "posX";
    };
    Text towards = Text.translatable("armorstands.current.facing." + towardsI18n);
    return Text.translatable("armorstands.current.facing", currentFacing.toString(), towards.getString());
  }

  private Text getCurrentRotationText(Entity entity) {
    float currentRotation = entity.getYaw();
    return Text.translatable("armorstands.current.rotation",
        String.format(Locale.ROOT, "%.1f", MathHelper.wrapDegrees(currentRotation))
    );
  }

  public enum RotateDirection {
    CLOCKWISE(1, "armorstands.rotate.clockwise"), COUNTERCLOCKWISE(-1, "armorstands.rotate.counter");

    private final int offset;
    private final Text label;

    RotateDirection(int offset, String i18n) {
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

    public String getModifier() {
      return this.offset > 0 ? "+" : "-";
    }
  }
}
