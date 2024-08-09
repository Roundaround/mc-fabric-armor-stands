package me.roundaround.armorstands.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.roundalib.client.gui.widget.ToggleWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class ArmorStandInventoryScreen extends AbstractArmorStandScreen {
  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 166;
  private static final int TOGGLE_HEIGHT = 16;
  private static final Identifier CUSTOM_TEXTURE = Identifier.of(
      ArmorStandsMod.MOD_ID, "textures/gui/container/inventory.png");

  private float prevMouseX;
  private float prevMouseY;
  private ToggleWidget showArmsToggle;
  private ToggleWidget lockInventoryToggle;

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
  protected void populateLayout() {
    super.populateLayout();

    this.showArmsToggle = this.layout.bottomRight.add(this.createToggleWidget(ArmorStandFlag.SHOW_ARMS));
    this.lockInventoryToggle = this.layout.bottomRight.add(this.createToggleWidget(ArmorStandFlag.LOCK_INVENTORY));
  }

  private ToggleWidget createToggleWidget(ArmorStandFlag flag) {
    return ToggleWidget.yesNoBuilder(this.textRenderer, (value) -> flag.getDisplayName())
        .initially(this.getFlagValue(flag))
        .onPress((toggle) -> ClientNetworking.sendSetFlagPacket(flag, !this.getFlagValue(flag)))
        .matchTooltipToLabel()
        .setHeight(TOGGLE_HEIGHT)
        .labelBgColor(BACKGROUND_COLOR)
        .build();
  }

  private boolean getFlagValue(ArmorStandFlag flag) {
    return flag.getValue(this.getArmorStand());
  }

  @Override
  protected void initTabNavigation() {
    this.x = (this.width - BACKGROUND_WIDTH) / 2;
    this.y = (this.height - BACKGROUND_HEIGHT) / 2;
    super.initTabNavigation();
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    this.drawMouseoverTooltip(context, mouseX, mouseY);
    this.prevMouseX = mouseX;
    this.prevMouseY = mouseY;
  }

  @Override
  protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    context.drawTexture(CUSTOM_TEXTURE, this.x, this.y, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

    ImmutableList<Pair<Slot, EquipmentSlot>> armorSlots = this.handler.getArmorSlots();
    for (int index = 0; index < armorSlots.size(); index++) {
      Slot slot = armorSlots.get(index).getFirst();
      EquipmentSlot equipmentSlot = armorSlots.get(index).getSecond();
      if (ArmorStandScreenHandler.isSlotDisabled(armorStand, equipmentSlot)) {
        context.fill(x + slot.x, this.y + slot.y, this.x + slot.x + 16, y + slot.y + 16, 0x80000000);
      }
    }

    InventoryScreen.drawEntity(context, this.x + 62, this.y + 8, this.x + 114, this.y + 78, 30, 0.0625f,
        this.prevMouseX, this.prevMouseY, this.getArmorStand()
    );
  }

  @Override
  public void handledScreenTick() {
    super.handledScreenTick();

    this.showArmsToggle.setValue(ArmorStandFlag.SHOW_ARMS.getValue(this.getArmorStand()));
    this.lockInventoryToggle.setValue(ArmorStandFlag.LOCK_INVENTORY.getValue(this.getArmorStand()));
  }
}
