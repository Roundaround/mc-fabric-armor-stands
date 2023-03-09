package me.roundaround.armorstands.network.packet.s2c;

import java.util.UUID;

import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.network.packet.NetworkPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class PongPacket {
  private final UUID playerUuid;

  private PongPacket(PacketByteBuf buf) {
    this.playerUuid = buf.readUuid();
  }

  private PongPacket(UUID playerUuid) {
    this.playerUuid = playerUuid;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeUuid(this.playerUuid);
    return buf;
  }

  private void handleOnClient(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketSender responseSender) {
    if (!(client.currentScreen instanceof AbstractArmorStandScreen)) {
      return;
    }

    ((AbstractArmorStandScreen) client.currentScreen).onPong();
  }

  public static void sendToClient(ServerPlayerEntity player) {
    ServerPlayNetworking.send(
        player,
        NetworkPackets.PONG_PACKET,
        new PongPacket(player.getUuid()).toPacket());
  }

  public static void registerClientReceiver() {
    ClientPlayNetworking.registerGlobalReceiver(
        NetworkPackets.PONG_PACKET,
        (client, handler, buf, responseSender) -> {
          PongPacket packet = new PongPacket(buf);
          client.execute(() -> {
            packet.handleOnClient(client, handler, responseSender);
          });
        });
  }
}
