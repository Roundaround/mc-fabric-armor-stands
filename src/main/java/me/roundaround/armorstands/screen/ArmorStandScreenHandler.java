package me.roundaround.armorstands.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import me.roundaround.armorstands.entity.ArmorStandInventory;
import me.roundaround.armorstands.mixin.ArmorStandEntityAccessor;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.server.network.ServerNetworking;
import me.roundaround.armorstands.util.ArmorStandEditor;
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

import java.util.ArrayList;

public class ArmorStandScreenHandler extends ScreenHandler {
  private static final Identifier EMPTY_MAINHAND_ARMOR_SLOT = Identifier.ofVanilla("item/empty_slot_sword");

  private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{
      PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE,
      PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE
  };
  private static final Identifier[] EMPTY_HAND_SLOT_TEXTURES = new Identifier[]{
      EMPTY_MAINHAND_ARMOR_SLOT, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT
  };
  private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{
      EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
  };
  private static final EquipmentSlot[] HAND_SLOT_ORDER = new EquipmentSlot[]{
      EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
  };

  private final PlayerInventory playerInventory;
  private final ArmorStandEntity armorStand;
  private final ScreenType screenType;
  private final ArmorStandInventory inventory;
  private final ArmorStandEditor editor;
  private final ArrayList<Pair<Slot, EquipmentSlot>> armorSlots = new ArrayList<>();

  public ArmorStandScreenHandler(
      int syncId, PlayerInventory playerInventory, ArmorStandEntity armorStand, ScreenType screenType
  ) {
    super(null, syncId);

    this.playerInventory = playerInventory;
    this.armorStand = armorStand;
    this.screenType = screenType;

    this.inventory = new ArmorStandInventory(armorStand);

    if (playerInventory.player instanceof ServerPlayerEntity) {
      this.editor = ArmorStandEditor.get((ServerPlayerEntity) playerInventory.player, armorStand);
    } else {
      this.editor = null;
    }

    this.inventory.onOpen(this.playerInventory.player);

    if (this.screenType != null && this.screenType.usesInventory()) {
      initSlots();
    }
  }

  private void initSlots() {
    for (int col = 0; col < 9; ++col) {
      addSlot(new Slot(this.playerInventory, col, 8 + col * 18, 142));
    }
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        addSlot(new Slot(this.playerInventory, col + (row + 1) * 9, 8 + col * 18, 84 + row * 18));
      }
    }

    for (int i = 0; i < 2; i++) {
      final int index = i;
      final EquipmentSlot equipmentSlot = HAND_SLOT_ORDER[i];

      Slot slot = addSlot(new Slot(this.inventory, index, 116, 44 + index * 18) {
        @Override
        public void setStack(ItemStack stack) {
          ArmorStandScreenHandler.this.armorStand.equipStack(equipmentSlot, stack);
          markDirty();
        }

        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
          return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_HAND_SLOT_TEXTURES[index]);
        }

        @Override
        public boolean canTakeItems(PlayerEntity player) {
          if (!ArmorStandScreenHandler.this.playerInventory.player.isCreative() &&
              isSlotDisabled(armorStand, equipmentSlot)) {
            return false;
          }
          return super.canTakeItems(player);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
          if (!ArmorStandScreenHandler.this.playerInventory.player.isCreative() &&
              isSlotDisabled(armorStand, equipmentSlot)) {
            return false;
          }
          return super.canInsert(stack);
        }
      });

      armorSlots.add(Pair.of(slot, equipmentSlot));
    }

    for (int i = 0; i < 4; i++) {
      final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];

      Slot slot = addSlot(new Slot(this.inventory, 2 + 3 - i, 44, 8 + i * 18) {
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
        public boolean canTakeItems(PlayerEntity player) {
          if (!ArmorStandScreenHandler.this.playerInventory.player.isCreative() &&
              isSlotDisabled(armorStand, equipmentSlot)) {
            return false;
          }
          return super.canTakeItems(player);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
          if (!ArmorStandScreenHandler.this.playerInventory.player.isCreative() &&
              isSlotDisabled(armorStand, equipmentSlot)) {
            return false;
          }
          return equipmentSlot == armorStand.getPreferredEquipmentSlot(stack);
        }

        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
          return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
              EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]
          );
        }
      });

      armorSlots.add(Pair.of(slot, equipmentSlot));
    }
  }

  public ImmutableList<Pair<Slot, EquipmentSlot>> getArmorSlots() {
    return ImmutableList.copyOf(this.armorSlots);
  }

  public ScreenType getScreenType() {
    return this.screenType;
  }

  public ArmorStandEntity getArmorStand() {
    return this.armorStand;
  }

  public ArmorStandEditor getEditor() {
    return this.editor;
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
  public ItemStack quickMove(PlayerEntity player, int index) {
    if (index < 0 || index >= this.slots.size()) {
      return ItemStack.EMPTY;
    }

    Slot slot = this.slots.get(index);
    if (!slot.hasStack()) {
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
      if (!tryTransferArmor(stack) && !tryTransferToMainHand(stack) && !tryTransferToOffHand(stack)) {
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

  private boolean tryTransferArmor(ItemStack stack) {
    EquipmentSlot equipmentSlot = this.armorStand.getPreferredEquipmentSlot(stack);
    if (isSlotDisabled(this.armorStand, equipmentSlot)) {
      return false;
    }

    int targetIndex = this.slots.size() - 1 - equipmentSlot.getEntitySlotId();
    return insertItem(stack, targetIndex, targetIndex + 1, false);
  }

  private boolean tryTransferToMainHand(ItemStack stack) {
    if (isSlotDisabled(this.armorStand, EquipmentSlot.MAINHAND)) {
      return false;
    }

    int targetIndex = PlayerInventory.MAIN_SIZE;
    return insertItem(stack, targetIndex, targetIndex + 1, false);
  }

  private boolean tryTransferToOffHand(ItemStack stack) {
    if (isSlotDisabled(this.armorStand, EquipmentSlot.OFFHAND)) {
      return false;
    }

    int targetIndex = PlayerInventory.MAIN_SIZE + 1;
    return insertItem(stack, targetIndex, targetIndex + 1, false);
  }

  public static boolean isSlotDisabled(ArmorStandEntity armorStand, EquipmentSlot slot) {
    return (((ArmorStandEntityAccessor) armorStand).getDisabledSlots() & 1 << slot.getArmorStandSlotId()) != 0;
  }
}
