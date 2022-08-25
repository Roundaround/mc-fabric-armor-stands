package me.roundaround.armorstands.client.gui.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ArmorStandFlag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ArmorStandFlagToggleWidget extends PressableWidget implements Consumer<Boolean> {
  public static final int WIDGET_WIDTH = 14;
  public static final int WIDGET_HEIGHT = 10;
  private static final int BAR_WIDTH = 8;
  private static final int TEXTURE_WIDTH = 200;
  private static final int TEXTURE_HEIGHT = 20;

  private final MinecraftClient client;
  private final ArmorStandEntity armorStand;
  private final ArmorStandFlag flag;
  private final boolean inverted;
  private boolean currentValue = false;
  private int textWidth;

  public ArmorStandFlagToggleWidget(
      MinecraftClient client,
      ArmorStandEntity armorStand,
      ArmorStandFlag flag,
      boolean inverted,
      boolean initialValue,
      int x,
      int y,
      int width,
      Text label) {
    super(x, y, width, WIDGET_HEIGHT, label);
    this.client = client;
    this.armorStand = armorStand;
    this.flag = flag;
    this.inverted = inverted;
    currentValue = initialValue;
    textWidth = client.textRenderer.getWidth(label);
  }

  @Override
  public void onPress() {
    ClientNetworking.sendSetFlagPacket(armorStand, flag, !currentValue);
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
  }

  @Override
  public void accept(Boolean newValue) {
    currentValue = newValue;
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    int right = x + width;
    int left = right - WIDGET_WIDTH - 4 - textWidth;

    return active && visible
        && mouseX >= left && mouseX < right
        && mouseY >= y && mouseY < y + height;
  }

  @Override
  protected boolean clicked(double mouseX, double mouseY) {
    return isMouseOver(mouseX, mouseY);
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    int right = x + width;
    int left = right - WIDGET_WIDTH - 4 - textWidth;
    hovered = mouseX >= left && mouseX < right && mouseY >= y && mouseY < y + height;

    renderBackground(matrixStack, mouseX, mouseY, delta);
    renderWidget(matrixStack, mouseX, mouseY, delta);
    renderLabel(matrixStack, mouseX, mouseY, delta);
  }

  public void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    textWidth = client.textRenderer.getWidth(getMessage());

    int left = x + width - WIDGET_WIDTH - textWidth - 4;
    int right = x + width;
    int top = y + Math.round((height - 10) / 2f);
    int bottom = y + Math.round((height + 10) / 2f);

    fill(
        matrixStack,
        left - 2,
        top - 2,
        right + 2,
        bottom + 2,
        0x40000000);
  }

  public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.enableDepthTest();

    int widgetX = x + width - WIDGET_WIDTH;
    int widgetY = y + (height - WIDGET_HEIGHT) / 2;

    int u1 = 0;
    int u2 = TEXTURE_WIDTH - WIDGET_WIDTH / 2;
    int v1 = 46;
    int v2 = 46 + TEXTURE_HEIGHT - WIDGET_HEIGHT / 2;

    drawTexture(
        matrixStack,
        widgetX,
        widgetY,
        u1,
        v1,
        WIDGET_WIDTH / 2,
        WIDGET_HEIGHT / 2);
    drawTexture(matrixStack,
        widgetX + WIDGET_WIDTH / 2,
        widgetY,
        u2,
        v1,
        WIDGET_WIDTH / 2,
        WIDGET_HEIGHT / 2);
    drawTexture(matrixStack,
        widgetX,
        widgetY + WIDGET_HEIGHT / 2,
        u1,
        v2,
        WIDGET_WIDTH / 2,
        WIDGET_HEIGHT / 2);
    drawTexture(matrixStack,
        widgetX + WIDGET_WIDTH / 2,
        widgetY + WIDGET_HEIGHT / 2,
        u2,
        v2,
        WIDGET_WIDTH / 2,
        WIDGET_HEIGHT / 2);

    renderBar(matrixStack, mouseX, mouseY, delta);
  }

  public void renderBar(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    int widgetX = x + width - WIDGET_WIDTH;
    int offset = (currentValue ^ inverted) ? WIDGET_WIDTH - BAR_WIDTH : 0;
    int widgetY = y + (height - WIDGET_HEIGHT) / 2;

    int u1 = 0;
    int u2 = TEXTURE_WIDTH - BAR_WIDTH / 2;
    int v1 = 46 + (isHovered() ? 2 : 1) * 20;
    int v2 = 46 + TEXTURE_HEIGHT - WIDGET_HEIGHT / 2 + (isHovered() ? 2 : 1) * 20;

    drawTexture(
        matrixStack,
        widgetX + offset,
        widgetY,
        u1,
        v1,
        BAR_WIDTH / 2,
        WIDGET_HEIGHT / 2);
    drawTexture(matrixStack,
        widgetX + offset + BAR_WIDTH / 2,
        widgetY,
        u2,
        v1,
        BAR_WIDTH / 2,
        WIDGET_HEIGHT / 2);
    drawTexture(matrixStack,
        widgetX + offset,
        widgetY + WIDGET_HEIGHT / 2,
        u1,
        v2,
        BAR_WIDTH / 2,
        WIDGET_HEIGHT / 2);
    drawTexture(matrixStack,
        widgetX + offset + BAR_WIDTH / 2,
        widgetY + WIDGET_HEIGHT / 2,
        u2,
        v2,
        BAR_WIDTH / 2,
        WIDGET_HEIGHT / 2);
  }

  public void renderLabel(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    TextRenderer textRenderer = client.textRenderer;
    textWidth = textRenderer.getWidth(getMessage());

    int right = x + width - WIDGET_WIDTH - 4;
    int left = right - textWidth;

    textRenderer.draw(
        matrixStack,
        getMessage(),
        left,
        y + (height - 8) / 2,
        active ? 0xFFFFFFFF : 0xFFA0A0A0);
  }
}
