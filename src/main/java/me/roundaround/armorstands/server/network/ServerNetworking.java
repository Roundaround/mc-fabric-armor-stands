package me.roundaround.armorstands.server.network;

import io.netty.buffer.Unpooled;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.NetworkPackets;
import me.roundaround.armorstands.network.PosePreset;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.HasArmorStandEditor;
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
        NetworkPackets.INIT_SLOTS_PACKET,
        ServerNetworking::handleInitSlotsPacket);
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
  }

  private static EulerAngle readEulerAngle(PacketByteBuf buf) {
    return new EulerAngle(buf.readFloat(), buf.readFloat(), buf.readFloat());
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

  private static void handleAdjustYawPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    int amount = buf.readInt();

    if (!(player.currentScreenHandler instanceof HasArmorStandEditor)) {
      return;
    }

    HasArmorStandEditor screenHandler = (HasArmorStandEditor) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    editor.rotate(amount);
  }

  private static void handleAdjustPosPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    Direction direction = Direction.byId(buf.readInt());
    int pixels = buf.readInt();

    if (!(player.currentScreenHandler instanceof HasArmorStandEditor)) {
      return;
    }

    HasArmorStandEditor screenHandler = (HasArmorStandEditor) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    editor.movePos(direction, pixels);
  }

  private static void handleUtilityActionPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    UtilityAction action = UtilityAction.fromString(buf.readString());

    if (!(player.currentScreenHandler instanceof HasArmorStandEditor)) {
      return;
    }

    HasArmorStandEditor screenHandler = (HasArmorStandEditor) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    action.apply(editor, player);
  }

  private static void handleToggleFlagPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    ArmorStandFlag flag = ArmorStandFlag.fromString(buf.readString());

    if (!(player.currentScreenHandler instanceof HasArmorStandEditor)) {
      return;
    }

    HasArmorStandEditor screenHandler = (HasArmorStandEditor) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    editor.toggleFlag(flag);
  }

  private static void handleSetFlagPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    ArmorStandFlag flag = ArmorStandFlag.fromString(buf.readString());
    boolean value = buf.readBoolean();

    if (!(player.currentScreenHandler instanceof HasArmorStandEditor)) {
      return;
    }

    HasArmorStandEditor screenHandler = (HasArmorStandEditor) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    editor.setFlag(flag, value);
  }

  private static void handleSetPosePacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    boolean isPreset = buf.readBoolean();

    if (!(player.currentScreenHandler instanceof HasArmorStandEditor)) {
      return;
    }

    HasArmorStandEditor screenHandler = (HasArmorStandEditor) player.currentScreenHandler;
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

  private static void handleUndoPacket(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    boolean redo = buf.readBoolean();

    if (!(player.currentScreenHandler instanceof HasArmorStandEditor)) {
      return;
    }

    HasArmorStandEditor screenHandler = (HasArmorStandEditor) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    if (redo) {
      editor.redo();
      return;
    }
    editor.undo();
  }

  public static void sendOpenScreenPacket(ServerPlayerEntity player, ArmorStandEntity armorStand, int syncId) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(syncId);
    buf.writeInt(armorStand.getId());

    ServerPlayNetworking.send(player, NetworkPackets.OPEN_SCREEN_PACKET, buf);
  }

  public static void sendClientUpdatePacket(ServerPlayerEntity player, ArmorStandEntity armorStand) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeDouble(armorStand.getX());
    buf.writeDouble(armorStand.getY());
    buf.writeDouble(armorStand.getZ());
    buf.writeFloat(armorStand.getYaw());
    buf.writeFloat(armorStand.getPitch());

    ServerPlayNetworking.send(player, NetworkPackets.CLIENT_UPDATE_PACKET, buf);
  }
}
