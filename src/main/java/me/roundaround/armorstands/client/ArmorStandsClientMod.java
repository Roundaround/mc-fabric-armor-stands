package me.roundaround.armorstands.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.generated.Constants;
import me.roundaround.armorstands.roundalib.util.BuiltinResourcePack;
import me.roundaround.gradle.api.annotation.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

@Entrypoint(Entrypoint.CLIENT)
public class ArmorStandsClientMod implements ClientModInitializer {
  public static final String RESOURCE_PACK_ID = "armorstands-dark-ui";

  public static KeyMapping highlightArmorStandKeyBinding;

  @Override
  public void onInitializeClient() {
    ClientNetworking.registerReceivers();

    highlightArmorStandKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
        "armorstands.key.highlight_armor_stand",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KeyMapping.Category.MISC
    ));

    BuiltinResourcePack.register(Constants.MOD_ID, RESOURCE_PACK_ID, Component.translatable("armorstands.resource.darkui"));
  }
}
