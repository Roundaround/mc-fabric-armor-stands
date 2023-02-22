package me.roundaround.armorstands.client.gui.widget;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class SimpleTooltipButtonWidget extends MiniButtonWidget {
  private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

  private final Screen parent;

  private List<OrderedText> tooltip = List.of();

  public SimpleTooltipButtonWidget(
      Screen parent,
      int x,
      int y,
      int width,
      int height,
      Text message,
      Text tooltip,
      PressAction onPress) {
    this(parent, x, y, width, height, message, wrapLines(tooltip, 200), onPress);
  }

  public SimpleTooltipButtonWidget(
      Screen parent,
      int x,
      int y,
      int width,
      int height,
      Text message,
      List<OrderedText> tooltip,
      PressAction onPress) {
    super(x, y, width, height, message, onPress);

    this.parent = parent;
    this.tooltip = tooltip;
  }

  public void setTooltip(Text tooltip) {
    this.tooltip = wrapLines(tooltip, 200);
  }

  public void setTooltip(List<OrderedText> tooltip) {
    this.tooltip = tooltip;
  }

  @Override
  public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
    this.parent.renderOrderedTooltip(
        matrixStack,
        this.tooltip,
        this.hovered ? mouseX : this.x,
        this.hovered ? mouseY : this.y);
  }

  private static List<OrderedText> wrapLines(Text text, int width) {
    return CLIENT.textRenderer.wrapLines(text, width);
  }
}
