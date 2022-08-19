package me.roundaround.armorstands.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PageChangeButtonWidget extends ButtonWidget {
  public static final int WIDTH = 14;
  public static final int HEIGHT = 22;

  private static final Identifier RESOURCE_PACKS_TEXTURE = new Identifier(
      Identifier.DEFAULT_NAMESPACE,
      "textures/gui/resource_packs.png");
  private static final Text PREVIOUS_TEXT = Text.literal("Previous page");
  private static final Text NEXT_TEXT = Text.literal("Next page");

  private final boolean forward;

  public PageChangeButtonWidget(AbstractArmorStandScreen parent, int x, int y, boolean forward) {
    super(x, y, WIDTH, HEIGHT, forward ? NEXT_TEXT : PREVIOUS_TEXT, (button) -> {
      if (forward) {
        parent.nextPage();
      } else {
        parent.previousPage();
      }
    });

    this.forward = forward;
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.setShaderTexture(0, RESOURCE_PACKS_TEXTURE);

    int u = forward ? 11 : 35;
    int v = isHovered() ? 38 : 6;

    drawTexture(matrixStack, x, y, u, v, width, height);
  }
}
