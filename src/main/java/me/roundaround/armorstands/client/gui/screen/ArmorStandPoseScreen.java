package me.roundaround.armorstands.client.gui.screen;

import me.roundaround.armorstands.client.gui.ArmorStandState;
import me.roundaround.armorstands.client.gui.widget.NavigationButton;
import me.roundaround.armorstands.client.gui.widget.PoseListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ArmorStandPoseScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.page.pose");

  private static final int SCREEN_EDGE_PAD = 4;

  private PoseListWidget list;

  public ArmorStandPoseScreen(ArmorStandState state) {
    super(TITLE, state);
  }

  @Override
  protected boolean supportsUndoRedo() {
    return true;
  }

  @Override
  public void init() {
    initNavigationButtons();

    int width = MathHelper.floor(this.width / 2f
        - 2.5f * NavigationButton.WIDTH
        - 2 * SCREEN_EDGE_PAD);
    int height = this.height - 2 * SCREEN_EDGE_PAD;

    int refX = this.width - SCREEN_EDGE_PAD - width;
    int refY = this.height - SCREEN_EDGE_PAD - height;

    this.list = new PoseListWidget(
        client,
        this.state,
        refX,
        refY,
        width,
        height);
    addSelectableChild(this.list);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.list.render(matrixStack, mouseX, mouseY, partialTicks);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
  }
}
