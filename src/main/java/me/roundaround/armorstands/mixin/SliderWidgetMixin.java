package me.roundaround.armorstands.mixin;

import net.minecraft.client.gui.widget.SliderWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SliderWidget.class)
public abstract class SliderWidgetMixin {
  @ModifyArg(
      method = "renderButton", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/gui/widget/SliderWidget;drawNineSlicedTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIIIIIII)V",
      ordinal = 1
  ), index = 4
  )
  private int modifyHeightArg(int height) {
    return ((SliderWidget) (Object) this).getHeight();
  }
}
