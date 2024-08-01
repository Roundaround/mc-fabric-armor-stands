package me.roundaround.armorstands.client.network;

import me.roundaround.armorstands.client.gui.MessageRenderer;
import me.roundaround.armorstands.client.gui.screen.AbstractArmorStandScreen;
import me.roundaround.armorstands.network.*;
import me.roundaround.armorstands.util.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public final class ClientNetworking {
  private ClientNetworking() {
  }

  public static void sendAdjustPosePacket(PosePart part, EulerAngleParameter parameter, float amount) {
    ClientPlayNetworking.send(new Networking.AdjustPoseC2S(part, parameter, amount));
  }

  public static void sendAdjustPosPacket(Direction direction, int amount, MoveMode mode, MoveUnits units) {
    ClientPlayNetworking.send(new Networking.AdjustPosC2S(direction, amount, mode, units));
  }

  public static void sendAdjustYawPacket(int amount) {
    ClientPlayNetworking.send(new Networking.AdjustYawC2S(amount));
  }

  public static void sendPingPacket(ClientPlayerEntity player) {
    if (ClientPlayNetworking.canSend(Networking.PingC2S.ID)) {
      ClientPlayNetworking.send(new Networking.PingC2S(player.getUuid()));
    }
  }

  public static void sendRequestScreenPacket(ArmorStandEntity armorStand, ScreenType screenType) {
    ClientPlayNetworking.send(new Networking.RequestScreenC2S(armorStand.getId(), screenType));
  }

  public static void sendSetFlagPacket(ArmorStandFlag flag, boolean value) {
    ClientPlayNetworking.send(new Networking.SetFlagC2S(flag, value));
  }

  public static void sendSetPosePacket(SavedPose pose) {
    sendSetPosePacket(pose.toPose());
  }

  public static void sendSetPosePacket(Pose pose) {
    ClientPlayNetworking.send(new Networking.SetPoseC2S(pose));
  }

  public static void sendSetPosePresetPacket(PosePreset pose) {
    ClientPlayNetworking.send(new Networking.SetPosePresetC2S(pose));
  }

  public static void sendSetScalePacket(float scale) {
    ClientPlayNetworking.send(new Networking.SetScaleC2S(scale));
  }

  public static void sendSetYawPacket(float angle) {
    ClientPlayNetworking.send(new Networking.SetYawC2S(angle));
  }

  public static void sendUndoPacket(boolean redo) {
    if (ClientPlayNetworking.canSend(Networking.UndoC2S.ID)) {
      ClientPlayNetworking.send(new Networking.UndoC2S(redo));
    }
  }

  public static void sendUtilityActionPacket(UtilityAction action) {
    ClientPlayNetworking.send(new Networking.UtilityActionC2S(action));
  }

  public static void registerReceivers() {
    ClientPlayNetworking.registerGlobalReceiver(Networking.ClientUpdateS2C.ID, ClientNetworking::handleClientUpdate);
    ClientPlayNetworking.registerGlobalReceiver(Networking.MessageS2C.ID, ClientNetworking::handleMessage);
    ClientPlayNetworking.registerGlobalReceiver(Networking.PongS2C.ID, ClientNetworking::handlePong);
  }

  private static void handleClientUpdate(Networking.ClientUpdateS2C payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> {
      Screen currentScreen = context.client().currentScreen;
      if (!(currentScreen instanceof AbstractArmorStandScreen screen)) {
        return;
      }

      screen.updatePosOnClient(payload.x(), payload.y(), payload.z());
      screen.updateScaleOnClient(payload.scale());
      screen.updateYawOnClient(MathHelper.wrapDegrees(payload.yaw()));
      screen.updatePitchOnClient(MathHelper.wrapDegrees(payload.pitch()));
      screen.updateInvulnerableOnClient(payload.invulnerable());
      screen.updateDisabledSlotsOnClient(payload.disabledSlots());
    });
  }

  private static void handleMessage(Networking.MessageS2C payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> {
      Screen currentScreen = context.client().currentScreen;
      if (!(currentScreen instanceof AbstractArmorStandScreen screen)) {
        return;
      }

      MessageRenderer messageRenderer = screen.getMessageRenderer();
      messageRenderer.addMessage(
          payload.translatable() ? Text.translatable(payload.message()) : Text.literal(payload.message()),
          payload.styled() ? payload.color() : MessageRenderer.BASE_COLOR
      );
    });
  }

  private static void handlePong(Networking.PongS2C payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> {
      Screen currentScreen = context.client().currentScreen;
      if (!(currentScreen instanceof AbstractArmorStandScreen screen)) {
        return;
      }

      screen.onPong();
    });
  }
}
