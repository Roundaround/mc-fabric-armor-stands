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

public class AdjustYawPacket {
  private final int amount;

  public AdjustYawPacket(PacketByteBuf buf) {
    this.amount = buf.readInt();
  }

  public AdjustYawPacket(int amount) {
    this.amount = amount;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeInt(this.amount);
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
    editor.rotate(this.amount);
  }

  public static void sendToServer(int amount) {
    ClientPlayNetworking.send(NetworkPackets.ADJUST_YAW_PACKET, new AdjustYawPacket(amount).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.ADJUST_YAW_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new AdjustYawPacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
