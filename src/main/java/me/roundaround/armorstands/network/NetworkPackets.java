package me.roundaround.armorstands.network;

import me.roundaround.armorstands.ArmorStandsMod;
import net.minecraft.util.Identifier;

public class NetworkPackets {
  public static final Identifier ADJUST_YAW_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "adjust_yaw_packet");

  public static final Identifier TOGGLE_FLAG_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "toggle_flag_packet");

  public static final Identifier IDENTIFY_STAND_PACKET = new Identifier(
      ArmorStandsMod.MOD_ID,
      "identify_stand_packet");
}
