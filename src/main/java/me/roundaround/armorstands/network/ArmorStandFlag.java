package me.roundaround.armorstands.network;

import java.util.Arrays;
import java.util.Optional;

public enum ArmorStandFlag {
  BASE("base"),
  ARMS("arms"),
  SMALL("small"),
  GRAVITY("gravity"),
  VISIBLE("visible"),
  NAME("name");

  private final String value;

  private ArmorStandFlag(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  public static Optional<ArmorStandFlag> fromString(String value) {
    return Arrays.stream(ArmorStandFlag.values())
        .filter((flag) -> flag.value.equals(value))
        .findFirst();
  }
}
