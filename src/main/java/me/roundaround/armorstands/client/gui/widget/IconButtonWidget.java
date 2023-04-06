package me.roundaround.armorstands.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.armorstands.ArmorStandsMod;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButtonWidget extends ButtonWidget {
  public static final int WIDTH = 20;
  public static final int HEIGHT = 20;
  protected static final Identifier TEXTURE =
      new Identifier(ArmorStandsMod.MOD_ID, "textures/gui/widgets.png");
  protected static final int TEXTURE_WIDTH = 256;
  protected static final int ICONS_PER_ROW = TEXTURE_WIDTH / WIDTH;

  protected final int textureIndex;

  public IconButtonWidget(
      int x,
      int y,
      int textureIndex,
      Text tooltip,
      ButtonWidget.PressAction onPress) {
    super(x, y, WIDTH, HEIGHT, Text.empty(), onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);

    this.textureIndex = textureIndex;
    setTooltip(Tooltip.of(tooltip));
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    super.renderButton(matrixStack, mouseX, mouseY, delta);

    RenderSystem.setShaderTexture(0, TEXTURE);

    int uIndex = this.textureIndex % ICONS_PER_ROW;
    int vIndex = this.textureIndex / ICONS_PER_ROW;

    int u = uIndex * WIDTH;
    int v = vIndex * HEIGHT;
    drawTexture(matrixStack, getX(), getY(), u, v, WIDTH, HEIGHT);
  }
}
