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
  public ItemStack transferSlot(PlayerEntity player, int slot) {
    return ItemStack.EMPTY;
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
