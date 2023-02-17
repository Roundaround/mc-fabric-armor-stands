package me.roundaround.armorstands.client.gui.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButtonWidget extends ButtonWidget {
  public static final int EDIT_ICON = 0;
  public static final int SAVE_ICON = 1;
  public static final int DELETE_ICON = 2;
  public static final int CANCEL_ICON = 3;
  public static final int RELOAD_ICON = 4;
  public static final int WIDTH = 20;
  public static final int HEIGHT = 20;
  protected static final Identifier BACKGROUND_TEXTURE = new Identifier(
      Identifier.DEFAULT_NAMESPACE,
      "textures/gui/widgets.png");
  protected static final Identifier WIDGETS_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/widgets.png");

  protected final int textureIndex;

  public IconButtonWidget(
      MinecraftClient client,
      int x,
      int y,
      int textureIndex,
      Text tooltip,
      PressAction onPress) {
    super(
        x,
        y,
        WIDTH,
        HEIGHT,
        tooltip,
        onPress,
        new TooltipSupplier(client, tooltip));
    this.textureIndex = textureIndex;
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.enableDepthTest();
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
    int vOffset = (this.isHovered() ? 2 : 1) * HEIGHT;
    drawTexture(
        matrixStack,
        this.x,
        this.y,
        0,
        46 + vOffset,
        WIDTH / 2,
        HEIGHT);
    drawTexture(
        matrixStack,
        this.x + WIDTH / 2,
        this.y,
        200 - WIDTH / 2,
        46 + vOffset,
        WIDTH / 2,
        HEIGHT);

    int uIndex = this.textureIndex % 5;
    int vIndex = this.textureIndex / 5;
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    drawTexture(
        matrixStack,
        this.x,
        this.y,
        uIndex * WIDTH,
        vIndex * HEIGHT,
        WIDTH,
        HEIGHT,
        WIDTH * 5,
        HEIGHT * 3);

    if (hovered) {
      renderTooltip(matrixStack, mouseX, mouseY);
    }
  }

  private static class TooltipSupplier implements ButtonWidget.TooltipSupplier {
    private final MinecraftClient client;
    private final Text tooltip;

    public TooltipSupplier(MinecraftClient client, Text tooltip) {
      this.client = client;
      this.tooltip = tooltip;
    }

    @Override
    public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int x, int y) {
      this.client.currentScreen.renderOrderedTooltip(
          matrixStack,
          this.client.textRenderer.wrapLines(this.tooltip, 200),
          x,
          y);
    }

    @Override
    public void supply(Consumer<Text> consumer) {
      consumer.accept(this.tooltip);
    }
  }
}
