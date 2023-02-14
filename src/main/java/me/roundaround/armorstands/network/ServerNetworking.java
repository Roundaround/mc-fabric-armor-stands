package me.roundaround.armorstands.network;

import io.netty.buffer.Unpooled;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.ArmorStandEditor;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
        NetworkPackets.UTILITY_ACTION_PACKET,
        ServerNetworking::handleUtilityActionPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.TOGGLE_FLAG_PACKET,
        ServerNetworking::handleToggleFlagPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.SET_FLAG_PACKET,
        ServerNetworking::handleSetFlagPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.SET_POSE_PACKET,
        ServerNetworking::handleSetPosePacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.UNDO_PACKET,
        ServerNetworking::handleUndoPacket);
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.INIT_SLOTS_PACKET,
        ServerNetworking::handleInitSlotsPacket);
  }

  private static EulerAngle readEulerAngle(PacketByteBuf buf) {
    return new EulerAngle(buf.readFloat(), buf.readFloat(), buf.readFloat());
  }

  public static void handleAdjustYawPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    int amount = buf.readInt();

    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ArmorStandScreenHandler screenHandler = (ArmorStandScreenHandler) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    editor.rotate(amount);
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

    ArmorStandScreenHandler screenHandler = (ArmorStandScreenHandler) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    editor.movePos(direction, pixels);
  }

  public static void handleUtilityActionPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    UtilityAction action = UtilityAction.fromString(buf.readString());

    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ArmorStandScreenHandler screenHandler = (ArmorStandScreenHandler) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    action.apply(editor, player);
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

    ArmorStandScreenHandler screenHandler = (ArmorStandScreenHandler) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
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

    ArmorStandScreenHandler screenHandler = (ArmorStandScreenHandler) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    editor.setFlag(flag, value);
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

    ArmorStandScreenHandler screenHandler = (ArmorStandScreenHandler) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();

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

    ArmorStandScreenHandler screenHandler = (ArmorStandScreenHandler) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    if (redo) {
      editor.redo();
      return;
    }
    editor.undo();
  }

  private static void handleInitSlotsPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    boolean fillSlots = buf.readBoolean();

    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ArmorStandScreenHandler screenHandler = (ArmorStandScreenHandler) player.currentScreenHandler;
    screenHandler.initSlots(fillSlots);
  }

  public static void sendOpenScreenPacket(ServerPlayerEntity player, ArmorStandEntity armorStand, int syncId) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(syncId);
    buf.writeInt(armorStand.getId());

    ServerPlayNetworking.send(player, NetworkPackets.OPEN_SCREEN_PACKET, buf);
  }

  public static void sendClientUpdatePacket(ServerPlayerEntity player, ArmorStandEntity armorStand) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(armorStand.getId());
    buf.writeDouble(armorStand.getX());
    buf.writeDouble(armorStand.getY());
    buf.writeDouble(armorStand.getZ());
    buf.writeFloat(armorStand.getYaw());
    buf.writeFloat(armorStand.getPitch());

    ServerPlayNetworking.send(player, NetworkPackets.CLIENT_UPDATE_PACKET, buf);
  }
}
