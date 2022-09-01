package me.roundaround.armorstands.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class LabelWidget extends DrawableHelper implements Drawable {
  private final Text text;
  private final int posX;
  private final int posY;
  private final Alignment alignmentH;
  private final Alignment alignmentV;
  private final boolean showBackground;
  private final boolean showTextShadow;
  private final MinecraftClient client;
  private final TextRenderer textRenderer;
  private final int textWidth;

  // TODO: Add option to include background padding in positioning

  private LabelWidget(
      Text text,
      int posX,
      int posY,
      Alignment alignmentH,
      Alignment alignmentV,
      boolean showBackground,
      boolean showTextShadow) {
    this.text = text;
    this.posX = posX;
    this.posY = posY;
    this.alignmentH = alignmentH;
    this.alignmentV = alignmentV;
    this.showBackground = showBackground;
    this.showTextShadow = showTextShadow;

    client = MinecraftClient.getInstance();
    textRenderer = client.textRenderer;
    textWidth = textRenderer.getWidth(text);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    float left, right;
    switch (alignmentH) {
      case CENTER:
        left = posX - textWidth / 2f;
        right = posX + textWidth / 2f;
        break;
      case END:
        left = posX - textWidth;
        right = posX;
        break;
      case START:
      default:
        left = posX;
        right = posX + textWidth;
    }

    float top, bottom;
    switch (alignmentV) {
      case START:
        top = posY;
        bottom = posY + 10;
        break;
      case END:
        top = posY - 10;
        bottom = posY;
        break;
      case CENTER:
      default:
        top = posY - 10 / 2f;
        bottom = posY + 10 / 2f;
    }

    if (showBackground) {
      fill(
          matrixStack,
          MathHelper.floor(left) - 2,
          MathHelper.floor(top) - 1,
          MathHelper.ceil(right) + 2,
          MathHelper.ceil(bottom) + 1,
          0x40000000);
    }

    if (showTextShadow) {
      textRenderer.drawWithShadow(
          matrixStack,
          text,
          left + 0.5f,
          top + 1,
          0xFFFFFFFF);
    } else {
      textRenderer.draw(
          matrixStack,
          text,
          left + 0.5f,
          top + 1,
          0xFFFFFFFF);
    }
  }

  public static Builder builder(Text text, int posX, int posY) {
    return new Builder(text, posX, posY);
  }

  public static class Builder implements DrawableBuilder<LabelWidget> {
    private final Text text;
    private final int posX;
    private final int posY;
    private Alignment alignmentH = Alignment.START;
    private Alignment alignmentV = Alignment.CENTER;
    private boolean showBackground = true;
    private boolean showTextShadow = false;

    public Builder(Text text, int posX, int posY) {
      this.text = text;
      this.posX = posX;
      this.posY = posY;
    }

    public Builder justifiedLeft() {
      alignmentH = Alignment.START;
      return this;
    }

    public Builder justifiedCenter() {
      alignmentH = Alignment.CENTER;
      return this;
    }

    public Builder justifiedRight() {
      alignmentH = Alignment.END;
      return this;
    }

    public Builder alignedTop() {
      alignmentV = Alignment.START;
      return this;
    }

    public Builder alignedMiddle() {
      alignmentV = Alignment.CENTER;
      return this;
    }

    public Builder alignedBottom() {
      alignmentV = Alignment.END;
      return this;
    }

    public Builder hideBackground() {
      showBackground = false;
      return this;
    }

    public Builder showTextShadow() {
      showTextShadow = true;
      return this;
    }

    @Override
    public LabelWidget build() {
      return new LabelWidget(text, posX, posY, alignmentH, alignmentV, showBackground, showTextShadow);
    }
  }

  public static enum Alignment {
    START,
    CENTER,
    END;
  }
}
