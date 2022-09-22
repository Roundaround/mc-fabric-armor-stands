package me.roundaround.armorstands.util.actions;

import java.util.Optional;

import me.roundaround.armorstands.util.ArmorStandApplyable;
import net.minecraft.entity.decoration.ArmorStandEntity;

public abstract class SimpleArmorStandAction<T extends ArmorStandApplyable> implements ArmorStandAction {
  private final T value;
  private Optional<T> original = Optional.empty();

  protected SimpleArmorStandAction(T value) {
    this.value = value;
  }

  protected abstract T get(ArmorStandEntity armorStand);

  @Override
  public void apply(ArmorStandEntity armorStand) {
    original = Optional.of(get(armorStand));
    value.apply(armorStand);
  }

  @Override
  public void undo(ArmorStandEntity armorStand) {
    original.ifPresent((original) -> original.apply(armorStand));
  }
}
