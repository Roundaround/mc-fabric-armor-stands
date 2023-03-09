package me.roundaround.armorstands.network.packet;

import me.roundaround.armorstands.ArmorStandsMod;
import net.minecraft.util.Identifier;

public class NetworkPackets {
  public static final Identifier OPEN_SCREEN_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "open_screen_packet");

  public static final Identifier CLIENT_UPDATE_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "client_update_packet");

  public static final Identifier MESSAGE_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "message_packet");

  public static final Identifier INIT_SLOTS_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "init_slots_packet");

  public static final Identifier SET_FLAG_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "set_flag_packet");

  public static final Identifier ADJUST_YAW_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "adjust_yaw_packet");

  public static final Identifier SET_YAW_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "set_yaw_packet");

  public static final Identifier ADJUST_POS_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "adjust_pos_packet");

  public static final Identifier UTILITY_ACTION_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "utility_action_packet");

  public static final Identifier SET_POSE_PRESET_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "set_pose_preset_packet");

  public static final Identifier SET_POSE_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "set_pose_packet");

  public static final Identifier ADJUST_POSE_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "adjust_pose_packet");

  public static final Identifier UNDO_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "undo_packet");
}
