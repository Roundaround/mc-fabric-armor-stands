package me.roundaround.armorstands.client.gui.page;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ArmorStandInventoryPage extends AbstractArmorStandPage {
  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 166;
  private static final Identifier CUSTOM_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/container/inventory.png");
  private static final Identifier CUSTOM_TEXTURE_DARK = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/container/inventory_dark.png");

  public ArmorStandInventoryPage(ArmorStandScreen screen) {
    super(screen);
  }

  @Override
  public boolean usesSlots() {
    return true;
  }

  @Override
  public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    int x = (screen.width - BACKGROUND_WIDTH) / 2;
    int y = (screen.height - BACKGROUND_HEIGHT) / 2;

    RenderSystem.setShaderTexture(0,
        ArmorStandsClientMod.darkModeDetected
            ? CUSTOM_TEXTURE_DARK
            : CUSTOM_TEXTURE);
    drawTexture(
        matrixStack,
        x,
        y,
        0,
        0,
        BACKGROUND_WIDTH,
        BACKGROUND_HEIGHT);

    InventoryScreen.drawEntity(
        x + 87,
        y + 75,
        30,
        0f,
        0f,
        screen.getArmorStand());
  }
}
