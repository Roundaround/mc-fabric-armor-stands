package me.roundaround.armorstands.client.gui.screen;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;

public interface HasArmorStandOverlay {
  default public void renderArmorStandOverlay(
      ArmorStandEntity entity,
      float tickDelta,
      MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider,
      int light) {
  }
}
