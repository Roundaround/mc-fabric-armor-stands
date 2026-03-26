package me.roundaround.armorstands.server.network;

import me.roundaround.armorstands.network.Networking;
import me.roundaround.armorstands.network.ScreenType;
import me.roundaround.armorstands.screen.ArmorStandScreenHandler;
import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.MoveMode;
import me.roundaround.armorstands.util.MoveUnits;
import me.roundaround.armorstands.util.actions.AdjustPosAction;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import java.util.function.Supplier;

public final class ServerNetworking {
  private ServerNetworking() {
  }

  public static void sendClientUpdatePacket(ServerPlayer player, ArmorStand armorStand) {
    if (ServerPlayNetworking.canSend(player, Networking.ClientUpdateS2C.ID)) {
      ServerPlayNetworking.send(player, new Networking.ClientUpdateS2C(armorStand));
    }
  }

  public static void sendMessagePacket(ServerPlayer player, String message) {
    if (ServerPlayNetworking.canSend(player, Networking.MessageS2C.ID)) {
      ServerPlayNetworking.send(player, new Networking.MessageS2C(message));
    }
  }

  public static void sendMessagePacket(ServerPlayer player, String message, int color) {
    if (ServerPlayNetworking.canSend(player, Networking.MessageS2C.ID)) {
      ServerPlayNetworking.send(player, new Networking.MessageS2C(message, color));
    }
  }

  public static void sendOpenScreenPacket(
      ServerPlayer player,
      int syncId,
      ArmorStand armorStand,
      ScreenType screenType
  ) {
    if (ServerPlayNetworking.canSend(player, Networking.OpenScreenS2C.ID)) {
      ServerPlayNetworking.send(player, new Networking.OpenScreenS2C(syncId, armorStand.getId(), screenType));
    }
  }

  public static void sendPongPacket(ServerPlayer player) {
    ServerPlayNetworking.send(player, new Networking.PongS2C(player.getUUID()));
  }

  public static void registerReceivers() {
    ServerPlayNetworking.registerGlobalReceiver(Networking.AdjustPoseC2S.ID, ServerNetworking::handleAdjustPose);
    ServerPlayNetworking.registerGlobalReceiver(Networking.AdjustPosC2S.ID, ServerNetworking::handleAdjustPos);
    ServerPlayNetworking.registerGlobalReceiver(Networking.AdjustYawC2S.ID, ServerNetworking::handleAdjustYaw);
    ServerPlayNetworking.registerGlobalReceiver(Networking.PingC2S.ID, ServerNetworking::handlePing);
    ServerPlayNetworking.registerGlobalReceiver(Networking.RequestScreenC2S.ID, ServerNetworking::handleRequestScreen);
    ServerPlayNetworking.registerGlobalReceiver(Networking.SetFlagC2S.ID, ServerNetworking::handleSetFlag);
    ServerPlayNetworking.registerGlobalReceiver(Networking.SetPoseC2S.ID, ServerNetworking::handleSetPose);
    ServerPlayNetworking.registerGlobalReceiver(Networking.SetPosePresetC2S.ID, ServerNetworking::handleSetPosePreset);
    ServerPlayNetworking.registerGlobalReceiver(Networking.SetScaleC2S.ID, ServerNetworking::handleSetScale);
    ServerPlayNetworking.registerGlobalReceiver(Networking.SetYawC2S.ID, ServerNetworking::handleSetYaw);
    ServerPlayNetworking.registerGlobalReceiver(Networking.UndoC2S.ID, ServerNetworking::handleUndo);
    ServerPlayNetworking.registerGlobalReceiver(Networking.UtilityActionC2S.ID, ServerNetworking::handleUtilityAction);
  }

  private static void handleAdjustPose(Networking.AdjustPoseC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      AbstractContainerMenu currentScreenHandler = context.player().containerMenu;
      if (!(currentScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
        return;
      }

      ArmorStandEditor editor = screenHandler.getEditor();
      editor.adjustPose(payload.part(), payload.parameter(), payload.amount());
    });
  }

  private static void handleAdjustPos(Networking.AdjustPosC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      AbstractContainerMenu currentScreenHandler = context.player().containerMenu;
      if (!(currentScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
        return;
      }

      ArmorStandEditor editor = screenHandler.getEditor();
      MoveMode mode = payload.mode();
      Direction direction = payload.direction();
      int amount = payload.amount();
      MoveUnits units = payload.units();

      editor.applyAction(mode.isLocal() ?
          AdjustPosAction.local(direction, amount, units, mode.isLocalToPlayer()) :
          AdjustPosAction.relative(direction, amount, units));
    });
  }

  private static void handleAdjustYaw(Networking.AdjustYawC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      AbstractContainerMenu currentScreenHandler = context.player().containerMenu;
      if (!(currentScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
        return;
      }

      ArmorStandEditor editor = screenHandler.getEditor();
      editor.rotate(payload.amount());
    });
  }

  private static void handlePing(Networking.PingC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      sendPongPacket(context.player());
    });
  }

  private static void handleRequestScreen(Networking.RequestScreenC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      ServerPlayer player = context.player();
      if (!(player.level().getEntity(payload.armorStandId()) instanceof ArmorStand armorStand)) {
        return;
      }

      player.armorstands$openScreen(armorStand, payload.screenType());
    });
  }

  private static void handleSetFlag(Networking.SetFlagC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      ServerPlayer player = context.player();
      AbstractContainerMenu currentScreenHandler = player.containerMenu;
      if (!(currentScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
        return;
      }

      ArmorStandEditor editor = screenHandler.getEditor();
      editor.setFlag(payload.flag(), payload.value());

      sendClientUpdatePacket(player, editor.getArmorStand());
    });
  }

  private static void handleSetPose(Networking.SetPoseC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      AbstractContainerMenu currentScreenHandler = context.player().containerMenu;
      if (!(currentScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
        return;
      }

      ArmorStandEditor editor = screenHandler.getEditor();
      editor.setPose(
          payload.head(),
          payload.body(),
          payload.rightArm(),
          payload.leftArm(),
          payload.rightLeg(),
          payload.leftLeg()
      );
    });
  }

  private static void handleSetPosePreset(Networking.SetPosePresetC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      AbstractContainerMenu currentScreenHandler = context.player().containerMenu;
      if (!(currentScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
        return;
      }

      ArmorStandEditor editor = screenHandler.getEditor();
      editor.setPose(payload.pose().toPose());
    });
  }

  private static void handleSetScale(Networking.SetScaleC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      AbstractContainerMenu currentScreenHandler = context.player().containerMenu;
      if (!(currentScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
        return;
      }

      ArmorStandEditor editor = screenHandler.getEditor();
      editor.setScale(payload.scale());
    });
  }

  private static void handleSetYaw(Networking.SetYawC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      AbstractContainerMenu currentScreenHandler = context.player().containerMenu;
      if (!(currentScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
        return;
      }

      ArmorStandEditor editor = screenHandler.getEditor();
      editor.setRotation(payload.angle());
    });
  }

  private static void handleUndo(Networking.UndoC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      ServerPlayer player = context.player();
      AbstractContainerMenu currentScreenHandler = player.containerMenu;
      if (!(currentScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
        return;
      }

      ArmorStandEditor editor = screenHandler.getEditor();
      Supplier<Boolean> action = payload.redo() ? editor::redo : editor::undo;
      String successMessage = payload.redo() ? "armorstands.message.redo" : "armorstands.message.undo";
      String failureMessage = payload.redo() ? "armorstands.message.redo.fail" : "armorstands.message.undo.fail";

      if (action.get()) {
        sendMessagePacket(player, successMessage);
      } else {
        sendMessagePacket(player, failureMessage);
      }
    });
  }

  private static void handleUtilityAction(Networking.UtilityActionC2S payload, ServerPlayNetworking.Context context) {
    context.server().execute(() -> {
      ServerPlayer player = context.player();
      AbstractContainerMenu currentScreenHandler = player.containerMenu;
      if (!(currentScreenHandler instanceof ArmorStandScreenHandler screenHandler)) {
        return;
      }

      ArmorStandEditor editor = screenHandler.getEditor();
      payload.action().apply(editor, player);
    });
  }
}
