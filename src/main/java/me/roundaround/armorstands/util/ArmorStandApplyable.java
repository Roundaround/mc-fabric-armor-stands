package me.roundaround.armorstands.util;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface ArmorStandApplyable {
  void apply(PlayerEntity player, ArmorStandEntity armorStand);
}
