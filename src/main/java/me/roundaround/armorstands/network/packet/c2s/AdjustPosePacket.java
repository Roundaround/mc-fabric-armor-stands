package me.roundaround.armorstands.network.packet.c2s;

import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.PosePart;
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

public class AdjustPosePacket {
  private final PosePart part;
  private final EulerAngleParameter parameter;
  private final float amount;

  public AdjustPosePacket(PacketByteBuf buf) {
    this.part = PosePart.fromString(buf.readString());
    this.parameter = EulerAngleParameter.fromString(buf.readString());
    this.amount = buf.readFloat();
  }

  public AdjustPosePacket(PosePart part, EulerAngleParameter parameter, float amount) {
    this.part = part;
    this.parameter = parameter;
    this.amount = amount;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeString(this.part.toString());
    buf.writeString(this.parameter.toString());
    buf.writeFloat(this.amount);
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
    editor.adjustPose(this.part, this.parameter, this.amount);
  }

  public static void sendToServer(PosePart part, EulerAngleParameter parameter, float amount) {
    ClientPlayNetworking.send(NetworkPackets.ADJUST_POSE_PACKET,
        new AdjustPosePacket(part, parameter, amount).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.ADJUST_POSE_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new AdjustPosePacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
