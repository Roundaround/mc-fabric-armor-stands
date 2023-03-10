package me.roundaround.armorstands.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import com.google.common.hash.Hashing;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.network.packet.s2c.ClientUpdatePacket;
import me.roundaround.armorstands.network.packet.s2c.MessagePacket;
import me.roundaround.armorstands.network.packet.s2c.OpenScreenPacket;
import me.roundaround.armorstands.network.packet.s2c.PongPacket;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
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
    PongPacket.registerClientReceiver();
  }

  /**
   * Fun surprise for the nerd nugget
   * 
   * Used from the following mixins:
   * - ClientPlayerEntityMixin
   * - PlayerEntityRendererMixin
   * 
   * If you do not know what this is, you are not the target, and you can ignore
   * this. It actually has nothing to do with the mod, and does not affect the
   * game in any way.
   */

  private static long lastDoofusTime = 0;

  public static boolean isTheDoofusNugget(AbstractClientPlayerEntity player) {
    // Don't want to expose his username, so check against the hash instead
    String username = player.getName().getString();
    String expected = "ba0df6ea0d50feddda95673a8398abac0e6b158c7e23467ab4edcd24ce7c0a60e85728208ba73780f0bf7abe3c1628d612791d2d82ec52494d5d7ee5aa7dc94e";

    String hash = Hashing.sha512()
        .hashString(username + "V%^tAHwdHjh*r5af", StandardCharsets.UTF_8)
        .toString();

    return hash.equals(expected);
  }

  public static boolean isAprilFirst() {
    LocalDate now = LocalDate.now();
    return now.getMonthValue() == 4 && now.getDayOfMonth() == 1;
  }

  public static void admit() {
    lastDoofusTime = System.currentTimeMillis();
  }

  public static boolean hasAdmittedInTheLastHour() {
    return System.currentTimeMillis() - lastDoofusTime <= 3600000;
  }
}
