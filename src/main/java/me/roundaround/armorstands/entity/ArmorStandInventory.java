package me.roundaround.armorstands.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class ArmorStandInventory implements Inventory {
  public static final Int2ObjectMap<EquipmentSlot> EQUIPMENT_SLOTS = new Int2ObjectArrayMap<>(Map.of(
      EquipmentSlot.MAINHAND.getEntitySlotId(),
      EquipmentSlot.MAINHAND,
      EquipmentSlot.OFFHAND.getEntitySlotId(),
      EquipmentSlot.OFFHAND,
      EquipmentSlot.FEET.getOffsetEntitySlotId(2),
      EquipmentSlot.FEET,
      EquipmentSlot.LEGS.getOffsetEntitySlotId(2),
      EquipmentSlot.LEGS,
      EquipmentSlot.CHEST.getOffsetEntitySlotId(2),
      EquipmentSlot.CHEST,
      EquipmentSlot.HEAD.getOffsetEntitySlotId(2),
      EquipmentSlot.HEAD
  ));

  public final ArmorStandEntity armorStand;

  private final EntityEquipment equipment;

  public ArmorStandInventory(ArmorStandEntity armorStand, EntityEquipment equipment) {
    this.armorStand = armorStand;
    this.equipment = equipment;
  }

  @Override
  public int size() {
    return EQUIPMENT_SLOTS.size();
  }

  @Override
  public void clear() {
    this.equipment.clear();
  }

  @Override
  public boolean isEmpty() {
    for (EquipmentSlot equipmentSlot : EQUIPMENT_SLOTS.values()) {
      if (!this.equipment.get(equipmentSlot).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getStack(int slot) {
    EquipmentSlot equipmentSlot = EQUIPMENT_SLOTS.get(slot);
    return equipmentSlot != null ? this.equipment.get(equipmentSlot) : ItemStack.EMPTY;
  }

  @Override
  public void setStack(int slot, ItemStack stack) {
    EquipmentSlot equipmentSlot = EQUIPMENT_SLOTS.get(slot);
    if (equipmentSlot != null) {
      this.equipment.put(equipmentSlot, stack);
    }
  }

  @Override
  public ItemStack removeStack(int slot) {
    EquipmentSlot equipmentSlot = EQUIPMENT_SLOTS.get(slot);
    return equipmentSlot != null ? this.equipment.put(equipmentSlot, ItemStack.EMPTY) : ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeStack(int slot, int amount) {
    EquipmentSlot equipmentSlot = EQUIPMENT_SLOTS.get(slot);
    if (equipmentSlot != null) {
      ItemStack itemStack = this.equipment.get(equipmentSlot);
      if (!itemStack.isEmpty()) {
        return itemStack.split(amount);
      }
    }

    return ItemStack.EMPTY;
  }

  @Override
  public void markDirty() {
  }

  @Override
  public boolean canPlayerUse(PlayerEntity player) {
    return player.squaredDistanceTo(this.armorStand) <= 64;
  }
}
