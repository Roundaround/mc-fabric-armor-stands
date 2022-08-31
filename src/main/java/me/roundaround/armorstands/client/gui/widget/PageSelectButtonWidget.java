package me.roundaround.armorstands.client.gui.widget;

import java.util.function.Consumer;

import me.roundaround.armorstands.ArmorStandsMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PageSelectButtonWidget extends TexturedButtonWidget {
  public static final int WIDTH = 20;
  public static final int HEIGHT = 20;
  protected static final Identifier WIDGETS_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/widgets.png");

  public PageSelectButtonWidget(
      int x,
      int y,
      int uIndex,
      PressAction pressAction,
      Text pageTitle,
      Screen screen) {
    super(
        x,
        y,
        WIDTH,
        HEIGHT,
        uIndex * WIDTH,
        0,
        HEIGHT,
        WIDGETS_TEXTURE,
        256,
        256,
        pressAction,
        new TooltipSupplier(pageTitle, screen),
        pageTitle);
  }

  public static class TooltipSupplier implements ButtonWidget.TooltipSupplier {
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
