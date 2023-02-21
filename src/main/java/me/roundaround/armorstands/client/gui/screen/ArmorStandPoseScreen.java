package me.roundaround.armorstands.client.gui.screen;

import java.util.List;

import me.roundaround.armorstands.client.gui.widget.AdjustPoseSliderWidget;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandPoseScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.pose");
  public static final int U_INDEX = 3;

  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  private AdjustPoseSliderWidget headYawSlider;

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

    this.headYawSlider = new AdjustPoseSliderWidget(
        this.width - SCREEN_EDGE_PAD - 100,
        this.height - SCREEN_EDGE_PAD - 20,
        100,
        20,
        PosePart.HEAD,
        EulerAngleParameter.YAW,
        this.armorStand);
    addDrawableChild(this.headYawSlider);
  }
}
