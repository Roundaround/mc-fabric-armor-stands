package me.roundaround.armorstands.network.packet.c2s;

import me.roundaround.armorstands.network.ArmorStandFlag;
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

public class SetFlagPacket {
  private final ArmorStandFlag flag;
  private final boolean value;

  public SetFlagPacket(PacketByteBuf buf) {
    this.flag = ArmorStandFlag.fromString(buf.readString());
    this.value = buf.readBoolean();
  }

  public SetFlagPacket(ArmorStandFlag flag, boolean value) {
    this.flag = flag;
    this.value = value;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeString(this.flag.toString());
    buf.writeBoolean(this.value);
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
    editor.setFlag(this.flag, this.value);
  }

  public static void sendToServer(ArmorStandFlag flag, boolean value) {
    ClientPlayNetworking.send(NetworkPackets.SET_FLAG_PACKET, new SetFlagPacket(flag, value).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.SET_FLAG_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new SetFlagPacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
