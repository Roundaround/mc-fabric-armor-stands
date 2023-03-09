package me.roundaround.armorstands.network.packet.s2c;

import me.roundaround.armorstands.client.gui.screen.ScreenFactory;
import me.roundaround.armorstands.client.util.LastUsedScreen;
import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class OpenScreenPacket {
  private final int syncId;
  private final int armorStandId;

  public OpenScreenPacket(PacketByteBuf buf) {
    this.syncId = buf.readInt();
    this.armorStandId = buf.readInt();
  }

  public OpenScreenPacket(int syncId, ArmorStandEntity armorStand) {
    this.syncId = syncId;
    this.armorStandId = armorStand.getId();
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeInt(this.syncId);
    buf.writeInt(this.armorStandId);
    return buf;
  }

  private void handleOnClient(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketSender responseSender) {
    ClientPlayerEntity player = client.player;
    Entity entity = player.getWorld().getEntityById(this.armorStandId);

    if (!(entity instanceof ArmorStandEntity)) {
      return;
    }

    ArmorStandEntity armorStand = (ArmorStandEntity) entity;
    ArmorStandScreenHandler screenHandler = new ArmorStandScreenHandler(
        this.syncId,
        player.getInventory(),
        armorStand);

    player.currentScreenHandler = screenHandler;

    ScreenFactory screenFactory = LastUsedScreen.get(armorStand, ScreenFactory.UTILITIES);
    LastUsedScreen.set(screenFactory, armorStand);
    client.setScreen(screenFactory.construct(screenHandler, armorStand));
  }

  public static void sendToClient(ServerPlayerEntity player, int syncId, ArmorStandEntity armorStand) {
    ServerPlayNetworking.send(
        player,
        NetworkPackets.OPEN_SCREEN_PACKET,
        new OpenScreenPacket(syncId, armorStand).toPacket());
  }

  public static void registerClientReceiver() {
    ClientPlayNetworking.registerGlobalReceiver(
        NetworkPackets.OPEN_SCREEN_PACKET,
        (client, handler, buf, responseSender) -> {
          OpenScreenPacket packet = new OpenScreenPacket(buf);
          client.execute(() -> {
            packet.handleOnClient(client, handler, responseSender);
          });
        });
  }
}
