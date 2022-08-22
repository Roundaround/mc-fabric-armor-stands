package me.roundaround.armorstands.client.network;

import io.netty.buffer.Unpooled;
import me.roundaround.armorstands.client.gui.screen.ArmorStandScreen;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.NetworkPackets;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;

public class ClientNetworking {
  public static void registerReceivers() {
    ClientPlayNetworking.registerGlobalReceiver(
        NetworkPackets.OPEN_SCREEN_PACKET,
        ClientNetworking::handleOpenScreenPacket);
    ClientPlayNetworking.registerGlobalReceiver(
        NetworkPackets.POPULATE_SLOTS_PACKET,
        ClientNetworking::handlePopulateSlotsPacket);
  }

  public static void handleOpenScreenPacket(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    int syncId = buf.readInt();
    int armorStandId = buf.readInt();

    client.execute(() -> {
      ClientPlayerEntity player = client.player;
      Entity entity = player.getWorld().getEntityById(armorStandId);

      if (!(entity instanceof ArmorStandEntity)) {
        return;
      }

      ArmorStandEntity armorStand = (ArmorStandEntity) entity;
      PlayerInventory playerInventory = player.getInventory();
      ArmorStandScreenHandler screenHandler = new ArmorStandScreenHandler(syncId, playerInventory, armorStand);

      player.currentScreenHandler = screenHandler;
      client.setScreen(new ArmorStandScreen(screenHandler, playerInventory, armorStand));
    });
  }

  public static void handlePopulateSlotsPacket(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    boolean fillSlots = buf.readBoolean();

    client.execute(() -> {
      ClientPlayerEntity player = client.player;

      if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
        return;
      }
      
      ((ArmorStandScreenHandler) player.currentScreenHandler).populateSlots(fillSlots);
    });
  }

  public static void sendAdjustYawPacket(ArmorStandEntity armorStand, int amount) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeUuid(armorStand.getUuid());
    buf.writeInt(amount);

    ClientPlayNetworking.send(NetworkPackets.ADJUST_YAW_PACKET, buf);
  }

  public static void sendToggleFlagPacket(ArmorStandEntity armorStand, ArmorStandFlag flag) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeUuid(armorStand.getUuid());
    buf.writeString(flag.toString());

    ClientPlayNetworking.send(NetworkPackets.TOGGLE_FLAG_PACKET, buf);
  }

  public static void sendSetFlagPacket(ArmorStandEntity armorStand, ArmorStandFlag flag, boolean value) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeUuid(armorStand.getUuid());
    buf.writeString(flag.toString());
    buf.writeBoolean(value);

    ClientPlayNetworking.send(NetworkPackets.SET_FLAG_PACKET, buf);
  }
}
