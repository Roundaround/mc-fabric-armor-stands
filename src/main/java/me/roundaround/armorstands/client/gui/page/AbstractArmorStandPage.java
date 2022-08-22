package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public abstract class AbstractArmorStandPage extends DrawableHelper {
  protected final ArmorStandScreen screen;

  protected AbstractArmorStandPage(ArmorStandScreen screen) {
    this.screen = screen;
  }

  public void init() {
  }

  public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
  }

  public void tick() {
  }
}
