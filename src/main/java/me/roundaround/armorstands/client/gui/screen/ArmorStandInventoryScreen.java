package me.roundaround.armorstands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.ArmorStandsClientMod;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ArmorStandInventoryScreen extends AbstractArmorStandScreen {
  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 166;
  private static final int CUSTOM_TEXTURE_HEIGHT = 80;
  private static final Identifier INVENTORY_TEXTURE = new Identifier(
      Identifier.DEFAULT_NAMESPACE,
      "textures/gui/container/inventory.png");
  private static final Identifier CUSTOM_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/container/inventory.png");
  private static final Identifier CUSTOM_TEXTURE_DARK = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/container/inventory_dark.png");

  protected int x;
  protected int y;

  public ArmorStandInventoryScreen(ArmorStandEntity armorStand, int index) {
    super(armorStand, index, Text.literal(""));
  }

  @Override
  protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    int x = (width - BACKGROUND_WIDTH) / 2;
    int y = (height - BACKGROUND_HEIGHT) / 2;

    RenderSystem.setShaderTexture(0,
        ArmorStandsClientMod.isDarkModeEnabled()
            ? CUSTOM_TEXTURE_DARK
            : CUSTOM_TEXTURE);
    drawTexture(
        matrixStack,
        x,
        y,
        0,
        0,
        BACKGROUND_WIDTH,
        CUSTOM_TEXTURE_HEIGHT);

    RenderSystem.setShaderTexture(0, INVENTORY_TEXTURE);
    drawTexture(
        matrixStack,
        x,
        y + CUSTOM_TEXTURE_HEIGHT,
        0,
        CUSTOM_TEXTURE_HEIGHT,
        BACKGROUND_WIDTH,
        BACKGROUND_HEIGHT - CUSTOM_TEXTURE_HEIGHT);
  }
}
