package me.roundaround.armorstands.client;

import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.generated.Constants;
import me.roundaround.armorstands.roundalib.util.BuiltinResourcePack;
import me.roundaround.gradle.api.annotation.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

@Entrypoint(Entrypoint.CLIENT)
public class ArmorStandsClientMod implements ClientModInitializer {
  public static final String RESOURCE_PACK_ID = "armorstands-dark-ui";

  public static KeyBinding highlightArmorStandKeyBinding;

  @Override
  public void onInitializeClient() {
    ClientNetworking.registerReceivers();

    highlightArmorStandKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "armorstands.key.highlight_armor_stand",
        InputUtil.Type.KEYSYM,
        InputUtil.UNKNOWN_KEY.getCode(),
        KeyBinding.MISC_CATEGORY
    ));

    BuiltinResourcePack.register(Constants.MOD_ID, RESOURCE_PACK_ID, Text.translatable("armorstands.resource.darkui"));
  }
}
