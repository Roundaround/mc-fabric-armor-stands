package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.client.gui.widget.PageSelectButtonWidget;
import me.roundaround.armorstands.client.gui.widget.PoseListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ArmorStandPosePage extends AbstractArmorStandPage {
  private static final int SCREEN_EDGE_PAD = 4;

  private PoseListWidget list;

  public ArmorStandPosePage(MinecraftClient client, ArmorStandScreen screen) {
    super(client, screen, Text.translatable("armorstands.page.pose"), 4);
  }

  @Override
  public void postInit() {
    int width = MathHelper.floor(screen.width / 2f
        - 2.5f * PageSelectButtonWidget.WIDTH
        - 2 * SCREEN_EDGE_PAD);
    int height = screen.height - 2 * SCREEN_EDGE_PAD;

    int refX = screen.width - SCREEN_EDGE_PAD - width;
    int refY = screen.height - SCREEN_EDGE_PAD - height;

    this.list = new PoseListWidget(
        client,
        refX,
        refY,
        width,
        height);
    screen.addSelectableChild(this.list);
  }

  @Override
  public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.list.render(matrixStack, mouseX, mouseY, partialTicks);
  }
}
