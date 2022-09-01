package me.roundaround.armorstands.util.actions;

import me.roundaround.armorstands.network.ArmorStandFlag;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class FlagAction implements ArmorStandAction {
  private final ArmorStandFlag flag;
  private final boolean toggle;
  private final boolean value;
  private boolean previous;

  private FlagAction(ArmorStandFlag flag, boolean toggle, boolean value) {
    this.flag = flag;
    this.toggle = toggle;
    this.value = value;
  }

  public static FlagAction set(ArmorStandFlag flag, boolean value) {
    return new FlagAction(flag, false, value);
  }

  public static FlagAction toggle(ArmorStandFlag flag) {
    return new FlagAction(flag, true, false);
  }

  @Override
  public void apply(ArmorStandEntity armorStand) {
    if (toggle) {
      flag.setValue(armorStand, !flag.getValue(armorStand));
      return;
    }

    previous = flag.getValue(armorStand);
    flag.setValue(armorStand, value);
  }

  @Override
  public void undo(ArmorStandEntity armorStand) {
    if (toggle) {
      flag.setValue(armorStand, !flag.getValue(armorStand));
      return;
    }

    flag.setValue(armorStand, previous);
  }
}
