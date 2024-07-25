package me.roundaround.armorstands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.widget.AdjustPoseSliderWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.Pose;
import me.roundaround.roundalib.asset.icon.CustomIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.util.Spacing;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandPoseScreen extends AbstractArmorStandScreen {
  private static final int CONTROL_WIDTH = 100;
  private static final int SLIDER_HEIGHT = 16;
  private static final int BUTTON_HEIGHT = 16;
  private static final int BUTTON_WIDTH = 16;
  protected static final CustomIcon HEAD_ICON = new CustomIcon("head", 20);
  protected static final CustomIcon BODY_ICON = new CustomIcon("body", 20);
  protected static final CustomIcon RIGHT_ARM_ICON = new CustomIcon("rightarm", 20);
  protected static final CustomIcon LEFT_ARM_ICON = new CustomIcon("leftarm", 20);
  protected static final CustomIcon RIGHT_LEG_ICON = new CustomIcon("rightleg", 20);
  protected static final CustomIcon LEFT_LEG_ICON = new CustomIcon("leftleg", 20);

  private PosePart posePart = PosePart.HEAD;
  private ButtonWidget activePosePartButton;
  private LabelWidget posePartLabel;
  private AdjustPoseSliderWidget pitchSlider;
  private AdjustPoseSliderWidget yawSlider;
  private AdjustPoseSliderWidget rollSlider;

  public ArmorStandPoseScreen(ArmorStandScreenHandler handler) {
    super(handler, ScreenType.POSE.getDisplayName());
    this.supportsUndoRedo = true;
  }

  public void onArmorStandPoseChanged(ArmorStandEntity armorStand, PosePart part) {
    if (this.pitchSlider == null || this.yawSlider == null || this.rollSlider == null) {
      return;
    }

    if (armorStand.getId() != this.armorStand.getId() || part != this.posePart) {
      return;
    }

    this.pitchSlider.refresh();
    this.yawSlider.refresh();
    this.rollSlider.refresh();
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.POSE;
  }

  @Override
  protected void collectElements() {
    super.collectElements();
    this.addDrawable((context, mouseX, mouseY, delta) -> {
      if (this.activePosePartButton == null) {
        return;
      }

      RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
      context.drawGuiTexture(
          SELECTION_TEXTURE, this.activePosePartButton.getX() - 2, this.activePosePartButton.getY() - 2, 24, 24);
    });
  }

  @Override
  protected void populateLayout() {
    super.populateLayout();

    this.initPartPicker();
    this.initCore();
  }

  private void initPartPicker() {
    this.layout.bottomLeft.defaultOffAxisContentAlignCenter().spacing(2 * GuiUtil.PADDING);

    IconButtonWidget headButton = this.layout.bottomLeft.add(IconButtonWidget.builder(HEAD_ICON, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .disableIconDim()
        .messageAndTooltip(PosePart.HEAD.getDisplayName())
        .onPress((button) -> setActivePosePart(button, PosePart.HEAD))
        .build());
    headButton.active = false;
    this.activePosePartButton = headButton;

    LinearLayoutWidget torsoRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING);
    torsoRow.add(IconButtonWidget.builder(RIGHT_ARM_ICON, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .disableIconDim()
        .messageAndTooltip(PosePart.RIGHT_ARM.getDisplayName())
        .onPress((button) -> setActivePosePart(button, PosePart.RIGHT_ARM))
        .build());
    torsoRow.add(IconButtonWidget.builder(BODY_ICON, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .disableIconDim()
        .messageAndTooltip(PosePart.BODY.getDisplayName())
        .onPress((button) -> setActivePosePart(button, PosePart.BODY))
        .build());
    torsoRow.add(IconButtonWidget.builder(LEFT_ARM_ICON, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .disableIconDim()
        .messageAndTooltip(PosePart.LEFT_ARM.getDisplayName())
        .onPress((button) -> setActivePosePart(button, PosePart.LEFT_ARM))
        .build());
    this.layout.bottomLeft.add(torsoRow);

    LinearLayoutWidget feetRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING);
    feetRow.add(IconButtonWidget.builder(RIGHT_LEG_ICON, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .disableIconDim()
        .messageAndTooltip(PosePart.RIGHT_LEG.getDisplayName())
        .onPress((button) -> setActivePosePart(button, PosePart.RIGHT_LEG))
        .build());
    feetRow.add(IconButtonWidget.builder(LEFT_LEG_ICON, ArmorStandsMod.MOD_ID)
        .vanillaSize()
        .disableIconDim()
        .messageAndTooltip(PosePart.LEFT_LEG.getDisplayName())
        .onPress((button) -> setActivePosePart(button, PosePart.LEFT_LEG))
        .build());
    this.layout.bottomLeft.add(feetRow);

    this.layout.bottomLeft.add(ButtonWidget.builder(Text.translatable("armorstands.pose.mirror"), (button) -> {
      ClientNetworking.sendSetPosePacket(new Pose(this.armorStand).mirror());

      this.pitchSlider.refresh();
      this.yawSlider.refresh();
      this.rollSlider.refresh();
    }).size(CONTROL_WIDTH, BUTTON_HEIGHT).build());
  }

  private void initCore() {
    this.posePartLabel = this.layout.bottomRight.add(LabelWidget.builder(this.textRenderer,
        Text.translatable("armorstands.pose.editing", this.posePart.getDisplayName())
    ).build());

    this.layout.bottomRight.add(CyclingButtonWidget.builder(SliderRange::getDisplayName)
        .initially(SliderRange.FULL)
        .values(SliderRange.values())
        .omitKeyText()
        .build(Text.translatable("armorstands.pose.range"), (button, value) -> {
          this.pitchSlider.setRange(value.getMin(), value.getMax());
          this.yawSlider.setRange(value.getMin(), value.getMax());
          this.rollSlider.setRange(value.getMin(), value.getMax());
        }), (adder) -> {
      adder.layoutHook((parent, self) -> self.setDimensions(CONTROL_WIDTH, BUTTON_HEIGHT));
      adder.margin(Spacing.of(0, 0, 2 * GuiUtil.PADDING, 0));
    });

    this.pitchSlider = addAdjustSlider(EulerAngleParameter.PITCH);
    this.yawSlider = addAdjustSlider(EulerAngleParameter.YAW);
    this.rollSlider = addAdjustSlider(EulerAngleParameter.ROLL);
  }

  private AdjustPoseSliderWidget addAdjustSlider(EulerAngleParameter parameter) {
    AdjustPoseSliderWidget slider = new AdjustPoseSliderWidget(
        CONTROL_WIDTH, SLIDER_HEIGHT, this.posePart, parameter, this.armorStand);

    LinearLayoutWidget firstRow = LinearLayoutWidget.horizontal()
        .defaultOffAxisContentAlignEnd()
        .spacing(GuiUtil.PADDING);
    firstRow.add(LabelWidget.builder(this.textRenderer, parameter.getDisplayName()).build(), (parent, self) -> {
      self.setWidth(CONTROL_WIDTH - 3 * (BUTTON_WIDTH + parent.getSpacing()));
    });
    firstRow.add(ButtonWidget.builder(Text.literal("-"), (button) -> slider.decrement())
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .tooltip(Tooltip.of(Text.translatable("armorstands.pose.subtract")))
        .build());
    firstRow.add(ButtonWidget.builder(Text.literal("+"), (button) -> slider.increment())
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .tooltip(Tooltip.of(Text.translatable("armorstands.pose.add")))
        .build());
    firstRow.add(ButtonWidget.builder(Text.literal("0"), (button) -> slider.zero())
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .tooltip(Tooltip.of(Text.translatable("armorstands.pose.zero")))
        .build());
    this.layout.bottomRight.add(firstRow, (adder) -> adder.margin(Spacing.of(GuiUtil.PADDING, 0, 0, 0)));

    this.layout.bottomRight.add(slider);

    return slider;
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount
  ) {
    if (this.pitchSlider != null && this.pitchSlider.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
      return true;
    }
    if (this.yawSlider != null && this.yawSlider.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
      return true;
    }
    if (this.rollSlider != null && this.rollSlider.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
  }

  @Override
  protected void handledScreenTick() {
    super.handledScreenTick();

    this.pitchSlider.tick();
    this.yawSlider.tick();
    this.rollSlider.tick();
  }

  private void setActivePosePart(ButtonWidget button, PosePart part) {
    this.posePart = part;
    this.pitchSlider.setPart(part);
    this.yawSlider.setPart(part);
    this.rollSlider.setPart(part);
    this.posePartLabel.setText(Text.translatable("armorstands.pose.editing", this.posePart.getDisplayName()));

    if (this.activePosePartButton != null) {
      this.activePosePartButton.active = true;
    }
    this.activePosePartButton = button;
    this.activePosePartButton.active = false;

    this.layout.refreshPositions();
  }

  private enum SliderRange {
    FULL(-180, 180), HALF(-90, 90), TIGHT(-35, 35);

    private final int min;
    private final int max;

    SliderRange(int min, int max) {
      this.min = min;
      this.max = max;
    }

    public int getMin() {
      return this.min;
    }

    public int getMax() {
      return this.max;
    }

    public Text getDisplayName() {
      return Text.translatable("armorstands.pose.range." + this.name().toLowerCase());
    }
  }
}
