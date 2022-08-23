package me.roundaround.armorstands.network;

import java.util.UUID;

import io.netty.buffer.Unpooled;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetworking {
  public static void registerReceivers() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.ADJUST_YAW_PACKET,
        ServerNetworking::handleAdjustYawPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.TOGGLE_FLAG_PACKET,
        ServerNetworking::handleToggleFlagPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.SET_FLAG_PACKET,
        ServerNetworking::handleSetFlagPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.POPULATE_SLOTS_PACKET,
        ServerNetworking::handlePopulateStacksPacket);
  }

  public static void handleAdjustYawPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    UUID armorStandUuid = buf.readUuid();
    int amount = buf.readInt();

    Entity entity = player.getWorld().getEntity(armorStandUuid);

    if (entity == null || !(entity instanceof ArmorStandEntity)) {
      return;
    }

    entity.setYaw(Math.round(entity.getYaw() + amount) % 360);
    entity.addScoreboardTag("test");
  }

  public static void handleToggleFlagPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    UUID armorStandUuid = buf.readUuid();
    ArmorStandFlag flag = ArmorStandFlag.fromString(buf.readString());

    Entity entity = player.getWorld().getEntity(armorStandUuid);

    if (entity == null || !(entity instanceof ArmorStandEntity)) {
      return;
    }

    boolean value = flag.getValue((ArmorStandEntity) entity);
    flag.setValue((ArmorStandEntity) entity, !value);
  }

  public static void handleSetFlagPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    UUID armorStandUuid = buf.readUuid();
    ArmorStandFlag flag = ArmorStandFlag.fromString(buf.readString());
    boolean value = buf.readBoolean();

    Entity entity = player.getWorld().getEntity(armorStandUuid);

    if (entity == null || !(entity instanceof ArmorStandEntity)) {
      return;
    }

    flag.setValue((ArmorStandEntity) entity, value);
  }

  public static void handlePopulateStacksPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    boolean fillSlots = buf.readBoolean();

    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ((ArmorStandScreenHandler) player.currentScreenHandler).populateSlots(fillSlots);
  }

  public static void sendOpenScreenPacket(ServerPlayerEntity player, ArmorStandEntity armorStand, int syncId) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(syncId);
    buf.writeInt(armorStand.getId());

    ServerPlayNetworking.send(player, NetworkPackets.OPEN_SCREEN_PACKET, buf);
  }
}
