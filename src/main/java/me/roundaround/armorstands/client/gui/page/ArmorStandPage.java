package me.roundaround.armorstands.client.gui.page;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public interface ArmorStandPage {
  Text getTitle();

  int getTextureU();

  default boolean usesSlots() {
    return false;
  }

  default void preInit() {
  }

  default void postInit() {
  }

  default void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
  }

  default void renderEntityOverlay(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
  }

  default void tick() {
  }
}
