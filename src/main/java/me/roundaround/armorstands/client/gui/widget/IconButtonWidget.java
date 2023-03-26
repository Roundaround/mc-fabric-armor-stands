package me.roundaround.armorstands.client.gui.widget;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButtonWidget<P extends AbstractArmorStandScreen> extends SimpleTooltipButtonWidget {
  public static final int WIDTH = 20;
  public static final int HEIGHT = 20;
  protected static final Identifier TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/widgets.png");
  protected static final int TEXTURE_WIDTH = 256;
  protected static final int TEXTURE_HEIGHT = 256;
  protected static final int ICONS_PER_ROW = TEXTURE_WIDTH / WIDTH;

  protected final P parent;
  protected final int textureIndex;

  @SuppressWarnings("unchecked")
  public IconButtonWidget(
      MinecraftClient client,
      P parent,
      int x,
      int y,
      int textureIndex,
      List<OrderedText> tooltip,
      PressAction<P> onPress) {
    super(
        parent,
        x,
        y,
        WIDTH,
        HEIGHT,
        Text.empty(),
        tooltip,
        (button) -> {
          onPress.accept((IconButtonWidget<P>) button);
        });

    this.parent = parent;
    this.textureIndex = textureIndex;
  }

  @SuppressWarnings("unchecked")
  public IconButtonWidget(
      MinecraftClient client,
      P parent,
      int x,
      int y,
      int textureIndex,
      Text tooltip,
      PressAction<P> onPress) {
    super(
        parent,
        x,
        y,
        WIDTH,
        HEIGHT,
        Text.empty(),
        tooltip,
        (button) -> {
          onPress.accept((IconButtonWidget<P>) button);
        });

    this.parent = parent;
    this.textureIndex = textureIndex;
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    super.renderButton(matrixStack, mouseX, mouseY, delta);

    RenderSystem.setShaderTexture(0, TEXTURE);

    int uIndex = this.textureIndex % ICONS_PER_ROW;
    int vIndex = this.textureIndex / ICONS_PER_ROW;

    int u = uIndex * WIDTH;
    int v = vIndex * HEIGHT;
    drawTexture(matrixStack, x, y, u, v, WIDTH, HEIGHT);

    if (isHovered()) {
      renderTooltip(matrixStack, mouseX, mouseY);
    }
  }

  @FunctionalInterface
  public static interface PressAction<P extends AbstractArmorStandScreen> {
    void accept(IconButtonWidget<P> button);
  }
}
