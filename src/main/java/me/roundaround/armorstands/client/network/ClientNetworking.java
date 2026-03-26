package me.roundaround.armorstands.client.network;

import me.roundaround.armorstands.client.gui.MessageRenderer;
import me.roundaround.armorstands.client.gui.screen.*;
import me.roundaround.armorstands.network.*;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;

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

  public static void sendPingPacket(LocalPlayer player) {
    if (ClientPlayNetworking.canSend(Networking.PingC2S.ID)) {
      ClientPlayNetworking.send(new Networking.PingC2S(player.getUUID()));
    }
  }

  public static void sendRequestScreenPacket(ArmorStand armorStand, ScreenType screenType) {
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
    ClientPlayNetworking.registerGlobalReceiver(Networking.OpenScreenS2C.ID, ClientNetworking::handleOpenScreen);
    ClientPlayNetworking.registerGlobalReceiver(Networking.PongS2C.ID, ClientNetworking::handlePong);
  }

  private static void handleClientUpdate(Networking.ClientUpdateS2C payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> {
      Screen currentScreen = context.client().screen;
      if (!(currentScreen instanceof AbstractArmorStandScreen screen)) {
        return;
      }

      screen.updatePosOnClient(payload.x(), payload.y(), payload.z());
      screen.updateYawOnClient(Mth.wrapDegrees(payload.yaw()));
      screen.updatePitchOnClient(Mth.wrapDegrees(payload.pitch()));
      screen.updateInvulnerableOnClient(payload.invulnerable());
      screen.updateDisabledSlotsOnClient(payload.disabledSlots());
    });
  }

  private static void handleMessage(Networking.MessageS2C payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> {
      Screen currentScreen = context.client().screen;
      if (!(currentScreen instanceof AbstractArmorStandScreen screen)) {
        return;
      }

      MessageRenderer messageRenderer = screen.getMessageRenderer();
      messageRenderer.addMessage(
          payload.translatable() ? Component.translatable(payload.message()) : Component.literal(payload.message()),
          payload.styled() ? payload.color() : MessageRenderer.BASE_COLOR
      );
    });
  }

  private static void handleOpenScreen(Networking.OpenScreenS2C payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> {
      LocalPlayer player = context.player();
      if (!(player.level().getEntity(payload.armorStandId()) instanceof ArmorStand armorStand)) {
        return;
      }

      ArmorStandScreenHandler screenHandler = new ArmorStandScreenHandler(payload.syncId(), player.getInventory(),
          armorStand, payload.screenType()
      );
      player.containerMenu = screenHandler;
      context.client().setScreen(switch (screenHandler.getScreenType()) {
        case UTILITIES -> new ArmorStandUtilitiesScreen(screenHandler);
        case MOVE -> new ArmorStandMoveScreen(screenHandler);
        case ROTATE -> new ArmorStandRotateScreen(screenHandler);
        case POSE -> new ArmorStandPoseScreen(screenHandler);
        case PRESETS -> new ArmorStandPresetsScreen(screenHandler);
        case INVENTORY -> new ArmorStandInventoryScreen(screenHandler);
      });
    });
  }

  private static void handlePong(Networking.PongS2C payload, ClientPlayNetworking.Context context) {
    context.client().execute(() -> {
      Screen currentScreen = context.client().screen;
      if (!(currentScreen instanceof AbstractArmorStandScreen screen)) {
        return;
      }

      screen.onPong();
    });
  }
}
