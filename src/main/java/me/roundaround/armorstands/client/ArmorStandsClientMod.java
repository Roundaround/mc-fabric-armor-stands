package me.roundaround.armorstands.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.network.packet.s2c.ClientUpdatePacket;
import me.roundaround.armorstands.network.packet.s2c.MessagePacket;
import me.roundaround.armorstands.network.packet.s2c.OpenScreenPacket;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class ArmorStandsClientMod implements ClientModInitializer {
  public static KeyBinding highlightArmorStandKeyBinding;
  public static boolean darkModeDetected;

  @Override
  public void onInitializeClient() {
    registerReceivers();

    highlightArmorStandKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "armorstands.key.highlight_armor_stand",
        InputUtil.Type.KEYSYM,
        InputUtil.UNKNOWN_KEY.getCode(),
        KeyBinding.MISC_CATEGORY));

    ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
        .register((atlasTexture, registry) -> {
          registry.register(ArmorStandScreenHandler.EMPTY_MAINHAND_ARMOR_SLOT);
        });

    // Detect Vanilla Tweaks dark UI and automatically adjust textures to match
    // if it is loaded
    ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
        .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
          @Override
          public Identifier getFabricId() {
            return new Identifier(ArmorStandsMod.MOD_ID, "resource_pack_loader");
          }

          @Override
          public void reload(ResourceManager manager) {
            darkModeDetected = false;

            manager.streamResourcePacks().forEach((pack) -> {
              if (!(pack instanceof ZipResourcePack)) {
                return;
              }

              ZipResourcePack zipPack = (ZipResourcePack) pack;

              if (!zipPack.containsFile("Selected Packs.txt")) {
                return;
              }

              try (InputStream stream = zipPack.openRoot("Selected Packs.txt")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                if (stream != null) {
                  String str = "";
                  while ((str = reader.readLine()) != null) {
                    if (str.trim().equals("DarkUI")) {
                      darkModeDetected = true;
                      break;
                    }
                  }
                }
              } catch (IOException e) {

              }
            });
          }
        });
  }

  private static void registerReceivers() {
    OpenScreenPacket.registerClientReceiver();
    ClientUpdatePacket.registerClientReceiver();
    MessagePacket.registerClientReceiver();
  }
}
