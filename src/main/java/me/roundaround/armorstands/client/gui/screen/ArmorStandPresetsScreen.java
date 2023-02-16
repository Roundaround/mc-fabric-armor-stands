package me.roundaround.armorstands.client.gui.screen;

import java.util.List;

import me.roundaround.armorstands.client.gui.widget.NavigationButton;
import me.roundaround.armorstands.client.gui.widget.PoseListWidget;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ArmorStandPresetsScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.presets");
  public static final int U_INDEX = 4;

  private static final int PADDING = 4;

  private PoseListWidget list;

  public ArmorStandPresetsScreen(
      ArmorStandScreenHandler handler,
      ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);

    this.supportsUndoRedo = true;
    this.passEvents = false;
  }

  @Override
  public boolean shouldPause() {
    return true;
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
            ArmorStandPresetsScreen.U_INDEX),
        ScreenFactory.create(
            ArmorStandPoseScreen.TITLE,
            ArmorStandPoseScreen.U_INDEX,
            ArmorStandPoseScreen::new),
        ScreenFactory.create(
            ArmorStandInventoryScreen.TITLE,
            ArmorStandInventoryScreen.U_INDEX,
            ArmorStandInventoryScreen::new)));

    int listWidth = MathHelper.floor(this.width / 2f
        - 2.5f * NavigationButton.WIDTH
        - 2 * ICON_BUTTON_SPACING
        - 2 * PADDING);
    int listHeight = this.height - 2 * PADDING;

    int refX = this.width - PADDING - listWidth;
    int refY = this.height - PADDING - listHeight;

    this.list = new PoseListWidget(
        client,
        refX,
        refY,
        listWidth,
        listHeight);
    addSelectableChild(this.list);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.list.render(matrixStack, mouseX, mouseY, partialTicks);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
  }
}
