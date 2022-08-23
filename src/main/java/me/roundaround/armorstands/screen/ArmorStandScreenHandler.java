package me.roundaround.armorstands.screen;

import com.mojang.datafixers.util.Pair;

import me.roundaround.armorstands.entity.ArmorStandInventory;
import me.roundaround.armorstands.mixin.ScreenHandlerAccessor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class ArmorStandScreenHandler extends ScreenHandler {
  private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[] {
      PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE,
      PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE,
      PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE,
      PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE };
  private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[] {
      EquipmentSlot.HEAD,
      EquipmentSlot.CHEST,
      EquipmentSlot.LEGS,
      EquipmentSlot.FEET };

  public ArmorStandEntity armorStand;

  private PlayerInventory playerInventory;
  private ArmorStandInventory inventory;

  public ArmorStandScreenHandler(int syncId, PlayerInventory playerInventory, ArmorStandEntity armorStand) {
    super(null, syncId);

    this.armorStand = armorStand;
    this.playerInventory = playerInventory;
    inventory = new ArmorStandInventory(armorStand);
  }

  @Override
  public ItemStack transferSlot(PlayerEntity player, int index) {
    if (index < 0 || index >= slots.size()) {
      return ItemStack.EMPTY;
    }

    Slot slot = slots.get(index);

    if (slot == null || !slot.hasStack()) {
      return ItemStack.EMPTY;
    }

    // 0-8 is hotbar
    // 9-35 is main player inventory
    // 36-39 is equipment
    // 40-41 is hands

    ItemStack stack = slot.getStack();
    ItemStack originalStack = stack.copy();

    if (index < PlayerInventory.MAIN_SIZE) {
      EquipmentSlot equipmentSlot = ArmorStandEntity.getPreferredEquipmentSlot(stack);

      if (equipmentSlot.getType().equals(EquipmentSlot.Type.ARMOR)) {
        
      }
      
      return ItemStack.EMPTY;
    }

    return ItemStack.EMPTY;

    ItemStack itemStack = ItemStack.EMPTY;
    Slot slot = (Slot) slots.get(index);
    if (slot != null && slot.hasStack()) {
      int i;
      ItemStack itemStack2 = slot.getStack();
      itemStack = itemStack2.copy();
      EquipmentSlot equipmentSlot = ArmorStandEntity.getPreferredEquipmentSlot(itemStack);
      if (index == 0) {
        if (!this.insertItem(itemStack2, 9, 45, true)) {
          return ItemStack.EMPTY;
        }
        slot.onQuickTransfer(itemStack2, itemStack);
      } else if (index >= 1 && index < 5 ? !this.insertItem(itemStack2, 9, 45, false)
          : (index >= 5 && index < 9 ? !this.insertItem(itemStack2, 9, 45, false)
              : (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR
                  && !((Slot) this.slots.get(8 - equipmentSlot.getEntitySlotId())).hasStack()
                      ? !this.insertItem(itemStack2, i = 8 - equipmentSlot.getEntitySlotId(), i + 1, false)
                      : (equipmentSlot == EquipmentSlot.OFFHAND && !((Slot) this.slots.get(45)).hasStack()
                          ? !this.insertItem(itemStack2, 45, 46, false)
                          : (index >= 9 && index < 36 ? !this.insertItem(itemStack2, 36, 45, false)
                              : (index >= 36 && index < 45 ? !this.insertItem(itemStack2, 9, 36, false)
                                  : !this.insertItem(itemStack2, 9, 45, false))))))) {
        return ItemStack.EMPTY;
      }
      if (itemStack2.isEmpty()) {
        slot.setStack(ItemStack.EMPTY);
      } else {
        slot.markDirty();
      }
      if (itemStack2.getCount() == itemStack.getCount()) {
        return ItemStack.EMPTY;
      }
      slot.onTakeItem(player, itemStack2);
      if (index == 0) {
        player.dropItem(itemStack2, false);
      }
    }
    return itemStack;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return inventory.canPlayerUse(player);
  }

  public void populateSlots(boolean fillSlots) {
    slots.clear();
    ((ScreenHandlerAccessor) this).getTrackedStacks().clear();
    ((ScreenHandlerAccessor) this).getPreviousTrackedStacks().clear();

    if (!fillSlots) {
      return;
    }

    for (int col = 0; col < 9; ++col) {
      addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
    }
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        addSlot(new Slot(playerInventory, col + (row + 1) * 9, 8 + col * 18, 84 + row * 18));
      }
    }

    for (int i = 0; i < 2; i++) {
      addSlot(new Slot(inventory, i, 116 + i * 18, 62) {
        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
          return Pair.of(
              PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
              PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
        }
      });
    }

    for (int i = 0; i < 4; i++) {
      final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
      addSlot(new Slot(inventory, 2 + 3 - i, 44, 8 + i * 18) {
        @Override
        public void setStack(ItemStack stack) {
          armorStand.equipStack(equipmentSlot, stack);
          markDirty();
        }

        @Override
        public int getMaxItemCount() {
          return 1;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
          return equipmentSlot == ArmorStandEntity.getPreferredEquipmentSlot(stack);
        }

        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
          return Pair.of(
              PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
              EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
        }
      });
    }
  }
}
