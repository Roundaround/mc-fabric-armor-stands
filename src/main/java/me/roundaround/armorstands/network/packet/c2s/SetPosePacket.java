package me.roundaround.armorstands.network.packet.c2s;

import me.roundaround.armorstands.network.NetworkHelpers;
import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.HasArmorStandEditor;
import me.roundaround.armorstands.util.Pose;
import me.roundaround.armorstands.util.SavedPose;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.EulerAngle;

public class SetPosePacket {
  private final EulerAngle head;
  private final EulerAngle body;
  private final EulerAngle rightArm;
  private final EulerAngle leftArm;
  private final EulerAngle rightLeg;
  private final EulerAngle leftLeg;

  public SetPosePacket(PacketByteBuf buf) {
    this.head = NetworkHelpers.readEulerAngle(buf);
    this.body = NetworkHelpers.readEulerAngle(buf);
    this.rightArm = NetworkHelpers.readEulerAngle(buf);
    this.leftArm = NetworkHelpers.readEulerAngle(buf);
    this.rightLeg = NetworkHelpers.readEulerAngle(buf);
    this.leftLeg = NetworkHelpers.readEulerAngle(buf);
  }

  public SetPosePacket(SavedPose pose) {
    this(pose.toPose());
  }

  public SetPosePacket(Pose pose) {
    this(
        pose.getHead(),
        pose.getBody(),
        pose.getRightArm(),
        pose.getLeftArm(),
        pose.getRightLeg(),
        pose.getLeftLeg());
  }

  public SetPosePacket(
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg) {
    this.head = head;
    this.body = body;
    this.rightArm = rightArm;
    this.leftArm = leftArm;
    this.rightLeg = rightLeg;
    this.leftLeg = leftLeg;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    NetworkHelpers.writeEulerAngle(buf, head);
    NetworkHelpers.writeEulerAngle(buf, body);
    NetworkHelpers.writeEulerAngle(buf, rightArm);
    NetworkHelpers.writeEulerAngle(buf, leftArm);
    NetworkHelpers.writeEulerAngle(buf, rightLeg);
    NetworkHelpers.writeEulerAngle(buf, leftLeg);
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
    editor.setPose(
        this.head,
        this.body,
        this.rightArm,
        this.leftArm,
        this.rightLeg,
        this.leftLeg);
  }

  public static void sendToServer(SavedPose pose) {
    ClientPlayNetworking.send(
        NetworkPackets.SET_POSE_PACKET,
        new SetPosePacket(pose).toPacket());
  }

  public static void sendToServer(Pose pose) {
    ClientPlayNetworking.send(
        NetworkPackets.SET_POSE_PACKET,
        new SetPosePacket(pose).toPacket());
  }

  public static void sendToServer(
      EulerAngle head,
      EulerAngle body,
      EulerAngle rightArm,
      EulerAngle leftArm,
      EulerAngle rightLeg,
      EulerAngle leftLeg) {
    ClientPlayNetworking.send(
        NetworkPackets.SET_POSE_PACKET,
        new SetPosePacket(head, body, rightArm, leftArm, rightLeg, leftLeg)
            .toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.SET_POSE_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new SetPosePacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
