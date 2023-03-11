package me.roundaround.armorstands.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.client.gui.widget.ArmorStandFlagToggleWidget;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ArmorStandInventoryScreen extends AbstractArmorStandScreen {
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
  private ArmorStandFlagToggleWidget toggle;

  public ArmorStandInventoryScreen(ArmorStandScreenHandler handler, ArmorStandEntity armorStand) {
    super(handler, ScreenType.INVENTORY.getDisplayName(), armorStand);

    this.utilizesInventory = true;
    this.passEvents = false;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.INVENTORY;
  }

  @Override
  protected void initRight() {
    this.toggle = addDrawableChild(
        new ArmorStandFlagToggleWidget(
            this.textRenderer,
            ArmorStandFlag.LOCK_INVENTORY,
            ArmorStandFlag.LOCK_INVENTORY.getValue(this.armorStand),
            this.width - SCREEN_EDGE_PAD,
            this.height - SCREEN_EDGE_PAD - ArmorStandFlagToggleWidget.WIDGET_HEIGHT));
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    this.toggle.setValue(ArmorStandFlag.LOCK_INVENTORY.getValue(this.armorStand));
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    renderBackground(matrixStack);

    this.mouseX = mouseX;
    this.mouseY = mouseY;
    super.render(matrixStack, mouseX, mouseY, delta);

    // If game is paused, render the vanilla pause text at the top
    if (this.client.isPaused()) {
      drawCenteredText(
          matrixStack,
          this.textRenderer,
          Text.translatable("menu.paused"),
          this.width / 2,
          10,
          0xFFFFFF);
    }

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

    ImmutableList<Pair<Slot, EquipmentSlot>> armorSlots = ((ArmorStandScreenHandler) this.handler).getArmorSlots();
    for (int index = 0; index < armorSlots.size(); index++) {
      Slot slot = armorSlots.get(index).getFirst();
      EquipmentSlot equipmentSlot = armorSlots.get(index).getSecond();
      if (ArmorStandScreenHandler.isSlotDisabled(armorStand, equipmentSlot)) {
        fill(matrixStack, x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0x80000000);
      }
    }

    InventoryScreen.drawEntity(
        x + 88,
        y + 75,
        30,
        x + 88 - this.mouseX,
        y + 40 - this.mouseY,
        this.armorStand);
  }
}
