package me.roundaround.armorstands.client.gui.screen;

import java.util.List;

import me.roundaround.armorstands.client.gui.widget.LabelWidget;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.PosePart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandPoseScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.pose");
  public static final int U_INDEX = 3;

  private static final int MINI_BUTTON_WIDTH = 16;
  private static final int MINI_BUTTON_HEIGHT = 16;
  private static final int SCREEN_EDGE_PAD = 4;
  private static final int BETWEEN_PAD = 2;

  public ArmorStandPoseScreen(
      ArmorStandScreenHandler handler,
      ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);
    this.supportsUndoRedo = true;
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
  }
  
  private void addRowOfButtons(Text label, PosePart part, int y) {
    addDrawable(LabelWidget.builder(
        label,
        SCREEN_EDGE_PAD,
        y)
        .alignedTop()
        .justifiedLeft()
        .shiftForPadding()
        .build());
  }
}
