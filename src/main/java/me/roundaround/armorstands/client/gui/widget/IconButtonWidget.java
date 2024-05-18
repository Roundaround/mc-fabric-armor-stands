package me.roundaround.armorstands.client.gui.widget;

import me.roundaround.armorstands.ArmorStandsMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
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
      int x, int y, int textureIndex, Text tooltip, ButtonWidget.PressAction onPress) {
    super(x, y, WIDTH, HEIGHT, Text.empty(), onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);

    this.textureIndex = textureIndex;
    setTooltip(Tooltip.of(tooltip));
  }

  @Override
  public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    super.renderWidget(drawContext, mouseX, mouseY, delta);

    int uIndex = this.textureIndex % ICONS_PER_ROW;
    int vIndex = this.textureIndex / ICONS_PER_ROW;

    int u = uIndex * WIDTH;
    int v = vIndex * HEIGHT;
    drawContext.drawTexture(TEXTURE, getX(), getY(), u, v, WIDTH, HEIGHT);
  }
}
