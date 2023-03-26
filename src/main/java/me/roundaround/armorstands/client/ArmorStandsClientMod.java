package me.roundaround.armorstands.client;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import com.google.common.hash.Hashing;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.client.gui.screen.ArmorStandInventoryScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandMoveScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandPoseScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandPresetsScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandRotateScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandUtilitiesScreen;
import me.roundaround.armorstands.network.packet.s2c.ClientUpdatePacket;
import me.roundaround.armorstands.network.packet.s2c.MessagePacket;
import me.roundaround.armorstands.network.packet.s2c.PongPacket;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class ArmorStandsClientMod implements ClientModInitializer {
  public static KeyBinding highlightArmorStandKeyBinding;

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

    FabricLoader.getInstance().getModContainer(ArmorStandsMod.MOD_ID).ifPresent((container) -> {
      ResourceManagerHelper.registerBuiltinResourcePack(
          new Identifier(ArmorStandsMod.MOD_ID, "armorstands-dark-ui"),
          container,
          "Armor Stands Dark UI",
          ResourcePackActivationType.NORMAL);
    });

    HandledScreens.register(ArmorStandsMod.ARMOR_STAND_SCREEN_HANDLER_TYPE, (handler, inventory, title) -> {
      return switch (handler.getScreenType()) {
        case UTILITIES -> new ArmorStandUtilitiesScreen(handler);
        case MOVE -> new ArmorStandMoveScreen(handler);
        case ROTATE -> new ArmorStandRotateScreen(handler);
        case POSE -> new ArmorStandPoseScreen(handler);
        case PRESETS -> new ArmorStandPresetsScreen(handler);
        case INVENTORY -> new ArmorStandInventoryScreen(handler);
      };
    });
  }

  private static void registerReceivers() {
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
