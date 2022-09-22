package me.roundaround.armorstands.util;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import me.roundaround.armorstands.util.actions.ClipboardPasteAction;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class Clipboard {
  private static final HashMap<UUID, Entry> entries = new HashMap<>();

  public static void copy(ServerPlayerEntity player, ArmorStandEntity armorStand) {
    entries.put(player.getUuid(), Entry.everything(armorStand));
  }

  public static boolean paste(ServerPlayerEntity player, ArmorStandEditor editor) {
    if (!entries.containsKey(player.getUuid())) {
      return false;
    }

    Entry entry = entries.get(player.getUuid());
    remove(player);
    editor.applyAction(ClipboardPasteAction.create(entry));
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
    public void apply(ArmorStandEntity armorStand) {
      pose.ifPresent((pose) -> pose.apply(armorStand));
      flags.ifPresent((flags) -> flags.apply(armorStand));
    }
  }
}
