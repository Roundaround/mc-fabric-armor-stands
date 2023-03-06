package me.roundaround.armorstands.network.packet.s2c;

import me.roundaround.armorstands.client.gui.MessageRenderer;
import me.roundaround.armorstands.client.gui.MessageRenderer.HasMessageRenderer;
import me.roundaround.armorstands.network.packet.NetworkPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MessagePacket {
  private final boolean translatable;
  private final String message;
  private final boolean styled;
  private final int color;

  public MessagePacket(PacketByteBuf buf) {
    this.translatable = buf.readBoolean();
    this.message = buf.readString();
    this.styled = buf.readBoolean();
    this.color = this.styled ? buf.readInt() : -1;
  }

  public MessagePacket(String message) {
    this(true, message);
  }

  public MessagePacket(String message, int color) {
    this(true, message, color);
  }

  public MessagePacket(boolean translatable, String message) {
    this(translatable, message, false, -1);
  }

  public MessagePacket(boolean translatable, String message, int color) {
    this(translatable, message, true, color);
  }

  public MessagePacket(boolean translatable, String message, boolean styled, int color) {
    this.translatable = translatable;
    this.message = message;
    this.styled = styled;
    this.color = color;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeBoolean(this.styled);
    buf.writeString(this.message);
    buf.writeBoolean(this.styled);
    if (this.styled) {
      buf.writeInt(this.color);
    }
    return buf;
  }

  private void handleOnClient(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketSender responseSender) {
    if (!(client.currentScreen instanceof HasMessageRenderer)) {
      return;
    }

    MessageRenderer messageRenderer = ((HasMessageRenderer) client.currentScreen).getMessageRenderer();
    messageRenderer.addMessage(
        this.translatable
            ? Text.translatable(this.message)
            : Text.literal(this.message),
        this.styled
            ? this.color
            : MessageRenderer.BASE_COLOR);
  }

  public static void sendToClient(ServerPlayerEntity player, String message) {
    ServerPlayNetworking.send(
        player,
        NetworkPackets.CLIENT_UPDATE_PACKET,
        new MessagePacket(message).toPacket());
  }

  public static void sendToClient(ServerPlayerEntity player, String message, int color) {
    ServerPlayNetworking.send(
        player,
        NetworkPackets.CLIENT_UPDATE_PACKET,
        new MessagePacket(message, color).toPacket());
  }

  public static void registerClientReceiver() {
    ClientPlayNetworking.registerGlobalReceiver(
        NetworkPackets.CLIENT_UPDATE_PACKET,
        (client, handler, buf, responseSender) -> {
          MessagePacket packet = new MessagePacket(buf);
          client.execute(() -> {
            packet.handleOnClient(client, handler, responseSender);
          });
        });
  }
}
