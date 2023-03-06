package me.roundaround.armorstands.network.packet.s2c;

import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.util.HasArmorStand;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ClientUpdatePacket {
  private final double x;
  private final double y;
  private final double z;
  private final float yaw;
  private final float pitch;
  private final boolean invulnerable;

  private ClientUpdatePacket(PacketByteBuf buf) {
    this.x = buf.readDouble();
    this.y = buf.readDouble();
    this.z = buf.readDouble();
    this.yaw = buf.readFloat();
    this.pitch = buf.readFloat();
    this.invulnerable = buf.readBoolean();
  }

  public ClientUpdatePacket(ArmorStandEntity armorStand) {
    this.x = armorStand.getX();
    this.y = armorStand.getY();
    this.z = armorStand.getZ();
    this.yaw = armorStand.getYaw();
    this.pitch = armorStand.getPitch();
    this.invulnerable = armorStand.isInvulnerable();
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeDouble(this.x);
    buf.writeDouble(this.y);
    buf.writeDouble(this.z);
    buf.writeFloat(this.yaw);
    buf.writeFloat(this.pitch);
    buf.writeBoolean(this.invulnerable);
    return buf;
  }

  private void handleOnClient(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketSender responseSender) {
    if (!(client.player.currentScreenHandler instanceof HasArmorStand)) {
      return;
    }

    HasArmorStand screenHandler = (HasArmorStand) client.player.currentScreenHandler;
    ArmorStandEntity armorStand = screenHandler.getArmorStand();
    armorStand.setPos(this.x, this.y, this.z);
    armorStand.setYaw(this.yaw % 360f);
    armorStand.setPitch(this.pitch % 360f);
    armorStand.setInvulnerable(this.invulnerable);
  }

  public static void sendToClient(ServerPlayerEntity player, ArmorStandEntity armorStand) {
    ServerPlayNetworking.send(
        player,
        NetworkPackets.CLIENT_UPDATE_PACKET,
        new ClientUpdatePacket(armorStand).toPacket());
  }

  public static void registerClientReceiver() {
    ClientPlayNetworking.registerGlobalReceiver(
        NetworkPackets.CLIENT_UPDATE_PACKET,
        (client, handler, buf, responseSender) -> {
          ClientUpdatePacket packet = new ClientUpdatePacket(buf);
          client.execute(() -> {
            packet.handleOnClient(client, handler, responseSender);
          });
        });
  }
}
