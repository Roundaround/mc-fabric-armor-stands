package me.roundaround.armorstands.client.gui.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.client.gui.ArmorStandState;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ArmorStandFlag;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ArmorStandFlagToggleWidget extends PressableWidget implements Consumer<Boolean> {
  public static final int WIDGET_WIDTH = 16;
  public static final int WIDGET_HEIGHT = 12;
  private static final int BAR_WIDTH = 10;
  private static final int TEXTURE_WIDTH = 200;
  private static final int TEXTURE_HEIGHT = 20;

  private final ArmorStandState state;
  private final ArmorStandFlag flag;
  private final boolean inverted;
  private final LabelWidget labelWidget;
  private final int widgetX;
  private final int widgetY;
  private boolean currentValue = false;

  public ArmorStandFlagToggleWidget(
      ArmorStandState state,
      ArmorStandFlag flag,
      boolean inverted,
      boolean initialValue,
      int x,
      int y,
      int width,
      Text label) {
    super(x, y, width, WIDGET_HEIGHT, label);
    this.state = state;
    this.flag = flag;
    this.inverted = inverted;

    this.labelWidget = LabelWidget.builder(
        label,
        x + width - WIDGET_WIDTH - 2,
        y + height / 2)
        .justifiedRight()
        .alignedMiddle()
        .shiftForPadding()
        .build();

    currentValue = initialValue;

    widgetX = x + width - WIDGET_WIDTH;
    widgetY = y + (height - WIDGET_HEIGHT) / 2;
  }

  @Override
  public void onPress() {
    ClientNetworking.sendSetFlagPacket(this.state.getArmorStand(), flag, !currentValue);
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
    return active && visible
        && (labelWidget.isMouseOver(mouseX, mouseY) || isWidgetMouseOver(mouseX, mouseY));
  }

  @Override
  protected boolean clicked(double mouseX, double mouseY) {
    return isMouseOver(mouseX, mouseY);
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    hovered = labelWidget.isMouseOver(mouseX, mouseY) || isWidgetMouseOver(mouseX, mouseY);

    renderWidget(matrixStack, mouseX, mouseY, delta);
    labelWidget.render(matrixStack, mouseX, mouseY, delta);
  }

  private void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.enableDepthTest();

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

  private void renderBar(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    int offset = (currentValue ^ inverted) ? WIDGET_WIDTH - BAR_WIDTH : 0;

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

  private boolean isWidgetMouseOver(double mouseX, double mouseY) {
    return mouseX >= widgetX && mouseX < widgetX + WIDGET_WIDTH
        && mouseY >= widgetY && mouseY < widgetY + WIDGET_HEIGHT;
  }
}
