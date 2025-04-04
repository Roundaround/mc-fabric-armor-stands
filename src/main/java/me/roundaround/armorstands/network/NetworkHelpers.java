package me.roundaround.armorstands.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.EulerAngle;

public class NetworkHelpers {
  public static EulerAngle readEulerAngle(PacketByteBuf buf) {
    return new EulerAngle(buf.readFloat(), buf.readFloat(), buf.readFloat());
  }

  public static void writeEulerAngle(PacketByteBuf buf, EulerAngle eulerAngle) {
    buf.writeFloat(eulerAngle.pitch());
    buf.writeFloat(eulerAngle.yaw());
    buf.writeFloat(eulerAngle.roll());
  }
}
