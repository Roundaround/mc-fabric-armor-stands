package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.HasArmorStandOverlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public interface ArmorStandPage extends HasArmorStandOverlay {
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

  default void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
  }

  default void tick() {
  }
}
