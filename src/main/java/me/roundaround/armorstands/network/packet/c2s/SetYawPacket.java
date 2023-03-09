package me.roundaround.armorstands.network.packet.c2s;

import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.HasArmorStandEditor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class SetYawPacket {
  private final float angle;

  public SetYawPacket(PacketByteBuf buf) {
    this.angle = buf.readFloat();
  }

  public SetYawPacket(float amount) {
    this.angle = amount;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeFloat(this.angle);
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
    editor.setRotation(this.angle);
  }

  public static void sendToServer(float amount) {
    ClientPlayNetworking.send(NetworkPackets.SET_YAW_PACKET, new SetYawPacket(amount).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.SET_YAW_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new SetYawPacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
