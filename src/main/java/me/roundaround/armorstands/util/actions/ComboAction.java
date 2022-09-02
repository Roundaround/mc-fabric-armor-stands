package me.roundaround.armorstands.util.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.decoration.ArmorStandEntity;

public class ComboAction implements ArmorStandAction {
  private final ArrayList<ArmorStandAction> actions = new ArrayList<>();

  private ComboAction(Collection<ArmorStandAction> actions) {
    this.actions.addAll(actions);
  }

  public static ComboAction of(Collection<ArmorStandAction> actions) {
    return new ComboAction(actions);
  }

  public static ComboAction of(ArmorStandAction... actions) {
    return of(List.of(actions));
  }

  @Override
  public void apply(ArmorStandEntity armorStand) {
    for (int i = 0; i < actions.size(); i++) {
      actions.get(i).apply(armorStand);
    }
  }

  @Override
  public void undo(ArmorStandEntity armorStand) {
    for (int i = actions.size() - 1; i >= 0; i--) {
      actions.get(i).undo(armorStand);
    }
  }
}
