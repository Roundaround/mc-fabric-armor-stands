package me.roundaround.armorstands.util.actions;

import me.roundaround.armorstands.util.ArmorStandApplyable;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public interface ArmorStandAction extends ArmorStandApplyable {
  public abstract Text getName(ArmorStandEntity armorStand);

  public abstract void undo(PlayerEntity player, ArmorStandEntity armorStand);

  public static ArmorStandAction noop() {
    return new ArmorStandAction() {
      @Override
      public Text getName(ArmorStandEntity armorStand) {
        return null;
      }

      @Override
      public void apply(PlayerEntity player, ArmorStandEntity armorStand) {
      }

      @Override
      public void undo(PlayerEntity player, ArmorStandEntity armorStand) {
      }
    };
  }
}
