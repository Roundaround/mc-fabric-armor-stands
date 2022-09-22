package me.roundaround.armorstands.util;

import net.minecraft.entity.decoration.ArmorStandEntity;

@FunctionalInterface
public interface ArmorStandApplyable {
  void apply(ArmorStandEntity armorStand);
}
