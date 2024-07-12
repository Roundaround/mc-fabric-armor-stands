package me.roundaround.armorstands.client;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.screen.*;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ArmorStandsClientMod implements ClientModInitializer {
  public static KeyBinding highlightArmorStandKeyBinding;

  @Override
  public void onInitializeClient() {
    ClientNetworking.registerReceivers();

    highlightArmorStandKeyBinding = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("armorstands.key.highlight_armor_stand", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(),
            KeyBinding.MISC_CATEGORY
        ));

    FabricLoader.getInstance()
        .getModContainer(ArmorStandsMod.MOD_ID)
        .ifPresent((container) -> ResourceManagerHelper.registerBuiltinResourcePack(
            new Identifier(ArmorStandsMod.MOD_ID, "armorstands-dark-ui"), container,
            Text.literal("Armor Stands Dark UI"), ResourcePackActivationType.NORMAL
        ));

    HandledScreens.register(ArmorStandsMod.ARMOR_STAND_SCREEN_HANDLER_TYPE, new HandledScreenProvider());
  }

  private static class HandledScreenProvider implements HandledScreens.Provider<ArmorStandScreenHandler,
      AbstractArmorStandScreen> {
    @Override
    public AbstractArmorStandScreen create(
        ArmorStandScreenHandler handler, PlayerInventory playerInventory, Text title
    ) {
      return switch (handler.getScreenType()) {
        case UTILITIES -> new ArmorStandUtilitiesScreen(handler);
        case MOVE -> new ArmorStandMoveScreen(handler);
        case ROTATE -> new ArmorStandRotateScreen(handler);
        case POSE -> new ArmorStandPoseScreen(handler);
        case PRESETS -> new ArmorStandPresetsScreen(handler);
        case INVENTORY -> new ArmorStandInventoryScreen(handler);
      };
    }
  }
}
