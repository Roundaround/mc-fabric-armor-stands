package me.roundaround.armorstands.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.packet.c2s.SetFlagPacket;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class FlagToggleWidget extends PressableWidget {
  public static final int WIDGET_WIDTH = 16;
  public static final int WIDGET_HEIGHT = 12;
  private static final int BAR_WIDTH = 10;
  private static final int TEXTURE_WIDTH = 200;
  private static final int TEXTURE_HEIGHT = 20;

  private final ArmorStandFlag flag;
  private final boolean inverted;
  private final LabelWidget flagLabel;
  private final LabelWidget valueLabel;
  private final int widgetX;
  private final int widgetY;
  private boolean currentValue = false;

  public FlagToggleWidget(
      TextRenderer textRenderer,
      ArmorStandFlag flag,
      boolean initialValue,
      int x,
      int y) {
    super(x, y, 100, WIDGET_HEIGHT, flag.getDisplayName());
    this.flag = flag;
    this.inverted = flag.invertControl();

    int valueLabelWidth = 2 * LabelWidget.PADDING + Math.max(
        textRenderer.getWidth(Text.translatable("armorstands.flagToggle.on")),
        textRenderer.getWidth(Text.translatable("armorstands.flagToggle.off")));

    this.flagLabel = LabelWidget.builder(
        flag.getDisplayName(),
        x - WIDGET_WIDTH - 2 - valueLabelWidth - 2,
        y + height / 2)
        .justifiedRight()
        .alignedMiddle()
        .shiftForPadding()
        .build();

    this.valueLabel = LabelWidget.builder(
        Text.translatable("armorstands.flagToggle." + (initialValue ^ inverted ? "on" : "off")),
        x - valueLabelWidth / 2,
        y + height / 2)
        .justifiedCenter()
        .alignedMiddle()
        .shiftForPadding()
        .build();

    currentValue = initialValue;

    widgetX = x - WIDGET_WIDTH - 2 - valueLabelWidth;
    widgetY = y + (height - WIDGET_HEIGHT) / 2;
  }

  @Override
  public void onPress() {
    SetFlagPacket.sendToServer(flag, !currentValue);
  }

  @Override
  public void appendClickableNarrations(NarrationMessageBuilder builder) {
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return active && visible && isWithinBounds(mouseX, mouseY);
  }

  @Override
  protected boolean clicked(double mouseX, double mouseY) {
    return isMouseOver(mouseX, mouseY);
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    hovered = isWithinBounds(mouseX, mouseY);

    renderWidget(matrixStack, mouseX, mouseY, delta);
    flagLabel.render(matrixStack, mouseX, mouseY, delta);
    valueLabel.render(matrixStack, mouseX, mouseY, delta);
  }

  public void setValue(boolean newValue) {
    currentValue = newValue;
    valueLabel.setText(Text.translatable("armorstands.flagToggle." + (newValue ^ inverted ? "on" : "off")));
  }

  private void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.enableDepthTest();

    int u1 = 0;
    int u2 = TEXTURE_WIDTH - WIDGET_WIDTH / 2;
    int v1 = getTextureY();
    int v2 = v1 + TEXTURE_HEIGHT - WIDGET_HEIGHT / 2;

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

  private int getTextureY() {
    int i = 1;
    if (!this.active) {
      i = 0;
    } else if (this.isSelected()) {
      i = 2;
    }

    return 46 + i * 20;
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

  private boolean isWithinBounds(double mouseX, double mouseY) {
    return mouseX >= this.flagLabel.getLeft() && mouseX < this.valueLabel.getRight()
        && mouseY >= this.flagLabel.getTop() && mouseY < this.flagLabel.getBottom();
  }
}
