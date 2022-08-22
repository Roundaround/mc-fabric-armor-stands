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



    heldItems = ((ArmorStandEntityAccessor) armorStand).getHeldItems();
    armorItems = ((ArmorStandEntityAccessor) armorStand).getArmorItems();
    fullInventory = List.of(heldItems, armorItems);
  }

  @Override
  public int size() {
    return heldItems.size() + armorItems.size();
  }

  @Override
  public void clear() {
    fullInventory.forEach(DefaultedList::clear);
    markDirty();
  }

  @Override
  public boolean isEmpty() {
    return fullInventory
        .stream()
        .allMatch((items) -> items.stream().allMatch(ItemStack::isEmpty));
  }

  @Override
  public ItemStack getStack(int slot) {
    for (DefaultedList<ItemStack> items : fullInventory) {
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
    for (DefaultedList<ItemStack> items : fullInventory) {
      if (slot < 0) {
        return;
      } else if (slot >= items.size()) {
        slot -= items.size();
      } else {
        items.set(slot, stack);
        markDirty();
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
    for (DefaultedList<ItemStack> items : fullInventory) {
      if (slot < 0) {
        return ItemStack.EMPTY;
      } else if (slot >= items.size()) {
        slot -= items.size();
      } else if (items.get(slot).isEmpty()) {
        return ItemStack.EMPTY;
      } else {
        ItemStack stack = Inventories.splitStack(items, slot, amount);
        if (!stack.isEmpty()) {
          markDirty();
        }
        return stack;
      }
    }

    return ItemStack.EMPTY;
  }

  @Override
  public void markDirty() {
    // TODO: Update necessary bits
  }

  @Override
  public boolean canPlayerUse(PlayerEntity player) {
    return player.squaredDistanceTo(armorStand) <= 64;
  }
}
