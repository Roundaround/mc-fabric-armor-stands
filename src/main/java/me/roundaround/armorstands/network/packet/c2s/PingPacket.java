package me.roundaround.armorstands.network.packet.c2s;

import java.util.UUID;

import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.network.packet.s2c.PongPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PingPacket {
  private final UUID playerUuid;

  private PingPacket(PacketByteBuf buf) {
    this.playerUuid = buf.readUuid();
  }

  private PingPacket(UUID playerUuid) {
    this.playerUuid = playerUuid;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeUuid(this.playerUuid);
    return buf;
  }

  private void handleOnServer(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketSender responseSender) {
    PongPacket.sendToClient(player);
  }

  public static void sendToServer(ClientPlayerEntity player) {
    ClientPlayNetworking.send(NetworkPackets.PING_PACKET,
        new PingPacket(player.getUuid()).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(NetworkPackets.PING_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new PingPacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
