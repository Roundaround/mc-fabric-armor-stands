package me.roundaround.armorstands.client.gui.screen;

import java.util.List;

import me.roundaround.armorstands.client.gui.widget.AdjustPoseSliderWidget;
import me.roundaround.armorstands.client.gui.widget.IconButtonWidget;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandPoseScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.pose");
  public static final int U_INDEX = 3;

  private static final int CONTROL_WIDTH = 100;
  private static final int CONTROL_HEIGHT = 20;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;
  private static final int PART_PAD_VERTICAL = 8;
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
  public void init() {
    super.init();

    this.headButton = new IconButtonWidget<>(
        client,
        this,
        SCREEN_EDGE_PAD + IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD - 3 * IconButtonWidget.HEIGHT - 2 * PART_PAD_VERTICAL,
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
    addSelectableChild(this.headButton);

    this.rightArmButton = new IconButtonWidget<>(
        client,
        this,
        SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * IconButtonWidget.HEIGHT - PART_PAD_VERTICAL,
        8,
        PosePart.RIGHT_ARM.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.RIGHT_ARM);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    addSelectableChild(this.rightArmButton);

    this.bodyButton = new IconButtonWidget<>(
        client,
        this,
        SCREEN_EDGE_PAD + IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD - 2 * IconButtonWidget.HEIGHT - PART_PAD_VERTICAL,
        7,
        PosePart.BODY.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.BODY);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    addSelectableChild(this.bodyButton);

    this.leftArmButton = new IconButtonWidget<>(
        client,
        this,
        SCREEN_EDGE_PAD + 2 * IconButtonWidget.WIDTH + 2 * PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD - 2 * IconButtonWidget.HEIGHT - PART_PAD_VERTICAL,
        9,
        PosePart.LEFT_ARM.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.LEFT_ARM);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    addSelectableChild(this.leftArmButton);

    this.rightLegButton = new IconButtonWidget<>(
        client,
        this,
        SCREEN_EDGE_PAD + (IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL) / 2,
        this.height - SCREEN_EDGE_PAD - IconButtonWidget.HEIGHT,
        11,
        PosePart.RIGHT_LEG.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.RIGHT_LEG);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    addSelectableChild(this.rightLegButton);

    this.leftLegButton = new IconButtonWidget<>(
        client,
        this,
        SCREEN_EDGE_PAD + (IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL) / 2 + IconButtonWidget.WIDTH + PART_PAD_HORIZONTAL,
        this.height - SCREEN_EDGE_PAD - IconButtonWidget.HEIGHT,
        10,
        PosePart.LEFT_LEG.getDisplayName(),
        (button) -> {
          setActivePosePart(PosePart.LEFT_LEG);
          this.activePosePartButton.active = true;
          this.activePosePartButton = button;
          this.activePosePartButton.active = false;
        });
    addSelectableChild(this.leftLegButton);

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

    this.pitchSlider = new AdjustPoseSliderWidget(
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD - 3 * CONTROL_HEIGHT - 2 * BETWEEN_PAD,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        this.posePart,
        EulerAngleParameter.PITCH,
        this.armorStand);
    addDrawableChild(this.pitchSlider);

    this.yawSlider = new AdjustPoseSliderWidget(
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD - 2 * CONTROL_HEIGHT - BETWEEN_PAD,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        this.posePart,
        EulerAngleParameter.YAW,
        this.armorStand);
    addDrawableChild(this.yawSlider);

    this.rollSlider = new AdjustPoseSliderWidget(
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD - CONTROL_HEIGHT,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        this.posePart,
        EulerAngleParameter.ROLL,
        this.armorStand);
    addDrawableChild(this.rollSlider);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.render(matrixStack, mouseX, mouseY, partialTicks);

    this.headButton.render(matrixStack, mouseX, mouseY, partialTicks);
    this.rightArmButton.render(matrixStack, mouseX, mouseY, partialTicks);
    this.bodyButton.render(matrixStack, mouseX, mouseY, partialTicks);
    this.leftArmButton.render(matrixStack, mouseX, mouseY, partialTicks);
    this.rightLegButton.render(matrixStack, mouseX, mouseY, partialTicks);
    this.leftLegButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  private void setActivePosePart(PosePart part) {
    this.posePart = part;
    this.pitchSlider.setPart(part);
    this.yawSlider.setPart(part);
    this.rollSlider.setPart(part);
  }
}
