package me.roundaround.armorstands.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class MiniButtonWidget extends ButtonWidget {
  private static final int TEX_WIDTH = 200;
  private static final int TEX_HEIGHT = 20;

  public MiniButtonWidget(
      int x,
      int y,
      int width,
      int height,
      Text message,
      PressAction onPress,
      TooltipSupplier tooltipSupplier) {
    super(x, y, width, height, message, onPress, tooltipSupplier);
  }

  public MiniButtonWidget(int x,
      int y,
      int width,
      int height,
      Text message,
      PressAction onPress) {
    super(x, y, width, height, message, onPress);
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    MinecraftClient client = MinecraftClient.getInstance();
    TextRenderer textRenderer = client.textRenderer;

    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.enableDepthTest();

    int u = 0;
    int v = 46 + getYImage(isHovered()) * TEX_HEIGHT;

    drawTexture(matrixStack,
        x,
        y,
        u,
        v,
        width / 2,
        height / 2);
    drawTexture(matrixStack,
        x + width / 2,
        y,
        u + TEX_WIDTH - width / 2,
        v,
        width / 2,
        height / 2);
    drawTexture(matrixStack,
        x,
        y + height / 2,
        u,
        v + TEX_HEIGHT - height / 2,
        width / 2,
        height / 2);
    drawTexture(matrixStack,
        x + width / 2,
        y + height / 2,
        u + TEX_WIDTH - width / 2,
        v + TEX_HEIGHT - height / 2,
        width / 2,
        height / 2);

    renderBackground(matrixStack, client, mouseX, mouseY);

    int color = active ? 0xFFFFFF : 0xA0A0A0;
    drawCenteredText(
        matrixStack,
        textRenderer,
        getMessage(),
        x + width / 2,
        y + (height - 8) / 2,
        color | MathHelper.ceil(alpha * 255) << 24);
  }
}
