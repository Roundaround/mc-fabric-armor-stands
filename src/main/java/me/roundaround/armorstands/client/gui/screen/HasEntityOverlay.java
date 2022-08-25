package me.roundaround.armorstands.client.gui.screen;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public interface HasEntityOverlay {
  public void renderEntityOverlay(
      LivingEntity entity,
      MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider,
      int light);
}
