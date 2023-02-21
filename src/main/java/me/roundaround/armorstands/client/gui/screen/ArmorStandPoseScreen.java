package me.roundaround.armorstands.client.gui.screen;

import java.util.List;

import me.roundaround.armorstands.client.gui.widget.AdjustPoseSliderWidget;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandPoseScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.pose");
  public static final int U_INDEX = 3;

  private static final int CONTROL_WIDTH = 100;
  private static final int CONTROL_HEIGHT = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  private CyclingButtonWidget<PosePart> posePartButton;
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

  @Override
  public ScreenType getScreenType() {
    return ScreenType.POSE;
  }

  @Override
  public void init() {
    super.init();

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

    this.posePartButton = CyclingButtonWidget.builder(PosePart::getDisplayName)
        .values(PosePart.values())
        .initially(PosePart.HEAD)
        .build(
            this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
            this.height - SCREEN_EDGE_PAD - 4 * CONTROL_HEIGHT - 3 * BETWEEN_PAD,
            CONTROL_WIDTH,
            CONTROL_HEIGHT,
            Text.empty(),
            (button, posePart) -> {
              this.yawSlider.setPart(posePart);
              this.pitchSlider.setPart(posePart);
              this.rollSlider.setPart(posePart);
            });
    addSelectableChild(this.posePartButton);

    this.pitchSlider = new AdjustPoseSliderWidget(
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD - 3 * CONTROL_HEIGHT - 2 * BETWEEN_PAD,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        this.posePartButton.getValue(),
        EulerAngleParameter.PITCH,
        this.armorStand);
    addDrawableChild(this.pitchSlider);

    this.yawSlider = new AdjustPoseSliderWidget(
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD - 2 * CONTROL_HEIGHT - BETWEEN_PAD,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        this.posePartButton.getValue(),
        EulerAngleParameter.YAW,
        this.armorStand);
    addDrawableChild(this.yawSlider);

    this.rollSlider = new AdjustPoseSliderWidget(
        this.width - SCREEN_EDGE_PAD - CONTROL_WIDTH,
        this.height - SCREEN_EDGE_PAD - CONTROL_HEIGHT,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        this.posePartButton.getValue(),
        EulerAngleParameter.ROLL,
        this.armorStand);
    addDrawableChild(this.rollSlider);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.render(matrixStack, mouseX, mouseY, partialTicks);

    this.posePartButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }
}
