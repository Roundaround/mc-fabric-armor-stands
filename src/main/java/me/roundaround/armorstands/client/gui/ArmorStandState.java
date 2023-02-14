package me.roundaround.armorstands.client.gui;

import me.roundaround.armorstands.client.ArmorStandsClientMod;
import me.roundaround.armorstands.mixin.MouseAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class ArmorStandState {
  private final MinecraftClient client;
  private final ArmorStandEntity armorStand;

  private boolean cursorLocked = false;

  public ArmorStandState(
      MinecraftClient client,
      ArmorStandEntity armorStand) {
    this.client = client;
    this.armorStand = armorStand;
  }

  public MinecraftClient getClient() {
    return client;
  }

  public ArmorStandEntity getArmorStand() {
    return armorStand;
  }

  public boolean shouldHighlight(Entity entity) {
    return ArmorStandsClientMod.highlightArmorStandKeyBinding.isPressed()
        && entity == this.armorStand;
  }

  public boolean isCursorLocked() {
    return this.cursorLocked;
  }

  public void lockCursor() {
    this.cursorLocked = true;
    int x = this.client.getWindow().getWidth() / 2;
    int y = this.client.getWindow().getHeight() / 2;
    ((MouseAccessor) this.client.mouse).setX(x);
    ((MouseAccessor) this.client.mouse).setY(y);
    InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_DISABLED, x, y);
  }

  public void unlockCursor() {
    this.cursorLocked = false;
    int x = this.client.getWindow().getWidth() / 2;
    int y = this.client.getWindow().getHeight() / 2;
    ((MouseAccessor) this.client.mouse).setX(x);
    ((MouseAccessor) this.client.mouse).setY(y);
    InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_NORMAL, x, y);
  }
}
