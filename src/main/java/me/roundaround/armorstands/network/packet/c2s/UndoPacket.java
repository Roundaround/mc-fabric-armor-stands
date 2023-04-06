package me.roundaround.armorstands.network.packet.c2s;

import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.network.packet.s2c.MessagePacket;
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

import java.util.function.Supplier;

public class UndoPacket {
  private final boolean redo;

  public UndoPacket(PacketByteBuf buf) {
    this.redo = buf.readBoolean();
  }

  public UndoPacket(boolean redo) {
    this.redo = redo;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeBoolean(redo);
    return buf;
  }

  private void handleOnServer(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketSender responseSender) {
    if (!(player.currentScreenHandler instanceof HasArmorStandEditor screenHandler)) {
      return;
    }

    ArmorStandEditor editor = screenHandler.getEditor();

    Supplier<Boolean> action = this.redo ? editor::redo : editor::undo;
    String successMessage = this.redo ? "armorstands.message.redo" : "armorstands.message.undo";
    String failureMessage = this.redo ? "armorstands.message.redo.fail" : "armorstands.message.undo.fail";

    if (action.get()) {
      MessagePacket.sendToClient(player, successMessage);
    } else {
      MessagePacket.sendToClient(player, failureMessage);
    }
  }

  public static void sendToServer(boolean redo) {
    ClientPlayNetworking.send(
        NetworkPackets.UNDO_PACKET,
        new UndoPacket(redo).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.UNDO_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new UndoPacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
