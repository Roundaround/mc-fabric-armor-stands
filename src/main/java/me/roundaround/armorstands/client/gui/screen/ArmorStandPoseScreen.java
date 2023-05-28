package me.roundaround.armorstands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.armorstands.client.gui.widget.AdjustPoseSliderWidget;
import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.packet.c2s.SetPosePacket;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.Pose;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandPoseScreen extends AbstractArmorStandScreen {
  private static final int CONTROL_WIDTH = 100;
  private static final int SLIDER_HEIGHT = 16;
  private static final int BUTTON_HEIGHT = 16;
  private static final int BUTTON_WIDTH = 16;
  private static final int ROW_PAD = 6;
  private static final int PART_PAD_VERTICAL = 10;
  private static final int PART_PAD_HORIZONTAL = 4;

  private PosePart posePart = PosePart.HEAD;
  private IconButtonWidget activePosePartButton;

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
  protected void initLeft() {
    super.initLeft();

    int offset = (CONTROL_WIDTH - 3 * IconButtonWidget.WIDTH - 2 * PART_PAD_HORIZONTAL) / 2;

    IconButtonWidget headButton = new IconButtonWidget(
        offset + SCREEN_EDGE_PAD + IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD - 3 * IconButtonWidget.HEIGHT - 2 * PART_PAD_VERTICAL -
            PART_PAD_VERTICAL - BUTTON_HEIGHT,
        6,
        PosePart.HEAD.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.HEAD);
          this.activePosePartButton.active = true;
          this.activePosePartButton = (IconButtonWidget) button;
          this.activePosePartButton.active = false;
        });
    headButton.active = false;
    this.activePosePartButton = headButton;
    addDrawableChild(headButton);

    IconButtonWidget rightArmButton = new IconButtonWidget(offset + SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * IconButtonWidget.HEIGHT - PART_PAD_VERTICAL -
            PART_PAD_VERTICAL - BUTTON_HEIGHT,
        8,
        PosePart.RIGHT_ARM.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.RIGHT_ARM);
          this.activePosePartButton.active = true;
          this.activePosePartButton = (IconButtonWidget) button;
          this.activePosePartButton.active = false;
        });
    addDrawableChild(rightArmButton);

    IconButtonWidget bodyButton = new IconButtonWidget(
        offset + SCREEN_EDGE_PAD + IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD - 2 * IconButtonWidget.HEIGHT - PART_PAD_VERTICAL -
            PART_PAD_VERTICAL - BUTTON_HEIGHT,
        7,
        PosePart.BODY.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.BODY);
          this.activePosePartButton.active = true;
          this.activePosePartButton = (IconButtonWidget) button;
          this.activePosePartButton.active = false;
        });
    addDrawableChild(bodyButton);

    IconButtonWidget leftArmButton = new IconButtonWidget(
        offset + SCREEN_EDGE_PAD + 2 * IconButtonWidget.WIDTH + 2 * PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD - 2 * IconButtonWidget.HEIGHT - PART_PAD_VERTICAL -
            PART_PAD_VERTICAL - BUTTON_HEIGHT,
        9,
        PosePart.LEFT_ARM.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.LEFT_ARM);
          this.activePosePartButton.active = true;
          this.activePosePartButton = (IconButtonWidget) button;
          this.activePosePartButton.active = false;
        });
    addDrawableChild(leftArmButton);

    IconButtonWidget rightLegButton = new IconButtonWidget(
        offset + SCREEN_EDGE_PAD + (IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL) / 2,
        this.height - SCREEN_EDGE_PAD - IconButtonWidget.HEIGHT - PART_PAD_VERTICAL - BUTTON_HEIGHT,
        11,
        PosePart.RIGHT_LEG.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.RIGHT_LEG);
          this.activePosePartButton.active = true;
          this.activePosePartButton = (IconButtonWidget) button;
          this.activePosePartButton.active = false;
        });
    addDrawableChild(rightLegButton);

    IconButtonWidget leftLegButton = new IconButtonWidget(
        offset + SCREEN_EDGE_PAD + (IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL) / 2 +
            IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD - IconButtonWidget.HEIGHT - PART_PAD_VERTICAL - BUTTON_HEIGHT,
        10,
        PosePart.LEFT_LEG.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.LEFT_LEG);
          this.activePosePartButton.active = true;
          this.activePosePartButton = (IconButtonWidget) button;
          this.activePosePartButton.active = false;
        });
    addDrawableChild(leftLegButton);

    addDrawableChild(ButtonWidget.builder(Text.translatable("armorstands.pose.mirror"),
            (button) -> {
              SetPosePacket.sendToServer(new Pose(this.armorStand).mirror());

              this.pitchSlider.refresh();
              this.yawSlider.refresh();
              this.rollSlider.refresh();
            })
        .size(CONTROL_WIDTH, BUTTON_HEIGHT)
        .position(SCREEN_EDGE_PAD, this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT)
        .build());
  }

  @Override
  protected void initRight() {
    super.initRight();

    addDrawableChild(CyclingButtonWidget.builder(SliderRange::getDisplayName)
        .initially(SliderRange.FULL)
        .values(SliderRange.values())
        .omitKeyText()
        .build(this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
            this.height - SCREEN_EDGE_PAD - 3 * SLIDER_HEIGHT - 3 * BUTTON_HEIGHT -
                3 * BETWEEN_PAD - 3 * ROW_PAD - 2 * LabelWidget.HEIGHT_WITH_PADDING,
            CONTROL_WIDTH,
            BUTTON_HEIGHT,
            Text.translatable("armorstands.pose.range"),
            (button, value) -> {
              this.pitchSlider.setRange(value.getMin(), value.getMax());
              this.yawSlider.setRange(value.getMin(), value.getMax());
              this.rollSlider.setRange(value.getMin(), value.getMax());
            }));

    this.posePartLabel = addLabel(LabelWidget.builder(Text.translatable("armorstands.pose.editing",
                this.posePart.getDisplayName().getString()),
            this.width - SCREEN_EDGE_PAD,
            this.height - SCREEN_EDGE_PAD - 3 * SLIDER_HEIGHT - 3 * BUTTON_HEIGHT - 4 * BETWEEN_PAD -
                3 * ROW_PAD - 2 * LabelWidget.HEIGHT_WITH_PADDING)
        .shiftForPadding()
        .alignedBottom()
        .justifiedRight()
        .build());

    this.pitchSlider = addAdjustSlider(EulerAngleParameter.PITCH, 2);
    this.yawSlider = addAdjustSlider(EulerAngleParameter.YAW, 1);
    this.rollSlider = addAdjustSlider(EulerAngleParameter.ROLL, 0);
  }

  private AdjustPoseSliderWidget addAdjustSlider(EulerAngleParameter parameter, int index) {
    int refRight = this.width - SCREEN_EDGE_PAD;
    int refLeft = refRight - CONTROL_WIDTH;
    int refY = this.height - SCREEN_EDGE_PAD - SLIDER_HEIGHT -
        index * (SLIDER_HEIGHT + BUTTON_HEIGHT + BETWEEN_PAD + ROW_PAD);

    AdjustPoseSliderWidget slider = new AdjustPoseSliderWidget(refLeft,
        refY,
        CONTROL_WIDTH,
        SLIDER_HEIGHT,
        this.posePart,
        parameter,
        this.armorStand);

    addLabel(LabelWidget.builder(parameter.getDisplayName(), refLeft, refY - BETWEEN_PAD)
        .alignedBottom()
        .justifiedLeft()
        .shiftForPadding()
        .build());
    addDrawableChild(ButtonWidget.builder(Text.literal("-"), (button) -> slider.decrement())
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(refRight - 3 * BUTTON_WIDTH - 2 * BETWEEN_PAD, refY - BETWEEN_PAD - BUTTON_HEIGHT)
        .tooltip(Tooltip.of(Text.translatable("armorstands.pose.subtract")))
        .build());
    addDrawableChild(ButtonWidget.builder(Text.literal("+"), (button) -> slider.increment())
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(refRight - 2 * BUTTON_WIDTH - BETWEEN_PAD, refY - BETWEEN_PAD - BUTTON_HEIGHT)
        .tooltip(Tooltip.of(Text.translatable("armorstands.pose.add")))
        .build());
    addDrawableChild(ButtonWidget.builder(Text.literal("0"), (button) -> slider.zero())
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .position(refRight - BUTTON_WIDTH, refY - BETWEEN_PAD - BUTTON_HEIGHT)
        .tooltip(Tooltip.of(Text.translatable("armorstands.pose.zero")))
        .build());
    addDrawableChild(slider);

    return slider;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    if (this.pitchSlider != null && this.pitchSlider.mouseScrolled(mouseX, mouseY, amount)) {
      return true;
    }
    if (this.yawSlider != null && this.yawSlider.mouseScrolled(mouseX, mouseY, amount)) {
      return true;
    }
    if (this.rollSlider != null && this.rollSlider.mouseScrolled(mouseX, mouseY, amount)) {
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, amount);
  }

  @Override
  protected void handledScreenTick() {
    super.handledScreenTick();

    this.pitchSlider.tick();
    this.yawSlider.tick();
    this.rollSlider.tick();
  }

  @Override
  protected void renderActivePageButtonHighlight(DrawContext drawContext) {
    super.renderActivePageButtonHighlight(drawContext);

    if (this.activePosePartButton == null) {
      return;
    }

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    MatrixStack matrixStack = drawContext.getMatrices();
    matrixStack.push();
    matrixStack.translate(0, 0, 100);
    drawContext.drawNineSlicedTexture(WIDGETS_TEXTURE,
        this.activePosePartButton.getX() - 2,
        this.activePosePartButton.getY() - 2,
        25,
        25,
        4,
        24,
        24,
        0,
        22);
    matrixStack.pop();
  }

  private void setActivePosePart(PosePart part) {
    this.posePart = part;
    this.pitchSlider.setPart(part);
    this.yawSlider.setPart(part);
    this.rollSlider.setPart(part);
    this.posePartLabel.setText(Text.translatable("armorstands.pose.editing",
        this.posePart.getDisplayName().getString()));
  }

  private static enum SliderRange {
    FULL(-180, 180),
    HALF(-90, 90),
    TIGHT(-35, 35);

    private final int min;
    private final int max;

    private SliderRange(int min, int max) {
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
