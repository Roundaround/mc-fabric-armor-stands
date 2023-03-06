package me.roundaround.armorstands.network.packet.c2s;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public class InitSlotsPacket {
  private boolean fillSlots;

  public InitSlotsPacket(PacketByteBuf buf) {
    this.fillSlots = buf.readBoolean();
  }

  public InitSlotsPacket(boolean fillSlots) {
    this.fillSlots = fillSlots;
  }

  public boolean shouldFillSlots() {
    return fillSlots;
  }

  public PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeBoolean(fillSlots);
    return buf;
  }
}
