package me.roundaround.armorstands.client.gui.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.page.ArmorStandPage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PageSelectButtonWidget extends ButtonWidget {
  public static final int WIDTH = 20;
  public static final int HEIGHT = 20;
  protected static final Identifier WIDGETS_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/widgets.png");

  private final int uIndex;

  public PageSelectButtonWidget(
      int x,
      int y,
      Screen screen,
      ArmorStandPage page,
      int pageNum) {
    super(
        x,
        y,
        WIDTH,
        HEIGHT,
        page.getTitle(),
        (button) -> {
        },
        new TooltipSupplier(page.getTitle(), screen));
    uIndex = page.getTextureU();
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.enableDepthTest();

    // int vIndex = isActivePage() ? 0 : (isHovered() ? 2 : 1);
    int vIndex = isHovered() ? 2 : 1;

    int u = uIndex * WIDTH;
    int v = vIndex * HEIGHT;
    drawTexture(matrixStack, x, y, u, v, WIDTH, HEIGHT);

    if (hovered) {
      renderTooltip(matrixStack, mouseX, mouseY);
    }
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (isActivePage()) {
      return false;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  public boolean isActivePage() {
    return false;
  }

  private static class TooltipSupplier implements ButtonWidget.TooltipSupplier {
    private final Text pageTitle;
    private final Screen screen;

    public TooltipSupplier(Text pageTitle, Screen screen) {
      this.pageTitle = pageTitle;
      this.screen = screen;
    }

    @Override
    public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int x, int y) {
      screen.renderTooltip(matrixStack, pageTitle, x, y);
    }

    @Override
    public void supply(Consumer<Text> consumer) {
      consumer.accept(pageTitle);
    }
  }
}
