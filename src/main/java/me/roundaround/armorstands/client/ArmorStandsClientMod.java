package me.roundaround.armorstands.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.roundaround.armorstands.client.network.ClientNetworking;
import me.roundaround.armorstands.generated.Constants;
import me.roundaround.roundalib.util.BuiltinResourcePack;
import me.roundaround.gradle.api.annotation.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

@Entrypoint(Entrypoint.CLIENT)
public class ArmorStandsClientMod implements ClientModInitializer {
  public static final String RESOURCE_PACK_ID = "armorstands-dark-ui";

  public static KeyMapping highlightArmorStandKeyMapping;

  @Override
  public void onInitializeClient() {
    ClientNetworking.registerReceivers();

    highlightArmorStandKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "armorstands.key.highlight_armor_stand",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KeyMapping.Category.MISC
    ));

    BuiltinResourcePack.register(Constants.MOD_ID, RESOURCE_PACK_ID, Component.translatable("armorstands.resource.darkui"));
  }
}
