package me.roundaround.armorstands.network;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.mixin.ArmorStandEntityAccessor;
import me.roundaround.armorstands.util.MoveMode;
import me.roundaround.armorstands.util.MoveUnits;
import me.roundaround.armorstands.util.Pose;
import me.roundaround.armorstands.util.PosePreset;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;

import java.util.UUID;

public final class Networking {
  private Networking() {
  }

  public static final Identifier CLIENT_UPDATE_S2C = new Identifier(ArmorStandsMod.MOD_ID, "client_update_s2c");
  public static final Identifier MESSAGE_S2C = new Identifier(ArmorStandsMod.MOD_ID, "message_s2c");
  public static final Identifier PONG_S2C = new Identifier(ArmorStandsMod.MOD_ID, "pong_s2c");

  public static final Identifier ADJUST_POSE_C2S = new Identifier(ArmorStandsMod.MOD_ID, "adjust_pose_c2s");
  public static final Identifier ADJUST_POS_C2S = new Identifier(ArmorStandsMod.MOD_ID, "adjust_pos_c2s");
  public static final Identifier ADJUST_YAW_C2S = new Identifier(ArmorStandsMod.MOD_ID, "adjust_yaw_c2s");
  public static final Identifier PING_C2S = new Identifier(ArmorStandsMod.MOD_ID, "ping_c2s");
  public static final Identifier REQUEST_SCREEN_C2S = new Identifier(ArmorStandsMod.MOD_ID, "request_screen_c2s");
  public static final Identifier SET_FLAG_C2S = new Identifier(ArmorStandsMod.MOD_ID, "set_flag_c2s");
  public static final Identifier SET_POSE_C2S = new Identifier(ArmorStandsMod.MOD_ID, "set_pose_c2s");
  public static final Identifier SET_POSE_PRESET_C2S = new Identifier(ArmorStandsMod.MOD_ID, "set_pose_preset_c2s");
  public static final Identifier SET_SCALE_C2S = new Identifier(ArmorStandsMod.MOD_ID, "set_scale_c2s");
  public static final Identifier SET_YAW_C2S = new Identifier(ArmorStandsMod.MOD_ID, "set_yaw_c2s");
  public static final Identifier UNDO_C2S = new Identifier(ArmorStandsMod.MOD_ID, "undo_c2s");
  public static final Identifier UTILITY_ACTION_C2S = new Identifier(ArmorStandsMod.MOD_ID, "utility_action_c2s");

  public static void registerS2CPayloads() {
    PayloadTypeRegistry.playS2C().register(ClientUpdateS2C.ID, ClientUpdateS2C.CODEC);
    PayloadTypeRegistry.playS2C().register(MessageS2C.ID, MessageS2C.CODEC);
    PayloadTypeRegistry.playS2C().register(PongS2C.ID, PongS2C.CODEC);
  }

  public static void registerC2SPayloads() {
    PayloadTypeRegistry.playC2S().register(AdjustPoseC2S.ID, AdjustPoseC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(AdjustPosC2S.ID, AdjustPosC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(AdjustYawC2S.ID, AdjustYawC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(PingC2S.ID, PingC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(RequestScreenC2S.ID, RequestScreenC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(SetFlagC2S.ID, SetFlagC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(SetPoseC2S.ID, SetPoseC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(SetPosePresetC2S.ID, SetPosePresetC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(SetScaleC2S.ID, SetScaleC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(SetYawC2S.ID, SetYawC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(UndoC2S.ID, UndoC2S.CODEC);
    PayloadTypeRegistry.playC2S().register(UtilityActionC2S.ID, UtilityActionC2S.CODEC);
  }

  public record ClientUpdateS2C(double x, double y, double z, float yaw, float pitch, boolean invulnerable,
                                int disabledSlots) implements CustomPayload {
    public static final CustomPayload.Id<ClientUpdateS2C> ID = new CustomPayload.Id<>(CLIENT_UPDATE_S2C);
    public static final PacketCodec<RegistryByteBuf, ClientUpdateS2C> CODEC = PacketCodec.of(
        ClientUpdateS2C::write, ClientUpdateS2C::new);

    public ClientUpdateS2C(ArmorStandEntity armorStand) {
      this(armorStand.getX(), armorStand.getY(), armorStand.getZ(), armorStand.getYaw(), armorStand.getPitch(),
          armorStand.isInvulnerable(), ((ArmorStandEntityAccessor) armorStand).getDisabledSlots()
      );
    }

    private ClientUpdateS2C(PacketByteBuf buf) {
      this(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat(), buf.readBoolean(),
          buf.readInt()
      );
    }

    private void write(PacketByteBuf buf) {
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
      buf.writeBoolean(this.invulnerable);
      buf.writeInt(this.disabledSlots);
    }

    @Override
    public Id<ClientUpdateS2C> getId() {
      return ID;
    }
  }

  public record MessageS2C(boolean translatable, String message, boolean styled, int color) implements CustomPayload {
    public static final CustomPayload.Id<MessageS2C> ID = new CustomPayload.Id<>(MESSAGE_S2C);
    public static final PacketCodec<RegistryByteBuf, MessageS2C> CODEC = PacketCodec.tuple(PacketCodecs.BOOL,
        MessageS2C::translatable, PacketCodecs.STRING, MessageS2C::message, PacketCodecs.BOOL, MessageS2C::styled,
        PacketCodecs.INTEGER, MessageS2C::color, MessageS2C::new
    );

    public MessageS2C(String message) {
      this(true, message);
    }

    public MessageS2C(String message, int color) {
      this(true, message, color);
    }

    public MessageS2C(boolean translatable, String message) {
      this(translatable, message, false, -1);
    }

    public MessageS2C(boolean translatable, String message, int color) {
      this(translatable, message, true, color);
    }

    @Override
    public Id<MessageS2C> getId() {
      return ID;
    }
  }

  public record PongS2C(UUID playerUuid) implements CustomPayload {
    public static final CustomPayload.Id<PongS2C> ID = new CustomPayload.Id<>(PONG_S2C);
    public static final PacketCodec<RegistryByteBuf, PongS2C> CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, PongS2C::playerUuid, PongS2C::new);

    @Override
    public Id<PongS2C> getId() {
      return ID;
    }
  }

  public record AdjustPoseC2S(PosePart part, EulerAngleParameter parameter, float amount) implements CustomPayload {
    public static final CustomPayload.Id<AdjustPoseC2S> ID = new CustomPayload.Id<>(ADJUST_POSE_C2S);
    public static final PacketCodec<RegistryByteBuf, AdjustPoseC2S> CODEC = PacketCodec.tuple(PosePart.PACKET_CODEC,
        AdjustPoseC2S::part, EulerAngleParameter.PACKET_CODEC, AdjustPoseC2S::parameter, PacketCodecs.FLOAT,
        AdjustPoseC2S::amount, AdjustPoseC2S::new
    );

    @Override
    public Id<AdjustPoseC2S> getId() {
      return ID;
    }
  }

  public record AdjustPosC2S(Direction direction, int amount, MoveMode mode, MoveUnits units) implements CustomPayload {
    public static final CustomPayload.Id<AdjustPosC2S> ID = new CustomPayload.Id<>(ADJUST_POS_C2S);
    public static final PacketCodec<RegistryByteBuf, AdjustPosC2S> CODEC = PacketCodec.tuple(Direction.PACKET_CODEC,
        AdjustPosC2S::direction, PacketCodecs.INTEGER, AdjustPosC2S::amount, MoveMode.PACKET_CODEC, AdjustPosC2S::mode,
        MoveUnits.PACKET_CODEC, AdjustPosC2S::units, AdjustPosC2S::new
    );

    @Override
    public Id<AdjustPosC2S> getId() {
      return ID;
    }
  }

  public record AdjustYawC2S(int amount) implements CustomPayload {
    public static final CustomPayload.Id<AdjustYawC2S> ID = new CustomPayload.Id<>(ADJUST_YAW_C2S);
    public static final PacketCodec<RegistryByteBuf, AdjustYawC2S> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, AdjustYawC2S::amount, AdjustYawC2S::new);

    @Override
    public Id<AdjustYawC2S> getId() {
      return ID;
    }
  }

  public record PingC2S(UUID playerUuid) implements CustomPayload {
    public static final CustomPayload.Id<PingC2S> ID = new CustomPayload.Id<>(PING_C2S);
    public static final PacketCodec<RegistryByteBuf, PingC2S> CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, PingC2S::playerUuid, PingC2S::new);

    @Override
    public Id<PingC2S> getId() {
      return ID;
    }
  }

  public record RequestScreenC2S(int armorStandId, ScreenType screenType) implements CustomPayload {
    public static final CustomPayload.Id<RequestScreenC2S> ID = new CustomPayload.Id<>(REQUEST_SCREEN_C2S);
    public static final PacketCodec<RegistryByteBuf, RequestScreenC2S> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER,
        RequestScreenC2S::armorStandId, ScreenType.PACKET_CODEC, RequestScreenC2S::screenType, RequestScreenC2S::new
    );

    @Override
    public Id<RequestScreenC2S> getId() {
      return ID;
    }
  }

  public record SetFlagC2S(ArmorStandFlag flag, boolean value) implements CustomPayload {
    public static final CustomPayload.Id<SetFlagC2S> ID = new CustomPayload.Id<>(SET_FLAG_C2S);
    public static final PacketCodec<RegistryByteBuf, SetFlagC2S> CODEC = PacketCodec.tuple(
        ArmorStandFlag.PACKET_CODEC, SetFlagC2S::flag, PacketCodecs.BOOL, SetFlagC2S::value, SetFlagC2S::new);

    @Override
    public Id<SetFlagC2S> getId() {
      return ID;
    }
  }

  public record SetPoseC2S(EulerAngle head, EulerAngle body, EulerAngle rightArm, EulerAngle leftArm,
                           EulerAngle rightLeg, EulerAngle leftLeg) implements CustomPayload {
    public static final CustomPayload.Id<SetPoseC2S> ID = new CustomPayload.Id<>(SET_POSE_C2S);
    public static final PacketCodec<RegistryByteBuf, SetPoseC2S> CODEC = PacketCodec.of(
        SetPoseC2S::write, SetPoseC2S::new);

    public SetPoseC2S(Pose pose) {
      this(pose.getHead(), pose.getBody(), pose.getRightArm(), pose.getLeftArm(), pose.getRightLeg(),
          pose.getLeftLeg()
      );
    }

    private SetPoseC2S(PacketByteBuf buf) {
      this(NetworkHelpers.readEulerAngle(buf), NetworkHelpers.readEulerAngle(buf), NetworkHelpers.readEulerAngle(buf),
          NetworkHelpers.readEulerAngle(buf), NetworkHelpers.readEulerAngle(buf), NetworkHelpers.readEulerAngle(buf)
      );
    }

    private void write(PacketByteBuf buf) {
      NetworkHelpers.writeEulerAngle(buf, head);
      NetworkHelpers.writeEulerAngle(buf, body);
      NetworkHelpers.writeEulerAngle(buf, rightArm);
      NetworkHelpers.writeEulerAngle(buf, leftArm);
      NetworkHelpers.writeEulerAngle(buf, rightLeg);
      NetworkHelpers.writeEulerAngle(buf, leftLeg);
    }

    @Override
    public Id<SetPoseC2S> getId() {
      return ID;
    }
  }

  public record SetPosePresetC2S(PosePreset pose) implements CustomPayload {
    public static final CustomPayload.Id<SetPosePresetC2S> ID = new CustomPayload.Id<>(SET_POSE_PRESET_C2S);
    public static final PacketCodec<RegistryByteBuf, SetPosePresetC2S> CODEC = PacketCodec.tuple(
        PosePreset.PACKET_CODEC, SetPosePresetC2S::pose, SetPosePresetC2S::new);

    @Override
    public Id<SetPosePresetC2S> getId() {
      return ID;
    }
  }

  public record SetScaleC2S(float scale) implements CustomPayload {
    public static final CustomPayload.Id<SetScaleC2S> ID = new CustomPayload.Id<>(SET_SCALE_C2S);
    public static final PacketCodec<RegistryByteBuf, SetScaleC2S> CODEC = PacketCodec.tuple(
        PacketCodecs.FLOAT, SetScaleC2S::scale, SetScaleC2S::new);

    @Override
    public Id<SetScaleC2S> getId() {
      return ID;
    }
  }

  public record SetYawC2S(float angle) implements CustomPayload {
    public static final CustomPayload.Id<SetYawC2S> ID = new CustomPayload.Id<>(SET_YAW_C2S);
    public static final PacketCodec<RegistryByteBuf, SetYawC2S> CODEC = PacketCodec.tuple(
        PacketCodecs.FLOAT, SetYawC2S::angle, SetYawC2S::new);

    @Override
    public Id<SetYawC2S> getId() {
      return ID;
    }
  }

  public record UndoC2S(boolean redo) implements CustomPayload {
    public static final CustomPayload.Id<UndoC2S> ID = new CustomPayload.Id<>(UNDO_C2S);
    public static final PacketCodec<RegistryByteBuf, UndoC2S> CODEC = PacketCodec.tuple(
        PacketCodecs.BOOL, UndoC2S::redo, UndoC2S::new);

    @Override
    public Id<UndoC2S> getId() {
      return ID;
    }
  }

  public record UtilityActionC2S(UtilityAction action) implements CustomPayload {
    public static final CustomPayload.Id<UtilityActionC2S> ID = new CustomPayload.Id<>(UTILITY_ACTION_C2S);
    public static final PacketCodec<RegistryByteBuf, UtilityActionC2S> CODEC = PacketCodec.tuple(
        UtilityAction.PACKET_CODEC, UtilityActionC2S::action, UtilityActionC2S::new);

    @Override
    public Id<UtilityActionC2S> getId() {
      return ID;
    }
  }
}
