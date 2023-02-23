package me.roundaround.armorstands.util;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import me.roundaround.armorstands.server.network.ServerNetworking;
import me.roundaround.armorstands.util.actions.ClipboardPasteAction;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class Clipboard {
  private static final HashMap<UUID, Entry> entries = new HashMap<>();

  public static void copy(ServerPlayerEntity player, ArmorStandEntity armorStand) {
    entries.put(player.getUuid(), Entry.everything(armorStand));
    ServerNetworking.sendMessagePacket(player, "armorstands.message.copy");
  }

  public static boolean paste(ServerPlayerEntity player, ArmorStandEditor editor) {
    if (!entries.containsKey(player.getUuid())) {
      return false;
    }

    editor.applyAction(ClipboardPasteAction.create(entries.get(player.getUuid())));
    ServerNetworking.sendMessagePacket(player, "armorstands.message.paste");
    return true;
  }

  public static void remove(ServerPlayerEntity player) {
    entries.remove(player.getUuid());
  }

  // Use all Optionals so that we can do partial copies if we want
  public static class Entry implements ArmorStandApplyable {
    private Optional<Pose> pose;
    private Optional<FlagSnapshot> flags;

    private Entry(Optional<Pose> pose, Optional<FlagSnapshot> flags) {
      this.pose = pose;
      this.flags = flags;
    }

    public static Entry everything(ArmorStandEntity armorStand) {
      return new Entry(Optional.of(new Pose(armorStand)), Optional.of(FlagSnapshot.all(armorStand)));
    }

    public static Entry poseOnly(ArmorStandEntity armorStand) {
      return new Entry(Optional.of(new Pose(armorStand)), Optional.empty());
    }

    public static Entry flagsOnly(ArmorStandEntity armorStand) {
      return new Entry(Optional.empty(), Optional.of(FlagSnapshot.all(armorStand)));
    }

    @Override
    public void apply(PlayerEntity player, ArmorStandEntity armorStand) {
      pose.ifPresent((pose) -> pose.apply(player, armorStand));
      flags.ifPresent((flags) -> flags.apply(player, armorStand));
    }
  }
}
