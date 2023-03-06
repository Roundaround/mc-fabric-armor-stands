package me.roundaround.armorstands.network.packet.c2s;

import me.roundaround.armorstands.network.UtilityAction;
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

public class UtilityActionPacket {
  private final UtilityAction action;

  public UtilityActionPacket(PacketByteBuf buf) {
    this.action = UtilityAction.fromString(buf.readString());
  }

  public UtilityActionPacket(UtilityAction action) {
    this.action = action;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeString(action.toString());
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
    this.action.apply(editor, player);
  }

  public static void sendToServer(UtilityAction action) {
    ClientPlayNetworking.send(NetworkPackets.UTILITY_ACTION_PACKET, new UtilityActionPacket(action).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.UTILITY_ACTION_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new UtilityActionPacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
