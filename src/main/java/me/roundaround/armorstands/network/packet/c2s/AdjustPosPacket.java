package me.roundaround.armorstands.network.packet.c2s;

import me.roundaround.armorstands.network.packet.NetworkPackets;
import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.HasArmorStandEditor;
import me.roundaround.armorstands.util.MoveMode;
import me.roundaround.armorstands.util.MoveUnits;
import me.roundaround.armorstands.util.actions.AdjustPosAction;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

public class AdjustPosPacket {
  private final Direction direction;
  private final int amount;
  private final MoveMode mode;
  private final MoveUnits units;

  public AdjustPosPacket(PacketByteBuf buf) {
    this.direction = Direction.byId(buf.readInt());
    this.amount = buf.readInt();
    this.mode = MoveMode.fromId(buf.readString());
    this.units = MoveUnits.fromId(buf.readString());
  }

  public AdjustPosPacket(Direction direction, int amount, MoveMode mode, MoveUnits units) {
    this.direction = direction;
    this.amount = amount;
    this.mode = mode;
    this.units = units;
  }

  private PacketByteBuf toPacket() {
    PacketByteBuf buf = new PacketByteBuf(PacketByteBufs.create());
    buf.writeInt(this.direction.getId());
    buf.writeInt(this.amount);
    buf.writeString(this.mode.getId());
    buf.writeString(this.units.getId());
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
    editor.applyAction(this.mode.isLocal()
        ? AdjustPosAction.local(this.direction, this.amount, this.units, this.mode.isLocalToPlayer())
        : AdjustPosAction.relative(this.direction, this.amount, this.units));
  }

  public static void sendToServer(Direction direction, int amount, MoveMode mode, MoveUnits units) {
    ClientPlayNetworking.send(NetworkPackets.ADJUST_POS_PACKET,
        new AdjustPosPacket(direction, amount, mode, units).toPacket());
  }

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(
        NetworkPackets.ADJUST_POS_PACKET,
        (server, player, handler, buf, responseSender) -> {
          new AdjustPosPacket(buf).handleOnServer(server, player, handler, responseSender);
        });
  }
}
