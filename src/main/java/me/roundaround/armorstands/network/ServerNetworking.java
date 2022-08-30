package me.roundaround.armorstands.network;

import java.util.UUID;

import io.netty.buffer.Unpooled;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.ArmorStandPositioning;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ServerNetworking {
  public static void registerReceivers() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.ADJUST_YAW_PACKET,
        ServerNetworking::handleAdjustYawPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.ADJUST_POS_PACKET,
        ServerNetworking::handleAdjustPosPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.SNAP_POS_PACKET,
        ServerNetworking::handleSnapPosPacket);
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

    int yaw = Math.round(entity.getYaw() + amount) % 360;

    entity.setYaw(yaw);
    entity.resetPosition();
  }

  public static void handleAdjustPosPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    UUID armorStandUuid = buf.readUuid();
    Direction direction = Direction.byId(buf.readInt());
    int pixels = buf.readInt();

    Entity entity = player.getWorld().getEntity(armorStandUuid);

    if (entity == null || !(entity instanceof ArmorStandEntity)) {
      return;
    }

    Vec3d position = entity.getPos()
        .add(new Vec3d(direction.getUnitVector()).multiply(pixels * 0.0625));
    double x = Math.round(position.x * 16) / 16.0;
    double y = Math.round(position.y * 16) / 16.0;
    double z = Math.round(position.z * 16) / 16.0;

    ArmorStandPositioning.setPosition(entity, x, y, z);
  }

  public static void handleSnapPosPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    UUID armorStandUuid = buf.readUuid();
    SnapPosition snap = SnapPosition.fromString(buf.readString());

    Entity entity = player.getWorld().getEntity(armorStandUuid);

    if (entity == null || !(entity instanceof ArmorStandEntity)) {
      return;
    }

    snap.apply(entity);
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
