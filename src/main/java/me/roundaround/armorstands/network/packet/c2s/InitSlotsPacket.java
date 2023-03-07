package me.roundaround.armorstands.network.packet.c2s;

import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class InitSlotsPacket {
  private final boolean fillSlots;

  public InitSlotsPacket(PacketByteBuf buf) {
    this.fillSlots = buf.readBoolean();
  }

  public InitSlotsPacket(boolean fillSlots) {
    this.fillSlots = fillSlots;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeBoolean(this.fillSlots);
    return buf;
  }

  private void handleOnServer(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketSender responseSender) {
    if (!(player.currentScreenHandler instanceof ArmorStandScreenHandler)) {
      return;
    }

    ArmorStandScreenHandler screenHandler = (ArmorStandScreenHandler) player.currentScreenHandler;
    screenHandler.initSlots(this.fillSlots);
  }

  public static void sendToServer(boolean fillSlots) {
    ClientPlayNetworking.send(NetworkPackets.INIT_SLOTS_PACKET, new InitSlotsPacket(fillSlots).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.INIT_SLOTS_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new InitSlotsPacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}