package me.roundaround.armorstands.client;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class ArmorStandsClientMod implements ClientModInitializer {
  public static KeyBinding editArmorStanKeyBinding;

  @Override
  public void onInitializeClient() {
    editArmorStanKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "armorstands.key.edit_armor_stand",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_O,
        "armorstands.key.category"));

    ClientTickEvents.END_CLIENT_TICK.register((client) -> {
      while (editArmorStanKeyBinding.wasPressed()) {
        Entity camera = client.getCameraEntity();
        if (client.world == null || camera == null) {
          return;
        }

        HitResult hitResult = camera.raycast(10, 0, false);
        if (hitResult.getType() != HitResult.Type.ENTITY) {
          return;
        }

        Entity entity = ((EntityHitResult) hitResult).getEntity();
        if (!(entity instanceof ArmorStandEntity)) {
          return;
        }

        client.setScreen(new ArmorStandScreen((ArmorStandEntity) entity));
      }
    });
  }
}
