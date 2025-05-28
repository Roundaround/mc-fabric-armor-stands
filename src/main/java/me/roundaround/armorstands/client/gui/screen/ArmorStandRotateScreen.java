package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.widget.RotateSliderWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.roundalib.client.gui.icon.BuiltinIcon;
import me.roundaround.armorstands.roundalib.client.gui.layout.FillerWidget;
import me.roundaround.armorstands.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.armorstands.roundalib.client.gui.util.GuiUtil;
import me.roundaround.armorstands.roundalib.client.gui.util.Spacing;
import me.roundaround.armorstands.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.roundalib.client.gui.widget.drawable.HorizontalLineWidget;
import me.roundaround.armorstands.roundalib.client.gui.widget.drawable.LabelWidget;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class ArmorStandRotateScreen extends AbstractArmorStandScreen {
  private static final int BUTTON_WIDTH = 46;
  private static final int DIRECTION_BUTTON_WIDTH = 70;
  private static final int MINI_BUTTON_WIDTH = 24;
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
    player.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.current.player"))
        .bgColor(BACKGROUND_COLOR)
        .build());
    this.playerFacingLabel = player.add(LabelWidget.builder(this.textRenderer, getCurrentFacingText(this.getPlayer()))
        .bgColor(BACKGROUND_COLOR)
        .build());
    this.playerRotationLabel = player.add(LabelWidget.builder(
        this.textRenderer,
        getCurrentRotationText(this.getPlayer())
    ).bgColor(BACKGROUND_COLOR).build());
    labels.add(player);

    LinearLayoutWidget stand = LinearLayoutWidget.vertical().spacing(1).defaultOffAxisContentAlignStart();
    stand.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.current.stand"))
        .bgColor(BACKGROUND_COLOR)
        .build());
    this.standFacingLabel = stand.add(LabelWidget.builder(this.textRenderer, getCurrentFacingText(this.getArmorStand()))
        .bgColor(BACKGROUND_COLOR)
        .build());
    this.standRotationLabel = stand.add(LabelWidget.builder(
        this.textRenderer,
        getCurrentRotationText(this.getArmorStand())
    ).bgColor(BACKGROUND_COLOR).build());
    labels.add(stand);

    this.layout.topLeft.add(
        new HorizontalLineWidget(this.utilRow.getWidth() - 2 * GuiUtil.PADDING).margin(
            3 * GuiUtil.PADDING),
        (configurator) -> configurator.margin(Spacing.of(0, 0, 0, GuiUtil.PADDING))
    );
    this.layout.topLeft.add(labels);
  }

  private void initBottomLeft() {
    LinearLayoutWidget snaps = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignStart();

    snaps.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.rotate.snap"))
        .bgColor(BACKGROUND_COLOR)
        .build());

    LinearLayoutWidget firstRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    firstRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.rotate.snap." + Direction.SOUTH.getId()),
        (button) -> ClientNetworking.sendSetYawPacket(MathHelper.wrapDegrees(Direction.SOUTH.asRotation()))
    ).size(DIRECTION_BUTTON_WIDTH, ELEMENT_HEIGHT).build());
    firstRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.rotate.snap." + Direction.NORTH.getId()),
        (button) -> ClientNetworking.sendSetYawPacket(MathHelper.wrapDegrees(Direction.NORTH.asRotation()))
    ).size(DIRECTION_BUTTON_WIDTH, ELEMENT_HEIGHT).build());
    snaps.add(firstRow);

    LinearLayoutWidget secondRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    secondRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.rotate.snap." + Direction.EAST.getId()),
        (button) -> ClientNetworking.sendSetYawPacket(MathHelper.wrapDegrees(Direction.EAST.asRotation()))
    ).size(DIRECTION_BUTTON_WIDTH, ELEMENT_HEIGHT).build());
    secondRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.rotate.snap." + Direction.WEST.getId()),
        (button) -> ClientNetworking.sendSetYawPacket(MathHelper.wrapDegrees(Direction.WEST.asRotation()))
    ).size(DIRECTION_BUTTON_WIDTH, ELEMENT_HEIGHT).build());
    snaps.add(secondRow);

    this.layout.bottomLeft.add(snaps);

    LinearLayoutWidget faces = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignStart();

    faces.add(LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.rotate.face"))
        .bgColor(BACKGROUND_COLOR)
        .build());

    LinearLayoutWidget buttonRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2);
    buttonRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.rotate.face.toward"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.FACE_TOWARD)
    ).size(BUTTON_WIDTH, ELEMENT_HEIGHT).build());
    buttonRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.rotate.face.away"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.FACE_AWAY)
    ).size(BUTTON_WIDTH, ELEMENT_HEIGHT).build());
    buttonRow.add(ButtonWidget.builder(
        Text.translatable("armorstands.rotate.face.with"),
        (button) -> ClientNetworking.sendUtilityActionPacket(UtilityAction.FACE_WITH)
    ).size(BUTTON_WIDTH, ELEMENT_HEIGHT).build());
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
    LinearLayoutWidget firstRow = LinearLayoutWidget.horizontal()
        .defaultOffAxisContentAlignEnd()
        .spacing(GuiUtil.PADDING / 2);

    firstRow.add(
        LabelWidget.builder(this.textRenderer, Text.translatable("armorstands.rotate"))
            .bgColor(BACKGROUND_COLOR)
            .build(), (parent, self) -> self.setWidth(SLIDER_WIDTH - 3 * (ELEMENT_HEIGHT + parent.getSpacing()))
    );
    firstRow.add(IconButtonWidget.builder(BuiltinIcon.MINUS_13, ArmorStandsMod.MOD_ID)
        .dimensions(ELEMENT_HEIGHT)
        .onPress((button) -> this.rotateSlider.decrement())
        .tooltip(Tooltip.of(Text.translatable("armorstands.rotate.subtract")))
        .build());
    firstRow.add(IconButtonWidget.builder(BuiltinIcon.PLUS_13, ArmorStandsMod.MOD_ID)
        .dimensions(ELEMENT_HEIGHT)
        .onPress((button) -> this.rotateSlider.increment())
        .tooltip(Tooltip.of(Text.translatable("armorstands.rotate.add")))
        .build());
    firstRow.add(IconButtonWidget.builder(BuiltinIcon.ROTATE_13, ArmorStandsMod.MOD_ID)
        .dimensions(ELEMENT_HEIGHT)
        .onPress((button) -> this.rotateSlider.zero())
        .tooltip(Tooltip.of(Text.translatable("armorstands.rotate.zero")))
        .build());

    rotateSection.add(firstRow);
    this.rotateSlider = rotateSection.add(new RotateSliderWidget(
        this,
        SLIDER_WIDTH,
        ELEMENT_HEIGHT,
        this.getArmorStand()
    ));
    this.layout.bottomRight.add(rotateSection);
  }

  private void initRotateRow(RotateDirection direction) {
    LinearLayoutWidget block = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignEnd();

    block.add(LabelWidget.builder(this.textRenderer, direction.getLabel()).bgColor(BACKGROUND_COLOR).build());

    LinearLayoutWidget row = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING / 2)
        .defaultOffAxisContentAlignCenter();

    String modifier = direction.getModifier();

    row.add(ButtonWidget.builder(
            Text.literal(modifier + "1"),
            (button) -> ClientNetworking.sendAdjustYawPacket(direction.offset())
        )
        .size(MINI_BUTTON_WIDTH, ELEMENT_HEIGHT)
        .build());
    row.add(ButtonWidget.builder(
            Text.literal(modifier + "5"),
            (button) -> ClientNetworking.sendAdjustYawPacket(direction.offset() * 5)
        )
        .size(MINI_BUTTON_WIDTH, ELEMENT_HEIGHT)
        .build());
    row.add(ButtonWidget.builder(
            Text.literal(modifier + "15"),
            (button) -> ClientNetworking.sendAdjustYawPacket(direction.offset() * 15)
        )
        .size(MINI_BUTTON_WIDTH, ELEMENT_HEIGHT)
        .build());
    row.add(ButtonWidget.builder(
            Text.literal(modifier + "45"),
            (button) -> ClientNetworking.sendAdjustYawPacket(direction.offset() * 45)
        )
        .size(MINI_BUTTON_WIDTH, ELEMENT_HEIGHT)
        .build());

    block.add(row);
    this.layout.bottomRight.add(block);
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    this.playerFacingLabel.setText(getCurrentFacingText(this.getPlayer()));
    this.playerRotationLabel.setText(getCurrentRotationText(this.getPlayer()));
    this.standFacingLabel.setText(getCurrentFacingText(this.getArmorStand()));
    this.standRotationLabel.setText(getCurrentRotationText(this.getArmorStand()));
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
