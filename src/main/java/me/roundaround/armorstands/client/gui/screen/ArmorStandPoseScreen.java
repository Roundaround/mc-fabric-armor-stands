package me.roundaround.armorstands.client.gui.screen;

import java.util.List;

import me.roundaround.armorstands.client.gui.widget.AdjustPoseSliderWidget;
import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.client.gui.widget.SimpleTooltipButtonWidget;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.Pose;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandPoseScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.pose");
  public static final int U_INDEX = 3;

  private static final int CONTROL_WIDTH = 100;
  private static final int SLIDER_HEIGHT = 20;
  private static final int BUTTON_HEIGHT = 16;
  private static final int BUTTON_WIDTH = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;
  private static final int ROW_PAD = 6;
  private static final int PART_PAD_VERTICAL = 10;
  private static final int PART_PAD_HORIZONTAL = 4;

  private PosePart posePart = PosePart.HEAD;
  private IconButtonWidget<ArmorStandPoseScreen> activePosePartButton;

  private IconButtonWidget<ArmorStandPoseScreen> headButton;
  private IconButtonWidget<ArmorStandPoseScreen> bodyButton;
  private IconButtonWidget<ArmorStandPoseScreen> leftArmButton;
  private IconButtonWidget<ArmorStandPoseScreen> rightArmButton;
  private IconButtonWidget<ArmorStandPoseScreen> leftLegButton;
  private IconButtonWidget<ArmorStandPoseScreen> rightLegButton;
  private AdjustPoseSliderWidget pitchSlider;
  private AdjustPoseSliderWidget yawSlider;
  private AdjustPoseSliderWidget rollSlider;

  public ArmorStandPoseScreen(
      ArmorStandScreenHandler handler,
      ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);

    this.supportsUndoRedo = true;
    this.passEvents = true;
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
  public ScreenConstructor<?> getNextScreen() {
    return ArmorStandInventoryScreen::new;
  }

  @Override
  public ScreenConstructor<?> getPreviousScreen() {
    return ArmorStandPresetsScreen::new;
  }

  @Override
  public void init() {
    super.init();

    int offset = (CONTROL_WIDTH - 3 * IconButtonWidget.WIDTH - 2 * PART_PAD_HORIZONTAL) / 2;

    this.headButton = new IconButtonWidget<>(
        client,
        this,
        offset + SCREEN_EDGE_PAD
            + IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD
            - 3 * IconButtonWidget.HEIGHT - 2 * PART_PAD_VERTICAL
            - PART_PAD_VERTICAL - BUTTON_HEIGHT,
        6,
        PosePart.HEAD.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.HEAD);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    this.headButton.active = false;
    this.activePosePartButton = this.headButton;
    addDrawableChild(this.headButton);

    this.rightArmButton = new IconButtonWidget<>(
        client,
        this,
        offset + SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD
            - 2 * IconButtonWidget.HEIGHT - PART_PAD_VERTICAL
            - PART_PAD_VERTICAL - BUTTON_HEIGHT,
        8,
        PosePart.RIGHT_ARM.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.RIGHT_ARM);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    addDrawableChild(this.rightArmButton);

    this.bodyButton = new IconButtonWidget<>(
        client,
        this,
        offset + SCREEN_EDGE_PAD
            + IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD
            - 2 * IconButtonWidget.HEIGHT - PART_PAD_VERTICAL
            - PART_PAD_VERTICAL - BUTTON_HEIGHT,
        7,
        PosePart.BODY.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.BODY);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    addDrawableChild(this.bodyButton);

    this.leftArmButton = new IconButtonWidget<>(
        client,
        this,
        offset + SCREEN_EDGE_PAD
            + 2 * IconButtonWidget.WIDTH + 2 * PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD
            - 2 * IconButtonWidget.HEIGHT - PART_PAD_VERTICAL
            - PART_PAD_VERTICAL - BUTTON_HEIGHT,
        9,
        PosePart.LEFT_ARM.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.LEFT_ARM);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    addDrawableChild(this.leftArmButton);

    this.rightLegButton = new IconButtonWidget<>(
        client,
        this,
        offset + SCREEN_EDGE_PAD
            + (IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL) / 2,
        this.height - SCREEN_EDGE_PAD
            - IconButtonWidget.HEIGHT
            - PART_PAD_VERTICAL - BUTTON_HEIGHT,
        11,
        PosePart.RIGHT_LEG.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.RIGHT_LEG);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    addDrawableChild(this.rightLegButton);

    this.leftLegButton = new IconButtonWidget<>(
        client,
        this,
        offset + SCREEN_EDGE_PAD
            + (IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL) / 2
            + IconButtonWidget.WIDTH
            + PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD
            - IconButtonWidget.HEIGHT
            - PART_PAD_VERTICAL - BUTTON_HEIGHT,
        10,
        PosePart.LEFT_LEG.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.LEFT_LEG);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    addDrawableChild(this.leftLegButton);

    addDrawableChild(new ButtonWidget(
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - BUTTON_HEIGHT,
        CONTROL_WIDTH,
        BUTTON_HEIGHT,
        Text.translatable("armorstands.pose.mirror"),
        (button) -> {
          ClientNetworking.sendSetPosePacket(new Pose(this.armorStand).mirror());

          this.pitchSlider.refresh();
          this.yawSlider.refresh();
          this.rollSlider.refresh();
        }));

    initNavigationButtons(List.of(
        ScreenFactory.create(
            ArmorStandUtilitiesScreen.TITLE,
            ArmorStandUtilitiesScreen.U_INDEX,
            ArmorStandUtilitiesScreen::new),
        ScreenFactory.create(
            ArmorStandMoveScreen.TITLE,
            ArmorStandMoveScreen.U_INDEX,
            ArmorStandMoveScreen::new),
        ScreenFactory.create(
            ArmorStandRotateScreen.TITLE,
            ArmorStandRotateScreen.U_INDEX,
            ArmorStandRotateScreen::new),
        ScreenFactory.create(
            ArmorStandPresetsScreen.TITLE,
            ArmorStandPresetsScreen.U_INDEX,
            ArmorStandPresetsScreen::new),
        ScreenFactory.create(
            ArmorStandPoseScreen.TITLE,
            ArmorStandPoseScreen.U_INDEX),
        ScreenFactory.create(
            ArmorStandInventoryScreen.TITLE,
            ArmorStandInventoryScreen.U_INDEX,
            ArmorStandInventoryScreen::new)));

    addDrawable(LabelWidget.builder(
        EulerAngleParameter.PITCH.getDisplayName(),
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD
            - 3 * SLIDER_HEIGHT
            - 2 * BUTTON_HEIGHT
            - 3 * BETWEEN_PAD
            - 2 * ROW_PAD)
        .alignedBottom()
        .justifiedLeft()
        .shiftForPadding()
        .build());
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        this.width - SCREEN_EDGE_PAD
            - 3 * BUTTON_WIDTH
            - 2 * BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - 3 * SLIDER_HEIGHT
            - 3 * BUTTON_HEIGHT
            - 3 * BETWEEN_PAD
            - 2 * ROW_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("-"),
        Text.translatable("armorstands.pose.subtract"),
        (button) -> {
          if (this.pitchSlider == null) {
            return;
          }
          this.pitchSlider.decrement();
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        this.width - SCREEN_EDGE_PAD
            - 2 * BUTTON_WIDTH
            - BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - 3 * SLIDER_HEIGHT
            - 3 * BUTTON_HEIGHT
            - 3 * BETWEEN_PAD
            - 2 * ROW_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+"),
        Text.translatable("armorstands.pose.add"),
        (button) -> {
          if (this.pitchSlider == null) {
            return;
          }
          this.pitchSlider.increment();
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        this.width - SCREEN_EDGE_PAD - BUTTON_WIDTH,
        this.height - SCREEN_EDGE_PAD
            - 3 * SLIDER_HEIGHT
            - 3 * BUTTON_HEIGHT
            - 3 * BETWEEN_PAD
            - 2 * ROW_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("0"),
        Text.translatable("armorstands.pose.zero"),
        (button) -> {
          if (this.pitchSlider == null) {
            return;
          }
          this.pitchSlider.zero();
        }));
    this.pitchSlider = new AdjustPoseSliderWidget(
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD
            - 3 * SLIDER_HEIGHT
            - 2 * BUTTON_HEIGHT
            - 2 * BETWEEN_PAD
            - 2 * ROW_PAD,
        CONTROL_WIDTH,
        SLIDER_HEIGHT,
        this.posePart,
        EulerAngleParameter.PITCH,
        this.armorStand);
    addDrawableChild(this.pitchSlider);

    addDrawable(LabelWidget.builder(
        EulerAngleParameter.YAW.getDisplayName(),
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD
            - 2 * SLIDER_HEIGHT
            - 1 * BUTTON_HEIGHT
            - 2 * BETWEEN_PAD
            - 1 * ROW_PAD)
        .alignedBottom()
        .justifiedLeft()
        .shiftForPadding()
        .build());
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        this.width - SCREEN_EDGE_PAD
            - 3 * BUTTON_WIDTH
            - 2 * BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - 2 * SLIDER_HEIGHT
            - 2 * BUTTON_HEIGHT
            - 2 * BETWEEN_PAD
            - 1 * ROW_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("-"),
        Text.translatable("armorstands.pose.subtract"),
        (button) -> {
          if (this.yawSlider == null) {
            return;
          }
          this.yawSlider.decrement();
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        this.width - SCREEN_EDGE_PAD
            - 2 * BUTTON_WIDTH
            - BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - 2 * SLIDER_HEIGHT
            - 2 * BUTTON_HEIGHT
            - 2 * BETWEEN_PAD
            - 1 * ROW_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+"),
        Text.translatable("armorstands.pose.add"),
        (button) -> {
          if (this.yawSlider == null) {
            return;
          }
          this.yawSlider.increment();
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        this.width - SCREEN_EDGE_PAD - BUTTON_WIDTH,
        this.height - SCREEN_EDGE_PAD
            - 2 * SLIDER_HEIGHT
            - 2 * BUTTON_HEIGHT
            - 2 * BETWEEN_PAD
            - 1 * ROW_PAD,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("0"),
        Text.translatable("armorstands.pose.zero"),
        (button) -> {
          if (this.yawSlider == null) {
            return;
          }
          this.yawSlider.zero();
        }));
    this.yawSlider = new AdjustPoseSliderWidget(
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD
            - 2 * SLIDER_HEIGHT
            - 1 * BUTTON_HEIGHT
            - 1 * BETWEEN_PAD
            - 1 * ROW_PAD,
        CONTROL_WIDTH,
        SLIDER_HEIGHT,
        this.posePart,
        EulerAngleParameter.YAW,
        this.armorStand);
    addDrawableChild(this.yawSlider);

    addDrawable(LabelWidget.builder(
        EulerAngleParameter.ROLL.getDisplayName(),
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD
            - SLIDER_HEIGHT
            - BETWEEN_PAD)
        .alignedBottom()
        .justifiedLeft()
        .shiftForPadding()
        .build());
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        this.width - SCREEN_EDGE_PAD
            - 3 * BUTTON_WIDTH
            - 2 * BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - SLIDER_HEIGHT
            - BETWEEN_PAD
            - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("-"),
        Text.translatable("armorstands.pose.subtract"),
        (button) -> {
          if (this.rollSlider == null) {
            return;
          }
          this.rollSlider.decrement();
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        this.width - SCREEN_EDGE_PAD
            - 2 * BUTTON_WIDTH
            - BETWEEN_PAD,
        this.height - SCREEN_EDGE_PAD
            - SLIDER_HEIGHT
            - BETWEEN_PAD
            - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("+"),
        Text.translatable("armorstands.pose.add"),
        (button) -> {
          if (this.rollSlider == null) {
            return;
          }
          this.rollSlider.increment();
        }));
    addDrawableChild(new SimpleTooltipButtonWidget(
        this,
        this.width - SCREEN_EDGE_PAD - BUTTON_WIDTH,
        this.height - SCREEN_EDGE_PAD
            - SLIDER_HEIGHT
            - BETWEEN_PAD
            - BUTTON_HEIGHT,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Text.literal("0"),
        Text.translatable("armorstands.pose.zero"),
        (button) -> {
          if (this.rollSlider == null) {
            return;
          }
          this.rollSlider.zero();
        }));
    this.rollSlider = new AdjustPoseSliderWidget(
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD - SLIDER_HEIGHT,
        CONTROL_WIDTH,
        SLIDER_HEIGHT,
        this.posePart,
        EulerAngleParameter.ROLL,
        this.armorStand);
    addDrawableChild(this.rollSlider);
  }

  private void setActivePosePart(PosePart part) {
    this.posePart = part;
    this.pitchSlider.setPart(part);
    this.yawSlider.setPart(part);
    this.rollSlider.setPart(part);
  }
}
