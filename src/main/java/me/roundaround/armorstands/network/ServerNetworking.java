package me.roundaround.armorstands.network;

import java.util.UUID;

import me.roundaround.armorstands.mixin.ArmorStandEntityAccessor;
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

    float currentYaw = entity.getYaw();
    entity.setYaw((currentYaw + amount) % 360f);
  }

  public static void handleToggleFlagPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    // TODO: Handle error case for non-existant flag values.

    UUID armorStandUuid = buf.readUuid();
    ArmorStandFlag flag = ArmorStandFlag.fromString(buf.readString()).get();

    Entity entity = player.getWorld().getEntity(armorStandUuid);

    if (entity == null || !(entity instanceof ArmorStandEntity)) {
      return;
    }

    ArmorStandEntity armorStand = (ArmorStandEntity) entity;
    ArmorStandEntityAccessor accessor = (ArmorStandEntityAccessor) entity;

    switch (flag) {
      case BASE:
        accessor.invokeSetHideBasePlate(!armorStand.shouldHideBasePlate());
        break;
      case ARMS:
        accessor.invokeSetShowArms(!armorStand.shouldShowArms());
        break;
      case SMALL:
        accessor.invokeSetSmall(!armorStand.isSmall());
        break;
      case GRAVITY:
        armorStand.setNoGravity(!armorStand.hasNoGravity());
        break;
      case VISIBLE:
        armorStand.setInvisible(!armorStand.isInvisible());
        break;
      case NAME:
        armorStand.setCustomNameVisible(!armorStand.isCustomNameVisible());
        break;
    }
  }
}
