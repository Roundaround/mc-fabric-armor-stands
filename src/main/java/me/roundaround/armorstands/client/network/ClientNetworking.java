package me.roundaround.armorstands.client.network;

import io.netty.buffer.Unpooled;
import me.roundaround.armorstands.client.gui.MessageRenderer;
import me.roundaround.armorstands.client.gui.MessageRenderer.HasMessageRenderer;
import me.roundaround.armorstands.client.util.LastUsedScreen;
import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.network.EulerAngleParameter;
import me.roundaround.armorstands.network.NetworkPackets;
import me.roundaround.armorstands.network.PosePart;
import me.roundaround.armorstands.network.UtilityAction;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.HasArmorStand;
import me.roundaround.armorstands.util.Pose;
import me.roundaround.armorstands.util.PosePreset;
import me.roundaround.armorstands.util.SavedPose;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
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
    ClientPlayNetworking.registerGlobalReceiver(
        NetworkPackets.MESSAGE_PACKET,
        ClientNetworking::handleMessagePacket);
  }

  public static void handleOpenScreenPacket(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    int syncId = buf.readInt();
    int armorStandId = buf.readInt();

    client.execute(() -> {
      ClientPlayerEntity player = client.player;
      Entity entity = player.getWorld().getEntityById(armorStandId);

      if (!(entity instanceof ArmorStandEntity)) {
        return;
      }

      ArmorStandEntity armorStand = (ArmorStandEntity) entity;
      ArmorStandScreenHandler screenHandler = new ArmorStandScreenHandler(
          syncId,
          player.getInventory(),
          armorStand);

      player.currentScreenHandler = screenHandler;
      client.setScreen(LastUsedScreen.get(screenHandler, armorStand));
    });
  }

  public static void handleClientUpdatePacket(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    double x = buf.readDouble();
    double y = buf.readDouble();
    double z = buf.readDouble();
    float yaw = buf.readFloat();
    float pitch = buf.readFloat();

    client.execute(() -> {
      if (!(client.player.currentScreenHandler instanceof HasArmorStand)) {
        return;
      }

      HasArmorStand screenHandler = (HasArmorStand) client.player.currentScreenHandler;
      ArmorStandEntity armorStand = screenHandler.getArmorStand();
      armorStand.setPos(x, y, z);
      armorStand.setYaw(yaw % 360f);
      armorStand.setPitch(pitch % 360f);
    });
  }

  private static void handleMessagePacket(
      MinecraftClient client,
      ClientPlayNetworkHandler handler,
      PacketByteBuf buf,
      PacketSender responseSender) {
    boolean translatable = buf.readBoolean();
    String message = buf.readString();
    boolean styled = buf.readBoolean();
    int color = styled ? buf.readInt() : -1;

    client.execute(() -> {
      if (!(client.currentScreen instanceof HasMessageRenderer)) {
        return;
      }

      MessageRenderer messageRenderer = ((HasMessageRenderer) client.currentScreen).getMessageRenderer();
      messageRenderer.addMessage(
          translatable
              ? Text.translatable(message)
              : Text.literal(message),
          styled
              ? color
              : MessageRenderer.BASE_COLOR);
    });
  }

  public static void sendInitSlotsPacket(boolean fillSlots) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeBoolean(fillSlots);

    ClientPlayNetworking.send(NetworkPackets.INIT_SLOTS_PACKET, buf);
  }

  public static void sendAdjustYawPacket(int amount) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(amount);

    ClientPlayNetworking.send(NetworkPackets.ADJUST_YAW_PACKET, buf);
  }

  public static void sendAdjustPosPacket(Direction direction, int pixels) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(direction.getId());
    buf.writeInt(pixels);

    ClientPlayNetworking.send(NetworkPackets.ADJUST_POS_PACKET, buf);
  }

  public static void sendUtilityActionPacket(UtilityAction action) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeString(action.toString());

    ClientPlayNetworking.send(NetworkPackets.UTILITY_ACTION_PACKET, buf);
  }

  public static void sendToggleFlagPacket(ArmorStandFlag flag) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeString(flag.toString());

    ClientPlayNetworking.send(NetworkPackets.TOGGLE_FLAG_PACKET, buf);
  }

  public static void sendSetFlagPacket(ArmorStandFlag flag, boolean value) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeString(flag.toString());
    buf.writeBoolean(value);

    ClientPlayNetworking.send(NetworkPackets.SET_FLAG_PACKET, buf);
  }

  public static void sendSetPosePacket(PosePreset pose) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeBoolean(true);
    buf.writeString(pose.toString());

    ClientPlayNetworking.send(NetworkPackets.SET_POSE_PACKET, buf);
  }

  public static void sendSetPosePacket(SavedPose pose) {
    sendSetPosePacket(pose.toPose());
  }

  public static void sendSetPosePacket(Pose pose) {
    sendSetPosePacket(
        pose.getHead(),
        pose.getBody(),
        pose.getRightArm(),
        pose.getLeftArm(),
        pose.getRightLeg(),
        pose.getLeftLeg());
  }

  public static void sendSetPosePacket(
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
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

  public static void sendAdjustPosePacket(
      PosePart part,
      EulerAngleParameter parameter,
      float value) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeString(part.toString());
    buf.writeString(parameter.toString());
    buf.writeFloat(value);

    ClientPlayNetworking.send(NetworkPackets.ADJUST_POSE_PACKET, buf);
  }

  public static void sendUndoPacket(boolean redo) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeBoolean(redo);

    ClientPlayNetworking.send(NetworkPackets.UNDO_PACKET, buf);
  }
}
