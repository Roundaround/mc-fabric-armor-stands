package me.roundaround.armorstands.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.packet.c2s.SetFlagPacket;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FlagToggleWidget extends PressableWidget {
  public static final int WIDGET_WIDTH = 16;
  public static final int WIDGET_HEIGHT = 12;
  private static final int BAR_WIDTH = 10;
  private static final Identifier TEXTURE = new Identifier("widget/slider");
  private static final Identifier HANDLE_TEXTURE = new Identifier("widget/slider_handle");
  private static final Identifier HANDLE_HIGHLIGHTED_TEXTURE =
      new Identifier("widget/slider_handle_highlighted");

  private final ArmorStandFlag flag;
  private final boolean inverted;
  private final LabelWidget flagLabel;
  private final LabelWidget valueLabel;
  private final int widgetX;
  private final int widgetY;
  private boolean currentValue = false;

  public FlagToggleWidget(
      TextRenderer textRenderer, ArmorStandFlag flag, boolean initialValue, int x, int y) {
    super(x, y, 100, WIDGET_HEIGHT, flag.getDisplayName());
    this.flag = flag;
    this.inverted = flag.invertControl();

    int valueLabelWidth = 2 * LabelWidget.PADDING +
        Math.max(textRenderer.getWidth(Text.translatable("armorstands.flagToggle.on")),
            textRenderer.getWidth(Text.translatable("armorstands.flagToggle.off")));

    this.flagLabel = LabelWidget.builder(flag.getDisplayName(),
        x - WIDGET_WIDTH - 2 - valueLabelWidth - 2,
        y + height / 2).justifiedRight().alignedMiddle().shiftForPadding().build();

    this.valueLabel = LabelWidget.builder(Text.translatable(
            "armorstands.flagToggle." + (initialValue ^ inverted ? "on" : "off")),
        x - valueLabelWidth / 2,
        y + height / 2).justifiedCenter().alignedMiddle().shiftForPadding().build();

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
  public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    hovered = isWithinBounds(mouseX, mouseY);

    renderWidget(drawContext, mouseX, mouseY, delta);
    flagLabel.render(drawContext, mouseX, mouseY, delta);
    valueLabel.render(drawContext, mouseX, mouseY, delta);
  }

  public void setValue(boolean newValue) {
    currentValue = newValue;
    valueLabel.setText(Text.translatable(
        "armorstands.flagToggle." + (newValue ^ inverted ? "on" : "off")));
  }

  private void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    int offset = (currentValue ^ inverted) ? WIDGET_WIDTH - BAR_WIDTH : 0;

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.enableDepthTest();

    drawContext.drawGuiTexture(TEXTURE, widgetX, widgetY, WIDGET_WIDTH, WIDGET_HEIGHT);
    drawContext.drawGuiTexture(getHandleTexture(),
        widgetX + offset,
        widgetY,
        BAR_WIDTH,
        WIDGET_HEIGHT);
  }

  private boolean isWithinBounds(double mouseX, double mouseY) {
    return mouseX >= this.flagLabel.getLeft() && mouseX < this.valueLabel.getRight() &&
        mouseY >= this.flagLabel.getTop() && mouseY < this.flagLabel.getBottom();
  }

  private Identifier getTexture() {
    return TEXTURE;
  }

  private Identifier getHandleTexture() {
    if (this.isFocused() || this.hovered) {
      return HANDLE_HIGHLIGHTED_TEXTURE;
    }
    return HANDLE_TEXTURE;
  }
}
