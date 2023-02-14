package me.roundaround.armorstands.client.network;

import io.netty.buffer.Unpooled;
import me.roundaround.armorstands.client.gui.ArmorStandState;
import me.roundaround.armorstands.client.gui.screen.ArmorStandUtilitiesScreen;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.NetworkPackets;
import me.roundaround.armorstands.network.PosePreset;
import me.roundaround.armorstands.network.UtilityAction;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;

public class ClientNetworking {
  public static void registerReceivers() {
    ClientPlayNetworking.registerGlobalReceiver(
        NetworkPackets.OPEN_SCREEN_PACKET,
        ClientNetworking::handleOpenScreenPacket);
    ClientPlayNetworking.registerGlobalReceiver(
        NetworkPackets.CLIENT_UPDATE_PACKET,
        ClientNetworking::handleClientUpdatePacket);
  }

  public static void handleOpenScreenPacket(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    int armorStandId = buf.readInt();
    int syncId = buf.readInt();

    client.execute(() -> {
      ClientPlayerEntity player = client.player;
      Entity entity = player.getWorld().getEntityById(armorStandId);

      if (!(entity instanceof ArmorStandEntity)) {
        return;
      }

      ArmorStandEntity armorStand = (ArmorStandEntity) entity;
      client.setScreen(new ArmorStandUtilitiesScreen(new ArmorStandState(client, armorStand, syncId)));
    });
  }

  public static void handleClientUpdatePacket(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    int armorStandId = buf.readInt();
    double x = buf.readDouble();
    double y = buf.readDouble();
    double z = buf.readDouble();
    float yaw = buf.readFloat();
    float pitch = buf.readFloat();

    client.execute(() -> {
      Entity entity = client.player.getWorld().getEntityById(armorStandId);
      if (!(entity instanceof ArmorStandEntity)) {
        return;
      }

      ArmorStandEntity armorStand = (ArmorStandEntity) entity;
      armorStand.setPos(x, y, z);
      armorStand.setYaw(yaw % 360f);
      armorStand.setPitch(pitch % 360f);
    });
  }

  public static void sendAdjustYawPacket(ArmorStandEntity armorStand, int amount) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(armorStand.getId());
    buf.writeInt(amount);

    ClientPlayNetworking.send(NetworkPackets.ADJUST_YAW_PACKET, buf);
  }

  public static void sendAdjustPosPacket(ArmorStandEntity armorStand, Direction direction, int pixels) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(armorStand.getId());
    buf.writeInt(direction.getId());
    buf.writeInt(pixels);

    ClientPlayNetworking.send(NetworkPackets.ADJUST_POS_PACKET, buf);
  }

  public static void sendUtilityActionPacket(ArmorStandEntity armorStand, UtilityAction action) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(armorStand.getId());
    buf.writeString(action.toString());

    ClientPlayNetworking.send(NetworkPackets.UTILITY_ACTION_PACKET, buf);
  }

  public static void sendToggleFlagPacket(ArmorStandEntity armorStand, ArmorStandFlag flag) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(armorStand.getId());
    buf.writeString(flag.toString());

    ClientPlayNetworking.send(NetworkPackets.TOGGLE_FLAG_PACKET, buf);
  }

  public static void sendSetFlagPacket(ArmorStandEntity armorStand, ArmorStandFlag flag, boolean value) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(armorStand.getId());
    buf.writeString(flag.toString());
    buf.writeBoolean(value);

    ClientPlayNetworking.send(NetworkPackets.SET_FLAG_PACKET, buf);
  }

  public static void sendSetPosePacket(ArmorStandEntity armorStand, PosePreset pose) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(armorStand.getId());
    buf.writeBoolean(true);
    buf.writeString(pose.toString());

    ClientPlayNetworking.send(NetworkPackets.SET_POSE_PACKET, buf);
  }

  public static void sendSetPosePacket(
      ArmorStandEntity armorStand,
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(armorStand.getId());
    buf.writeBoolean(false);
    writeEulerAngle(buf, head);
    writeEulerAngle(buf, body);
    writeEulerAngle(buf, rightArm);
    writeEulerAngle(buf, leftArm);
    writeEulerAngle(buf, rightLeg);
    writeEulerAngle(buf, leftLeg);

    ClientPlayNetworking.send(NetworkPackets.SET_POSE_PACKET, buf);
  }

  private static void writeEulerAngle(PacketByteBuf buf, EulerAngle eulerAngle) {
    buf.writeFloat(eulerAngle.getPitch());
    buf.writeFloat(eulerAngle.getYaw());
    buf.writeFloat(eulerAngle.getRoll());
  }

  public static void sendUndoPacket(ArmorStandEntity armorStand, boolean redo) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(armorStand.getId());
    buf.writeBoolean(redo);

    ClientPlayNetworking.send(NetworkPackets.UNDO_PACKET, buf);
  }

  public static void sendCreateScreenHandlerPacket(ArmorStandEntity armorStand, int syncId) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(armorStand.getId());
    buf.writeInt(syncId);

    ClientPlayNetworking.send(NetworkPackets.CREATE_SCREEN_HANDLER_PACKET, buf);
  }
}
