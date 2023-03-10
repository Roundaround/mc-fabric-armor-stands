package me.roundaround.armorstands.network.packet.s2c;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandInventoryScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandMoveScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandPoseScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandPresetsScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandRotateScreen;
import me.roundaround.armorstands.client.gui.screen.ArmorStandUtilitiesScreen;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class OpenScreenPacket {
  private final int syncId;
  private final int armorStandId;
  private final ScreenType screenType;

  private OpenScreenPacket(PacketByteBuf buf) {
    this.syncId = buf.readInt();
    this.armorStandId = buf.readInt();
    this.screenType = ScreenType.fromId(buf.readString());
  }

  private OpenScreenPacket(int syncId, ArmorStandEntity armorStand, ScreenType screenType) {
    this.syncId = syncId;
    this.armorStandId = armorStand.getId();
    this.screenType = screenType;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeInt(this.syncId);
    buf.writeInt(this.armorStandId);
    buf.writeString(this.screenType.getId());
    return buf;
  }

  private void handleOnClient(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketSender responseSender) {
    ClientPlayerEntity player = client.player;
    Entity entity = player.getWorld().getEntityById(this.armorStandId);

    if (!(entity instanceof ArmorStandEntity)) {
      return;
    }

    ArmorStandEntity armorStand = (ArmorStandEntity) entity;
    ArmorStandScreenHandler screenHandler = new ArmorStandScreenHandler(
        this.syncId,
        player.getInventory(),
        armorStand,
        this.screenType);

    player.currentScreenHandler = screenHandler;

    AbstractArmorStandScreen screen = switch (screenType) {
      case UTILITIES -> new ArmorStandUtilitiesScreen(screenHandler, screenHandler.getArmorStand());
      case MOVE -> new ArmorStandMoveScreen(screenHandler, screenHandler.getArmorStand());
      case ROTATE -> new ArmorStandRotateScreen(screenHandler, screenHandler.getArmorStand());
      case POSE -> new ArmorStandPoseScreen(screenHandler, screenHandler.getArmorStand());
      case PRESETS -> new ArmorStandPresetsScreen(screenHandler, screenHandler.getArmorStand());
      case INVENTORY -> new ArmorStandInventoryScreen(screenHandler, screenHandler.getArmorStand());
    };

    client.setScreen(screen);
  }

  public static void sendToClient(
      ServerPlayerEntity player,
      int syncId,
      ArmorStandEntity armorStand,
      ScreenType screenType) {
    ServerPlayNetworking.send(
        player,
        NetworkPackets.OPEN_SCREEN_PACKET,
        new OpenScreenPacket(syncId, armorStand, screenType).toPacket());
  }

  public static void registerClientReceiver() {
    ClientPlayNetworking.registerGlobalReceiver(
        NetworkPackets.OPEN_SCREEN_PACKET,
        (client, handler, buf, responseSender) -> {
          OpenScreenPacket packet = new OpenScreenPacket(buf);
          client.execute(() -> {
            packet.handleOnClient(client, handler, responseSender);
          });
        });
  }
}
