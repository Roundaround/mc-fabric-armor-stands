package me.roundaround.armorstands.util.actions;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public interface ArmorStandAction {
  public abstract Text getName(ArmorStandEntity armorStand);

  public abstract void apply(ArmorStandEntity armorStand);

  public abstract void undo(ArmorStandEntity armorStand);

  public static ArmorStandAction noop() {
    return new ArmorStandAction() {
      @Override
      public Text getName(ArmorStandEntity armorStand) {
        return null;
      }

      @Override
      public void apply(ArmorStandEntity armorStand) {
      }

      @Override
      public void undo(ArmorStandEntity armorStand) {
      }
    };
  }
}
