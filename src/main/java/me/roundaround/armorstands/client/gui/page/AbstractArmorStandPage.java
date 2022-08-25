package me.roundaround.armorstands.client.gui.page;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public abstract class AbstractArmorStandPage extends DrawableHelper {
  protected final MinecraftClient client;
  protected final ArmorStandScreen screen;

  protected AbstractArmorStandPage(MinecraftClient client, ArmorStandScreen screen) {
    this.client = client;
    this.screen = screen;
  }

  public boolean usesSlots() {
    return false;
  }

  public void init() {
  }

  public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
  }

  public void tick() {
  }
}
