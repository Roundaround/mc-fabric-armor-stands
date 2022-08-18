package me.roundaround.armorstands.client;

import org.lwjgl.glfw.GLFW;

import me.roundaround.armorstands.client.gui.screen.ArmorStandCoreScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class ArmorStandsClientMod implements ClientModInitializer {
  public static KeyBinding editArmorStandKeyBinding;

  @Override
  public void onInitializeClient() {
    editArmorStandKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "armorstands.key.edit_armor_stand",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_O,
        "armorstands.key.category"));

    ClientTickEvents.END_CLIENT_TICK.register((client) -> {
      while (editArmorStandKeyBinding.wasPressed()) {
        Entity entity = client.targetedEntity;
        if (!(entity instanceof ArmorStandEntity)) {
          return;
        }

        client.setScreen(new ArmorStandCoreScreen((ArmorStandEntity) entity, true));
      }
    });
  }
}
