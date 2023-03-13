package me.roundaround.armorstands.entity;

import java.util.List;

import me.roundaround.armorstands.mixin.ArmorStandEntityAccessor;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ArmorStandInventory implements Inventory {
  public final ArmorStandEntity armorStand;

  private final DefaultedList<ItemStack> heldItems;
  private final DefaultedList<ItemStack> armorItems;
  private final List<DefaultedList<ItemStack>> fullInventory;

  public ArmorStandInventory(ArmorStandEntity armorStand) {
    this.armorStand = armorStand;

    if (this.armorStand == null) {
      this.heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
      this.armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
    } else {
      this.heldItems = ((ArmorStandEntityAccessor) armorStand).getHeldItems();
      this.armorItems = ((ArmorStandEntityAccessor) armorStand).getArmorItems();
    }

    this.fullInventory = List.of(heldItems, armorItems);
  }

  @Override
  public int size() {
    return this.heldItems.size() + this.armorItems.size();
  }

  @Override
  public void clear() {
    this.fullInventory.forEach(DefaultedList::clear);
  }

  @Override
  public boolean isEmpty() {
    return this.fullInventory
        .stream()
        .allMatch((items) -> items.stream().allMatch(ItemStack::isEmpty));
  }

  @Override
  public ItemStack getStack(int slot) {
    for (DefaultedList<ItemStack> items : this.fullInventory) {
      if (slot < 0) {
        return ItemStack.EMPTY;
      } else if (slot >= items.size()) {
        slot -= items.size();
      } else {
        return items.get(slot);
      }
    }

    return ItemStack.EMPTY;
  }

  @Override
  public void setStack(int slot, ItemStack stack) {
    for (DefaultedList<ItemStack> items : this.fullInventory) {
      if (slot < 0) {
        return;
      } else if (slot >= items.size()) {
        slot -= items.size();
      } else {
        items.set(slot, stack);
        return;
      }
    }
  }

  @Override
  public ItemStack removeStack(int slot) {
    for (DefaultedList<ItemStack> items : fullInventory) {
      if (slot < 0) {
        return ItemStack.EMPTY;
      } else if (slot >= items.size()) {
        slot -= items.size();
      } else {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        return stack;
      }
    }

    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeStack(int slot, int amount) {
    for (DefaultedList<ItemStack> items : this.fullInventory) {
      if (slot < 0) {
        return ItemStack.EMPTY;
      } else if (slot >= items.size()) {
        slot -= items.size();
      } else if (items.get(slot).isEmpty()) {
        return ItemStack.EMPTY;
      } else {
        return Inventories.splitStack(items, slot, amount);
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
