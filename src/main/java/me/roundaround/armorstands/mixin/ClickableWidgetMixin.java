package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin {
  private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
  private static final int TEX_WIDTH = 200;
  private static final int TEX_HEIGHT = 20;

  @Shadow
  protected int width;

  @Shadow
  protected int height;

  @Shadow
  public int x;

  @Shadow
  public int y;

  @Shadow
  public boolean active;

  @Shadow
  protected float alpha;

  @Shadow
  public abstract boolean isHovered();

  @Shadow
  protected abstract int getYImage(boolean hovered);

  @Shadow
  protected abstract void renderBackground(
      MatrixStack matrixStack,
      MinecraftClient client,
      int mouseX,
      int mouseY);

  @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
  public void renderButton(
      MatrixStack matrixStack,
      int mouseX,
      int mouseY,
      float delta,
      CallbackInfo info) {
    if (this.width >= TEX_WIDTH && this.height >= TEX_HEIGHT) {
      return;
    }

    info.cancel();

    MinecraftClient client = MinecraftClient.getInstance();
    TextRenderer textRenderer = client.textRenderer;

    RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.enableDepthTest();

    int u = 0;
    int v = 46 + getYImage(isHovered()) * TEX_HEIGHT;

    ((ClickableWidget) (Object) this).drawTexture(matrixStack,
        x,
        y,
        u,
        v,
        width / 2,
        height / 2);
    ((ClickableWidget) (Object) this).drawTexture(matrixStack,
        x + width / 2,
        y,
        u + TEX_WIDTH - width / 2,
        v,
        width / 2,
        height / 2);
    ((ClickableWidget) (Object) this).drawTexture(matrixStack,
        x,
        y + height / 2,
        u,
        v + TEX_HEIGHT - height / 2,
        width / 2,
        height / 2);
    ((ClickableWidget) (Object) this).drawTexture(matrixStack,
        x + width / 2,
        y + height / 2,
        u + TEX_WIDTH - width / 2,
        v + TEX_HEIGHT - height / 2,
        width / 2,
        height / 2);

    renderBackground(matrixStack, client, mouseX, mouseY);

    int color = active ? 0xFFFFFF : 0xA0A0A0;
    DrawableHelper.drawCenteredText(
        matrixStack,
        textRenderer,
        ((ClickableWidget) (Object) this).getMessage(),
        x + width / 2,
        y + (height - 8) / 2,
        color | MathHelper.ceil(alpha * 255) << 24);
  }
}
