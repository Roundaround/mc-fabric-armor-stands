package me.roundaround.armorstands.network.packet.c2s;

import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.HasArmorStandEditor;
import me.roundaround.armorstands.util.PosePreset;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class SetPosePresetPacket {
  private final PosePreset pose;

  public SetPosePresetPacket(PacketByteBuf buf) {
    this.pose = PosePreset.fromString(buf.readString());
  }

  public SetPosePresetPacket(PosePreset pose) {
    this.pose = pose;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeString(this.pose.toString());
    return buf;
  }

  private void handleOnServer(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketSender responseSender) {
    if (!(player.currentScreenHandler instanceof HasArmorStandEditor)) {
      return;
    }

    HasArmorStandEditor screenHandler = (HasArmorStandEditor) player.currentScreenHandler;
    ArmorStandEditor editor = screenHandler.getEditor();
    editor.setPose(this.pose.toPose());
  }

  public static void sendToServer(PosePreset pose) {
    ClientPlayNetworking.send(
        NetworkPackets.SET_POSE_PRESET_PACKET,
        new SetPosePresetPacket(pose).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.SET_POSE_PRESET_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new SetPosePresetPacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
