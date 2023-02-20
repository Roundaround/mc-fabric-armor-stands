package me.roundaround.armorstands.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen.ScreenFactory;
import me.roundaround.armorstands.client.util.LastUsedScreen;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NavigationButton<P extends AbstractArmorStandScreen, T extends AbstractArmorStandScreen>
    extends ButtonWidget {
  public static final int WIDTH = 20;
  public static final int HEIGHT = 20;
  protected static final Identifier WIDGETS_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/widgets.png");

  private final T parent;
  private final int uIndex;

  private boolean clickable;

  @SuppressWarnings("unchecked")
  private NavigationButton(
      MinecraftClient client,
      T parent,
      int x,
      int y,
      Text tooltip,
      PressAction<P, T> onPress,
      boolean clickable,
      int uIndex) {
    super(
        x,
        y,
        WIDTH,
        HEIGHT,
        tooltip,
        (button) -> {
          onPress.accept((NavigationButton<P, T>) button, parent.getScreenHandler(), parent.getArmorStand());
        });
    this.parent = parent;
    this.uIndex = uIndex;
    this.clickable = true;
  }

  public static NavigationButton<?, ?> create(
      MinecraftClient client,
      AbstractArmorStandScreen parent,
      int x,
      int y,
      ScreenFactory<?> factory) {
    return new NavigationButton<>(
        client,
        parent,
        x,
        y,
        factory.tooltip,
        (button, handler, armorStand) -> {
          if (factory.constructor == null) {
            return;
          }
          client.currentScreen = null;
          AbstractArmorStandScreen nextScreen = factory.constructor.accept(handler, armorStand);
          LastUsedScreen.set(nextScreen);
          client.setScreen(nextScreen);
        },
        factory.constructor == null,
        factory.uIndex);
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.enableDepthTest();

    int vIndex = this.clickable && isHovered() ? 2 : 1;

    int u = uIndex * WIDTH;
    int v = vIndex * HEIGHT;
    drawTexture(matrixStack, x, y, u, v, WIDTH, HEIGHT);

    if (isHovered()) {
      renderTooltip(matrixStack, mouseX, mouseY);
    }
  }

  @Override
  public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
    parent.renderTooltip(matrixStack, getMessage(), mouseX, mouseY);
  }

  @FunctionalInterface
  public static interface ScreenConstructor<T extends AbstractArmorStandScreen> {
    public T accept(
        ArmorStandScreenHandler handler,
        ArmorStandEntity armorStand);
  }

  @FunctionalInterface
  public static interface PressAction<P extends AbstractArmorStandScreen, T extends AbstractArmorStandScreen> {
    public void accept(
        NavigationButton<P, T> button,
        ArmorStandScreenHandler handler,
        ArmorStandEntity armorStand);
  }
}
