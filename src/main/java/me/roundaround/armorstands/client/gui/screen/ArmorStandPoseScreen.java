package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.client.gui.widget.NavigationButton;
import me.roundaround.armorstands.client.gui.widget.PoseListWidget;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ArmorStandPoseScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.page.pose");

  private static final int PADDING = 4;

  private PoseListWidget list;

  public ArmorStandPoseScreen(
      ArmorStandScreenHandler handler,
      ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);
    this.supportsUndoRedo = true;
  }

  @Override
  public void init() {
    initNavigationButtons();

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
