package me.roundaround.armorstands.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.widget.FlagToggleWidget;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class ArmorStandInventoryScreen extends AbstractArmorStandScreen {
  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 166;
  private static final Identifier CUSTOM_TEXTURE =
      new Identifier(ArmorStandsMod.MOD_ID, "textures/gui/container/inventory.png");

  private float mouseX;
  private float mouseY;
  private FlagToggleWidget showArmsToggle;
  private FlagToggleWidget lockInventoryToggle;

  public ArmorStandInventoryScreen(ArmorStandScreenHandler handler) {
    super(handler, ScreenType.INVENTORY.getDisplayName());

    this.utilizesInventory = true;
    this.passEvents = false;
  }

  @Override
  public ScreenType getScreenType() {
    return ScreenType.INVENTORY;
  }

  @Override
  protected void initRight() {
    this.showArmsToggle = addDrawableChild(new FlagToggleWidget(this.textRenderer,
        ArmorStandFlag.SHOW_ARMS,
        ArmorStandFlag.SHOW_ARMS.getValue(this.armorStand),
        this.width - SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - 2 * FlagToggleWidget.WIDGET_HEIGHT - BETWEEN_PAD));
    this.lockInventoryToggle = addDrawableChild(new FlagToggleWidget(this.textRenderer,
        ArmorStandFlag.LOCK_INVENTORY,
        ArmorStandFlag.LOCK_INVENTORY.getValue(this.armorStand),
        this.width - SCREEN_EDGE_PAD,
        this.height - SCREEN_EDGE_PAD - FlagToggleWidget.WIDGET_HEIGHT));
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    this.showArmsToggle.setValue(ArmorStandFlag.SHOW_ARMS.getValue(this.armorStand));
    this.lockInventoryToggle.setValue(ArmorStandFlag.LOCK_INVENTORY.getValue(this.armorStand));
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    renderBackground(drawContext, mouseX, mouseY, delta);

    this.mouseX = mouseX;
    this.mouseY = mouseY;
    super.render(drawContext, mouseX, mouseY, delta);

    drawMouseoverTooltip(drawContext, mouseX, mouseY);
  }

  @Override
  protected void drawBackground(DrawContext drawContext, float delta, int mouseX, int mouseY) {
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    drawContext.drawTexture(CUSTOM_TEXTURE,
        this.x,
        this.y,
        0,
        0,
        BACKGROUND_WIDTH,
        BACKGROUND_HEIGHT);

    ImmutableList<Pair<Slot, EquipmentSlot>> armorSlots = this.handler.getArmorSlots();
    for (int index = 0; index < armorSlots.size(); index++) {
      Slot slot = armorSlots.get(index).getFirst();
      EquipmentSlot equipmentSlot = armorSlots.get(index).getSecond();
      if (ArmorStandScreenHandler.isSlotDisabled(armorStand, equipmentSlot)) {
        drawContext.fill(x + slot.x,
            this.y + slot.y,
            this.x + slot.x + 16,
            y + slot.y + 16,
            0x80000000);
      }
    }

    InventoryScreen.drawEntity(drawContext,
        this.x + 26,
        this.y + 8,
        this.x + 75,
        this.y + 75,
        30,
        0.0625f,
        this.x + 45 - this.mouseX,
        this.y + 40 - this.mouseY,
        this.armorStand);
  }
}
