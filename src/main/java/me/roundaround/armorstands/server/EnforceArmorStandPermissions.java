package me.roundaround.armorstands.server;

public interface EnforceArmorStandPermissions {
  default boolean getEnforceArmorStandPermissions() {
    return false;
  }
}
