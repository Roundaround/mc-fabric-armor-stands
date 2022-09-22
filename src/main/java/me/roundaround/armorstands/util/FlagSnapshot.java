package me.roundaround.armorstands.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Predicate;

import me.roundaround.armorstands.network.ArmorStandFlag;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class FlagSnapshot implements ArmorStandApplyable {
  private HashMap<ArmorStandFlag, Boolean> values = new HashMap<>();

  private FlagSnapshot(ArmorStandEntity armorStand, Predicate<ArmorStandFlag> predicate) {
    Arrays.stream(ArmorStandFlag.values())
        .filter(predicate)
        .forEach((flag) -> {
          values.put(flag, flag.getValue(armorStand));
        });
  }

  public static FlagSnapshot all(ArmorStandEntity armorStand) {
    return new FlagSnapshot(armorStand, (flag) -> true);
  }

  public static FlagSnapshot none(ArmorStandEntity armorStand) {
    return new FlagSnapshot(armorStand, (flag) -> false);
  }

  public static FlagSnapshot some(ArmorStandEntity armorStand, Predicate<ArmorStandFlag> predicate) {
    return new FlagSnapshot(armorStand, predicate);
  }

  @Override
  public void apply(ArmorStandEntity armorStand) {
    values.entrySet().forEach((entry) -> {
      entry.getKey().setValue(armorStand, entry.getValue());
    });
  }
}
