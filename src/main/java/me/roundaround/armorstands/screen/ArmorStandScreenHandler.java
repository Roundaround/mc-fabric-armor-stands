package me.roundaround.armorstands.screen;

import com.mojang.datafixers.util.Pair;

import me.roundaround.armorstands.entity.ArmorStandInventory;
import me.roundaround.armorstands.network.ServerNetworking;
import me.roundaround.armorstands.util.HasArmorStand;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ArmorStandScreenHandler
    extends ScreenHandler
    implements HasArmorStand {
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

  private final PlayerInventory playerInventory;
  private final ArmorStandEntity armorStand;
  private final ArmorStandInventory inventory;

  public ArmorStandScreenHandler(int syncId, PlayerInventory playerInventory, ArmorStandEntity armorStand) {
    super(null, syncId);

    this.playerInventory = playerInventory;
    this.armorStand = armorStand;
    this.inventory = new ArmorStandInventory(armorStand);

    this.inventory.onOpen(this.playerInventory.player);

    for (int col = 0; col < 9; ++col) {
      addSlot(new Slot(this.playerInventory, col, 8 + col * 18, 142));
    }
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        addSlot(new Slot(this.playerInventory, col + (row + 1) * 9, 8 + col * 18, 84 + row * 18));
      }
    }

    for (int i = 0; i < 2; i++) {
      addSlot(new Slot(this.inventory, i, 116 + i * 18, 62) {
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
      addSlot(new Slot(this.inventory, 2 + 3 - i, 44, 8 + i * 18) {
        @Override
        public void setStack(ItemStack stack) {
          ArmorStandScreenHandler.this.armorStand.equipStack(equipmentSlot, stack);
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

  @Override
  public ArmorStandEntity getArmorStand() {
    return this.armorStand;
  }

  public PlayerInventory getPlayerInventory() {
    return this.playerInventory;
  }

  @Override
  public void sendContentUpdates() {
    if (this.playerInventory.player instanceof ServerPlayerEntity) {
      ServerNetworking.sendClientUpdatePacket((ServerPlayerEntity) this.playerInventory.player, this.armorStand);
    }

    super.sendContentUpdates();
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.inventory.canPlayerUse(player);
  }

  @Override
  public ItemStack transferSlot(PlayerEntity player, int index) {
    if (index < 0 || index >= this.slots.size()) {
      return ItemStack.EMPTY;
    }

    Slot slot = this.slots.get(index);

    if (slot == null || !slot.hasStack()) {
      return ItemStack.EMPTY;
    }

    // 0-8 is hotbar
    // 9-35 is main player inventory
    // 36-37 is hands
    // 38 is head
    // 39 is chest
    // 40 is legs
    // 41 is feet

    ItemStack stack = slot.getStack();
    ItemStack originalStack = stack.copy();

    if (index < PlayerInventory.MAIN_SIZE) {
      if (!tryTransferArmor(slot, stack)
          && !tryTransferToMainHand(stack)
          && !tryTransferToOffHand(stack)) {
        return ItemStack.EMPTY;
      }
    } else {
      if (!insertItem(stack, 0, PlayerInventory.MAIN_SIZE, false)) {
        return ItemStack.EMPTY;
      }
    }

    if (stack.isEmpty()) {
      slot.setStack(ItemStack.EMPTY);
    } else {
      slot.markDirty();
    }

    return originalStack;
  }

  @Override
  public void close(PlayerEntity player) {
    super.close(player);
    this.inventory.onClose(player);
  }

  private boolean tryTransferArmor(Slot source, ItemStack stack) {
    EquipmentSlot equipmentSlot = ArmorStandEntity.getPreferredEquipmentSlot(stack);

    if (equipmentSlot.getType() != EquipmentSlot.Type.ARMOR) {
      return false;
    }

    int targetIndex = this.slots.size() - 1 - equipmentSlot.getEntitySlotId();
    Slot slot = this.slots.get(targetIndex);
    ItemStack equipped = slot.getStack();
    slot.setStack(stack);
    source.setStack(equipped);

    return true;
  }

  private boolean tryTransferToMainHand(ItemStack stack) {
    int targetIndex = PlayerInventory.MAIN_SIZE;
    return insertItem(stack, targetIndex, targetIndex + 1, false);
  }

  private boolean tryTransferToOffHand(ItemStack stack) {
    int targetIndex = PlayerInventory.MAIN_SIZE + 1;
    return insertItem(stack, targetIndex, targetIndex + 1, false);
  }
}
