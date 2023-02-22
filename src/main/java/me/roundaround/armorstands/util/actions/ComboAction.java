package me.roundaround.armorstands.util.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class ComboAction implements ArmorStandAction {
  protected Function<ArmorStandEntity, Text> nameFunction;
  protected final ArrayList<ArmorStandAction> actions = new ArrayList<>();

  protected ComboAction(Function<ArmorStandEntity, Text> nameFunction, Collection<ArmorStandAction> actions) {
    this.nameFunction = nameFunction;
    this.actions.addAll(actions);
  }

  protected ComboAction(Text name, Collection<ArmorStandAction> actions) {
    this((armorStand) -> name, actions);
  }

  public static ComboAction of(Text name, Collection<ArmorStandAction> actions) {
    return new ComboAction(name, actions);
  }

  public static ComboAction of(Text name, ArmorStandAction... actions) {
    return of(name, List.of(actions));
  }

  public static ComboAction of(Function<ArmorStandEntity, Text> nameFunction, Collection<ArmorStandAction> actions) {
    return new ComboAction(nameFunction, actions);
  }

  public static ComboAction of(Function<ArmorStandEntity, Text> nameFunction, ArmorStandAction... actions) {
    return of(nameFunction, List.of(actions));
  }

  @Override
  public Text getName(ArmorStandEntity armorStand) {
    return nameFunction.apply(armorStand);
  }

  @Override
  public void apply(ArmorStandEntity armorStand) {
    for (int i = 0; i < actions.size(); i++) {
      ArmorStandAction action = actions.get(i);
      if (action != null) {
        action.apply(armorStand);
      }
    }
  }

  @Override
  public void undo(ArmorStandEntity armorStand) {
    for (int i = actions.size() - 1; i >= 0; i--) {
      actions.get(i).undo(armorStand);
    }
  }
}
