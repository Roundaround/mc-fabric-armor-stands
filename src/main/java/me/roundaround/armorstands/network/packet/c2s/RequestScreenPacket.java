package me.roundaround.armorstands.network.packet.c2s;

import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.LastUsedScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class RequestScreenPacket {
  private final int armorStandId;
  private final ScreenType screenType;

  private RequestScreenPacket(PacketByteBuf buf) {
    this.armorStandId = buf.readInt();
    this.screenType = ScreenType.fromId(buf.readString());
  }

  private RequestScreenPacket(ArmorStandEntity armorStand, ScreenType screenType) {
    this.armorStandId = armorStand.getId();
    this.screenType = screenType;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeInt(this.armorStandId);
    buf.writeString(this.screenType.getId());
    return buf;
  }

  private void handleOnServer(
      MinecraftServer server,
      ServerPlayerEntity player,
      ServerPlayNetworkHandler handler,
      PacketSender responseSender) {
    ArmorStandEntity armorStand = (ArmorStandEntity) player.world.getEntityById(this.armorStandId);

    if (armorStand == null) {
      return;
    }

    if (player.currentScreenHandler instanceof ArmorStandScreenHandler) {
      // Bypass the normal screen closing logic, as we don't want to send a
      // close packet to the client.
      player.onHandledScreenClosed();
    }

    LastUsedScreen.set(player, armorStand, this.screenType);
    player.openHandledScreen(ArmorStandScreenHandler.Factory.create(this.screenType, armorStand));
  }

  public static void sendToServer(ArmorStandEntity armorStand, ScreenType screenType) {
    ClientPlayNetworking.send(NetworkPackets.REQUEST_SCREEN_PACKET,
        new RequestScreenPacket(armorStand, screenType).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(NetworkPackets.REQUEST_SCREEN_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new RequestScreenPacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
