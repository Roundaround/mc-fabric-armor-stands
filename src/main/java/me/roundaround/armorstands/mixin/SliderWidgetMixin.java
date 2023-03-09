package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Mixin(SliderWidget.class)
public abstract class SliderWidgetMixin {
  private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
  private static final int TEX_WIDTH = 200;
  private static final int TEX_HEIGHT = 20;

  @Shadow
  protected double value;

  @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
  protected void renderBackground(
      MatrixStack matrixStack,
      MinecraftClient client,
      int mouseX,
      int mouseY,
      CallbackInfo info) {
    int width = ((SliderWidget) (Object) this).getWidth();
    int height = ((SliderWidget) (Object) this).getHeight();

    if (width >= TEX_WIDTH && height >= TEX_HEIGHT) {
      return;
    }

    info.cancel();

    int x = ((SliderWidget) (Object) this).x;
    int y = ((SliderWidget) (Object) this).y;

    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    int v = 46 + (((SliderWidget) (Object) this).isHovered() ? 2 : 1) * 20;

    ((SliderWidget) (Object) this).drawTexture(
        matrixStack,
        x + (int) (this.value * (width - 8)),
        y,
        0,
        v,
        4,
        height / 2);
    ((SliderWidget) (Object) this).drawTexture(
        matrixStack,
        x + (int) (this.value * (width - 8)) + 4,
        y,
        196,
        v,
        4,
        height / 2);
    ((SliderWidget) (Object) this).drawTexture(
        matrixStack,
        x + (int) (this.value * (width - 8)),
        y + height / 2,
        0,
        v + 20 - height / 2,
        4,
        height / 2);
    ((SliderWidget) (Object) this).drawTexture(
        matrixStack,
        x + (int) (this.value * (width - 8)) + 4,
        y + height / 2,
        196,
        v + 20 - height / 2,
        4,
        height / 2);
  }
}
