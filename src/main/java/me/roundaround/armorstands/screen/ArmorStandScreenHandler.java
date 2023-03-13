package me.roundaround.armorstands.screen;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.entity.ArmorStandInventory;
import me.roundaround.armorstands.mixin.ArmorStandEntityAccessor;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.packet.s2c.ClientUpdatePacket;
import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.HasArmorStand;
import me.roundaround.armorstands.util.HasArmorStandEditor;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ArmorStandScreenHandler
    extends ScreenHandler
    implements HasArmorStand, HasArmorStandEditor {
  public static final Identifier EMPTY_MAINHAND_ARMOR_SLOT = new Identifier(
      ArmorStandsMod.MOD_ID,
      "item/empty_armor_slot_sword");

  private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[] {
      PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE,
      PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE,
      PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE,
      PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE };
  private static final Identifier[] EMPTY_HAND_SLOT_TEXTURES = new Identifier[] {
      EMPTY_MAINHAND_ARMOR_SLOT,
      PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT };
  private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[] {
      EquipmentSlot.HEAD,
      EquipmentSlot.CHEST,
      EquipmentSlot.LEGS,
      EquipmentSlot.FEET };
  private static final EquipmentSlot[] HAND_SLOT_ORDER = new EquipmentSlot[] {
      EquipmentSlot.MAINHAND,
      EquipmentSlot.OFFHAND };

  private final PlayerInventory playerInventory;
  private final ArmorStandEntity armorStand;
  private final ScreenType screenType;
  private final ArmorStandInventory inventory;
  private final ArmorStandEditor editor;
  private final ArrayList<Pair<Slot, EquipmentSlot>> armorSlots = new ArrayList<>();

  public ArmorStandScreenHandler(
      int syncId,
      PlayerInventory playerInventory,
      ArmorStandEntity armorStand,
      ScreenType screenType) {
    super(ArmorStandsMod.ARMOR_STAND_SCREEN_HANDLER_TYPE, syncId);

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

  public ArmorStandScreenHandler(
      int syncId,
      PlayerInventory playerInventory,
      PacketByteBuf buf) {
    this(
        syncId,
        playerInventory,
        (ArmorStandEntity) playerInventory.player.world.getEntityById(buf.readInt()),
        ScreenType.fromId(buf.readString()));
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
          return Pair.of(
              PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
              EMPTY_HAND_SLOT_TEXTURES[index]);
        }

        @Override
        public boolean canTakeItems(PlayerEntity player) {
          if (!ArmorStandScreenHandler.this.playerInventory.player.isCreative()
              && isSlotDisabled(armorStand, equipmentSlot)) {
            return false;
          }
          return super.canTakeItems(player);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
          if (!ArmorStandScreenHandler.this.playerInventory.player.isCreative()
              && isSlotDisabled(armorStand, equipmentSlot)) {
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
          if (!ArmorStandScreenHandler.this.playerInventory.player.isCreative()
              && isSlotDisabled(armorStand, equipmentSlot)) {
            return false;
          }
          return super.canTakeItems(player);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
          if (!ArmorStandScreenHandler.this.playerInventory.player.isCreative()
              && isSlotDisabled(armorStand, equipmentSlot)) {
            return false;
          }
          return equipmentSlot == ArmorStandEntity.getPreferredEquipmentSlot(stack);
        }

        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
          return Pair.of(
              PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
              EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
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

  @Override
  public ArmorStandEntity getArmorStand() {
    return this.armorStand;
  }

  @Override
  public ArmorStandEditor getEditor() {
    return this.editor;
  }

  public PlayerInventory getPlayerInventory() {
    return this.playerInventory;
  }

  @Override
  public void sendContentUpdates() {
    if (this.playerInventory.player instanceof ServerPlayerEntity) {
      ClientUpdatePacket.sendToClient((ServerPlayerEntity) this.playerInventory.player, this.armorStand);
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
  }

  private boolean tryTransferArmor(Slot source, ItemStack stack) {
    EquipmentSlot equipmentSlot = ArmorStandEntity.getPreferredEquipmentSlot(stack);

    if (equipmentSlot.getType() != EquipmentSlot.Type.ARMOR) {
      return false;
    }

    int targetIndex = this.slots.size() - 1 - equipmentSlot.getEntitySlotId();
    Slot slot = this.slots.get(targetIndex);

    if (!slot.canInsert(stack)) {
      return false;
    }

    ItemStack equipped = slot.getStack();
    slot.setStack(stack);
    source.setStack(equipped);

    return true;
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

  public static class Factory implements ExtendedScreenHandlerFactory {
    private final ScreenType screenType;
    private final ArmorStandEntity armorStand;

    private Factory(ScreenType screenType, ArmorStandEntity armorStand) {
      this.screenType = screenType;
      this.armorStand = armorStand;
    }

    public static Factory create(ScreenType screenType, ArmorStandEntity armorStand) {
      return new Factory(screenType, armorStand);
    }

    @Override
    public Text getDisplayName() {
      return screenType.getDisplayName();
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
      return new ArmorStandScreenHandler(
          syncId,
          playerInventory,
          this.armorStand,
          this.screenType);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
      buf.writeInt(this.armorStand.getId());
      buf.writeString(this.screenType.getId());
    }
  }
}
