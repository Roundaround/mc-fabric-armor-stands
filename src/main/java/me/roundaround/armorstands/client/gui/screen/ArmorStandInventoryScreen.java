package me.roundaround.armorstands.client.gui.screen;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.util.LastUsedScreen.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ArmorStandInventoryScreen
    extends AbstractArmorStandScreen {
  public static final Text TITLE = Text.translatable("armorstands.screen.inventory");
  public static final int U_INDEX = 5;

  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 166;
  private static final Identifier CUSTOM_TEXTURE = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/container/inventory.png");
  private static final Identifier CUSTOM_TEXTURE_DARK = new Identifier(
      ArmorStandsMod.MOD_ID,
      "textures/gui/container/inventory_dark.png");

  private float mouseX;
  private float mouseY;

  public ArmorStandInventoryScreen(
      ArmorStandScreenHandler handler,
      ArmorStandEntity armorStand) {
    super(handler, TITLE, armorStand);

    this.utilizesInventory = true;
    this.passEvents = false;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.INVENTORY;
  }

  @Override
  public ScreenConstructor<?> getNextScreen() {
    return ArmorStandUtilitiesScreen::new;
  }

  @Override
  public ScreenConstructor<?> getPreviousScreen() {
    return ArmorStandPoseScreen::new;
  }

  @Override
  public boolean shouldPause() {
    return true;
  }

  @Override
  public void init() {
    super.init();
    initNavigationButtons(List.of(
        ScreenFactory.create(
            ArmorStandUtilitiesScreen.TITLE,
            ArmorStandUtilitiesScreen.U_INDEX,
            ArmorStandUtilitiesScreen::new),
        ScreenFactory.create(
            ArmorStandMoveScreen.TITLE,
            ArmorStandMoveScreen.U_INDEX,
            ArmorStandMoveScreen::new),
        ScreenFactory.create(
            ArmorStandRotateScreen.TITLE,
            ArmorStandRotateScreen.U_INDEX,
            ArmorStandRotateScreen::new),
        ScreenFactory.create(
            ArmorStandPresetsScreen.TITLE,
            ArmorStandPresetsScreen.U_INDEX,
            ArmorStandPresetsScreen::new),
        ScreenFactory.create(
            ArmorStandPoseScreen.TITLE,
            ArmorStandPoseScreen.U_INDEX,
            ArmorStandPoseScreen::new),
        ScreenFactory.create(
            ArmorStandInventoryScreen.TITLE,
            ArmorStandInventoryScreen.U_INDEX)));
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    renderBackground(matrixStack);
    this.mouseX = mouseX;
    this.mouseY = mouseY;
    super.render(matrixStack, mouseX, mouseY, delta);
    drawMouseoverTooltip(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    int x = (this.width - BACKGROUND_WIDTH) / 2;
    int y = (this.height - BACKGROUND_HEIGHT) / 2;

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
        x + 88,
        y + 75,
        30,
        x + 88 - this.mouseX,
        y + 40 - this.mouseY,
        this.armorStand);
  }
}
