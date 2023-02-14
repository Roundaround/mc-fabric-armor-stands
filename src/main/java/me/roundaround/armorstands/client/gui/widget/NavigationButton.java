package me.roundaround.armorstands.client.gui.widget;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.ArmorStandState;
import me.roundaround.armorstands.client.gui.HasArmorStandState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NavigationButton<T extends Screen & HasArmorStandState> extends ButtonWidget {
  public static final int WIDTH = 20;
  public static final int HEIGHT = 20;
  protected static final Identifier WIDGETS_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/widgets.png");

  private final int uIndex;

  public NavigationButton(
      MinecraftClient client,
      T parent,
      int x,
      int y,
      Text tooltip,
      int uIndex) {
    this(
        client,
        parent,
        x,
        y,
        tooltip,
        (button, state) -> {
        },
        uIndex);
  }

  public NavigationButton(
      MinecraftClient client,
      T parent,
      int x,
      int y,
      Text tooltip,
      Function<ArmorStandState, T> supplier,
      int uIndex) {
    this(
        client,
        parent,
        x,
        y,
        tooltip,
        (button, state) -> {
          client.setScreen(supplier.apply(state));
        },
        uIndex);
  }

  @SuppressWarnings("unchecked")
  public NavigationButton(
      MinecraftClient client,
      T parent,
      int x,
      int y,
      Text tooltip,
      BiConsumer<NavigationButton<T>, ArmorStandState> onPress,
      int uIndex) {
    super(
        x,
        y,
        WIDTH,
        HEIGHT,
        tooltip,
        (button) -> {
          onPress.accept((NavigationButton<T>) button, parent.getState());
        },
        new TooltipSupplier(tooltip, parent));
    this.uIndex = uIndex;
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.enableDepthTest();

    int vIndex = isHovered() ? 2 : 1;

    int u = uIndex * WIDTH;
    int v = vIndex * HEIGHT;
    drawTexture(matrixStack, x, y, u, v, WIDTH, HEIGHT);

    if (hovered) {
      renderTooltip(matrixStack, mouseX, mouseY);
    }
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

  @FunctionalInterface
  private static interface ScreenSupplier {
    public Screen get(ArmorStandState state);
  }
}
