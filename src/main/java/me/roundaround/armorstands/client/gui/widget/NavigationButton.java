package me.roundaround.armorstands.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NavigationButton<T extends AbstractArmorStandScreen> extends ButtonWidget {
  public static final int WIDTH = 20;
  public static final int HEIGHT = 20;
  protected static final Identifier WIDGETS_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/widgets.png");

  private final T parent;
  private final int uIndex;

  private boolean clickable;

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
        (button, handler, armorStand) -> {
        },
        uIndex);
    this.clickable = false;
  }

  public NavigationButton(
      MinecraftClient client,
      T parent,
      int x,
      int y,
      Text tooltip,
      ScreenConstructor<T> constructor,
      int uIndex) {
    this(
        client,
        parent,
        x,
        y,
        tooltip,
        (button, handler, armorStand) -> {
          // Get around an issue where it was breaking on screen change
          client.currentScreen = null;
          client.setScreen(constructor.accept(handler, armorStand));
        },
        uIndex);
  }

  @SuppressWarnings("unchecked")
  private NavigationButton(
      MinecraftClient client,
      T parent,
      int x,
      int y,
      Text tooltip,
      PressAction<T> onPress,
      int uIndex) {
    super(
        x,
        y,
        WIDTH,
        HEIGHT,
        tooltip,
        (button) -> {
          onPress.accept((NavigationButton<T>) button, parent.getScreenHandler(), parent.getArmorStand());
        });
    this.parent = parent;
    this.uIndex = uIndex;
    this.clickable = true;
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
  public static interface PressAction<T extends AbstractArmorStandScreen> {
    public void accept(
        NavigationButton<T> button,
        ArmorStandScreenHandler handler,
        ArmorStandEntity armorStand);
  }
}
