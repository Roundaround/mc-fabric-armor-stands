package me.roundaround.armorstands.network;

import me.roundaround.armorstands.ArmorStandsMod;
import net.minecraft.util.Identifier;

public class NetworkPackets {
  public static final Identifier OPEN_SCREEN_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "open_screen_packet");

  public static final Identifier POPULATE_SLOTS_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "populate_slots_packet");

  public static final Identifier ADJUST_YAW_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "adjust_yaw_packet");

  public static final Identifier ADJUST_POS_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "adjust_pos_packet");

  public static final Identifier SNAP_POS_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "snap_pos_packet");

  public static final Identifier TOGGLE_FLAG_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "toggle_flag_packet");

  public static final Identifier SET_FLAG_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "set_flag_packet");
}
