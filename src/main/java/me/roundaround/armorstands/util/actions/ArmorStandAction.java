package me.roundaround.armorstands.util.actions;

import net.minecraft.entity.decoration.ArmorStandEntity;

public interface ArmorStandAction {
  public abstract void apply(ArmorStandEntity armorStand);

  public abstract void undo(ArmorStandEntity armorStand);

  public static ArmorStandAction noop() {
    return new ArmorStandAction() {
      @Override
      public void apply(ArmorStandEntity armorStand) {
      }

      @Override
      public void undo(ArmorStandEntity armorStand) {
      }
    };
  }
}
