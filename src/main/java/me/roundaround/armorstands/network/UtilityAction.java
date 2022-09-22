package me.roundaround.armorstands.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.ArmorStandHelper;
import me.roundaround.armorstands.util.Clipboard;
import me.roundaround.armorstands.util.actions.ArmorStandAction;
import me.roundaround.armorstands.util.actions.ComboAction;
import me.roundaround.armorstands.util.actions.FlagAction;
import me.roundaround.armorstands.util.actions.MoveAction;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public enum UtilityAction {
  COPY("copy"),
  PASTE("paste"),
  CHARACTER("character"),
  SNAP_CORNER("snap_corner"),
  SNAP_CENTER("snap_center"),
  SNAP_STANDING("snap_standing"),
  SNAP_SITTING("snap_sitting"),
  SNAP_PLAYER("snap_player"),
  UNKNOWN("unknown");

  private final String id;

  private UtilityAction(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }

  public void apply(ArmorStandEditor editor, ServerPlayerEntity player) {
    ArmorStandEntity armorStand = editor.getArmorStand();

    switch (this) {
      case COPY:
        Clipboard.copy(player, armorStand);
        break;
      case PASTE:
        Clipboard.paste(player, editor);
        break;
      case CHARACTER:
        applyCharacter(editor, armorStand, player);
        break;
      case SNAP_CORNER:
        editor.setPos(ArmorStandHelper.getCornerPos(armorStand));
        break;
      case SNAP_CENTER:
        editor.setPos(ArmorStandHelper.getCenterPos(armorStand));
        break;
      case SNAP_STANDING:
      case SNAP_SITTING:
        applySnapGround(editor, armorStand, player);
        break;
      case SNAP_PLAYER:
        editor.setPos(player.getPos());
        break;
      case UNKNOWN:
      default:
        editor.applyAction(ArmorStandAction.noop());
    }
  }

  private void applyCharacter(ArmorStandEditor editor, ArmorStandEntity armorStand, ServerPlayerEntity player) {
    ArrayList<ArmorStandAction> actions = new ArrayList<>();
    actions.add(FlagAction.set(ArmorStandFlag.ARMS, true));
    actions.add(FlagAction.set(ArmorStandFlag.BASE, true));
    actions.add(FlagAction.set(ArmorStandFlag.GRAVITY, true));

    Optional<Vec3d> maybeGround = ArmorStandHelper.getStandingPos(armorStand, false);
    if (maybeGround.isPresent()) {
      actions.add(MoveAction.absolute(maybeGround.get()));
    }

    editor.applyAction(ComboAction.of(actions));
  }

  private void applySnapGround(ArmorStandEditor editor, ArmorStandEntity armorStand, ServerPlayerEntity player) {
    ArrayList<ArmorStandAction> actions = new ArrayList<>();
    actions.add(FlagAction.set(ArmorStandFlag.GRAVITY, true));

    Optional<Vec3d> maybeGround = ArmorStandHelper.getGroundPos(armorStand, this.equals(SNAP_SITTING));
    if (maybeGround.isPresent()) {
      actions.add(MoveAction.absolute(maybeGround.get()));
    }

    editor.applyAction(ComboAction.of(actions));
  }

  public static UtilityAction fromString(String value) {
    return Arrays.stream(UtilityAction.values())
        .filter((action) -> action.id.equals(value))
        .findFirst()
        .orElseGet(() -> {
          ArmorStandsMod.LOGGER.warn("Unknown id '{}'. Defaulting to UNKNOWN.", value);
          return UNKNOWN;
        });
  }
}
