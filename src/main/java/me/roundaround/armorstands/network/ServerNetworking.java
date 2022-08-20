package me.roundaround.armorstands.network;

import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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

  public static void handleIdentifyStandPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    UUID armorStandUuid = buf.readUuid();

    Entity entity = player.getWorld().getEntity(armorStandUuid);

    if (entity == null || !(entity instanceof ArmorStandEntity)) {
      return;
    }

    ArmorStandEntity armorStand = (ArmorStandEntity) entity;

    armorStand.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100), player);
  }

  public static void handleCancelIdentifyPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    UUID armorStandUuid = buf.readUuid();

    Entity entity = player.getWorld().getEntity(armorStandUuid);

    if (entity == null || !(entity instanceof ArmorStandEntity)) {
      return;
    }

    ArmorStandEntity armorStand = (ArmorStandEntity) entity;

    armorStand.removeStatusEffect(StatusEffects.GLOWING);
  }
}
