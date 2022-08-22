package me.roundaround.armorstands.screen;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.entity.ArmorStandInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class ArmorStandScreenHandler extends ScreenHandler {
  public ArmorStandScreenHandler(int syncId, PlayerInventory playerInventory) {
    this(syncId, playerInventory, new ArmorStandInventory(null));
  }
  
  public ArmorStandScreenHandler(int syncId, PlayerInventory playerInventory, ArmorStandInventory inventory) {
    super(ArmorStandsMod.ARMOR_STAND_SCREEN_HANDLER, syncId);
  }

  @Override
  public ItemStack transferSlot(PlayerEntity var1, int var2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean canUse(PlayerEntity var1) {
    // TODO Auto-generated method stub
    return false;
  }

}
