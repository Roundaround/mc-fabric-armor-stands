package me.roundaround.armorstands.screen;

import me.roundaround.armorstands.network.ServerNetworking;
import me.roundaround.armorstands.util.ArmorStandEditor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ArmorStandBasicScreenHandler extends ScreenHandler {
  private final PlayerEntity player;
  private final ArmorStandEntity armorStand;
  private final ArmorStandEditor editor;

  public ArmorStandBasicScreenHandler(int syncId, PlayerEntity player, ArmorStandEntity armorStand) {
    super(null, syncId);

    this.player = player;
    this.armorStand = armorStand;

    if (this.player instanceof ServerPlayerEntity) {
      this.editor = new ArmorStandEditor(armorStand);
    } else {
      this.editor = null;
    }
  }

  public ArmorStandEntity getArmorStand() {
    return this.armorStand;
  }

  @Environment(EnvType.SERVER)
  public ArmorStandEditor getEditor() {
    return this.editor;
  }

  @Override
  public void sendContentUpdates() {
    if (this.player instanceof ServerPlayerEntity) {
      ServerNetworking.sendClientUpdatePacket((ServerPlayerEntity) this.player, this.armorStand);
    }

    super.sendContentUpdates();
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return false;
  }

  @Override
  public ItemStack transferSlot(PlayerEntity player, int index) {
    return ItemStack.EMPTY;
  }
}
