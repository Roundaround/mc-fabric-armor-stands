package me.roundaround.armorstands.network;

import java.util.UUID;

import io.netty.buffer.Unpooled;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.ArmorStandEditor;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;

public class ServerNetworking {
  public static void registerReceivers() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.ADJUST_YAW_PACKET,
        ServerNetworking::handleAdjustYawPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.ADJUST_POS_PACKET,
        ServerNetworking::handleAdjustPosPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.ALIGN_POS_PACKET,
        ServerNetworking::handleAlignPosPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.TOGGLE_FLAG_PACKET,
        ServerNetworking::handleToggleFlagPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.SET_FLAG_PACKET,
        ServerNetworking::handleSetFlagPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.POPULATE_SLOTS_PACKET,
        ServerNetworking::handlePopulateStacksPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.SET_POSE_PACKET,
        ServerNetworking::handleSetPosePacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.UNDO_PACKET,
        ServerNetworking::handleUndoPacket);
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
    Direction direction = Direction.byId(buf.readInt());
    int pixels = buf.readInt();

    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ArmorStandEditor editor = ((ArmorStandScreenHandler) player.currentScreenHandler).editor;
    editor.movePos(direction, pixels);
  }

  public static void handleAlignPosPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    AlignPosition align = AlignPosition.fromString(buf.readString());

    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ArmorStandEditor editor = ((ArmorStandScreenHandler) player.currentScreenHandler).editor;
    align.apply(editor);
  }

  public static void handleToggleFlagPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    ArmorStandFlag flag = ArmorStandFlag.fromString(buf.readString());

    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ArmorStandEditor editor = ((ArmorStandScreenHandler) player.currentScreenHandler).editor;
    editor.toggleFlag(flag);
  }

  public static void handleSetFlagPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    ArmorStandFlag flag = ArmorStandFlag.fromString(buf.readString());
    boolean value = buf.readBoolean();

    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ArmorStandEditor editor = ((ArmorStandScreenHandler) player.currentScreenHandler).editor;
    editor.setFlag(flag, value);
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

  public static void handleSetPosePacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    boolean isPreset = buf.readBoolean();

    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ArmorStandEditor editor = ((ArmorStandScreenHandler) player.currentScreenHandler).editor;

    if (isPreset) {
      PosePreset preset = PosePreset.fromString(buf.readString());
      editor.setPose(preset.toPose());
      return;
    }

    editor.setPose(
        readEulerAngle(buf),
        readEulerAngle(buf),
        readEulerAngle(buf),
        readEulerAngle(buf),
        readEulerAngle(buf),
        readEulerAngle(buf));
  }

  private static EulerAngle readEulerAngle(PacketByteBuf buf) {
    return new EulerAngle(buf.readFloat(), buf.readFloat(), buf.readFloat());
  }

  public static void handleUndoPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    boolean redo = buf.readBoolean();

    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ArmorStandEditor editor = ((ArmorStandScreenHandler) player.currentScreenHandler).editor;
    if (redo) {
      editor.redo();
    } else {
      editor.undo();
    }
  }

  public static void sendOpenScreenPacket(ServerPlayerEntity player, ArmorStandEntity armorStand, int syncId) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(syncId);
    buf.writeInt(armorStand.getId());

    ServerPlayNetworking.send(player, NetworkPackets.OPEN_SCREEN_PACKET, buf);
  }
}
