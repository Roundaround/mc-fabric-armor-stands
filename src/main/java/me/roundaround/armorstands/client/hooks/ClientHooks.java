package me.roundaround.armorstands.client.hooks;

import me.roundaround.armorstands.client.gui.screen.ArmorStandCoreScreen;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.ArmorStandEntity;

/*
 * Used to separate client-only code for mixed-purpose classes like Mixins.
 */
public class ClientHooks {
  public static void openArmorStandScreen(ArmorStandEntity armorStand) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.currentScreen instanceof AbstractArmorStandScreen) {
      return;
    }

    client.setScreen(new ArmorStandCoreScreen(armorStand, true));
  }
}