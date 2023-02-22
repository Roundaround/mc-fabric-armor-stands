package me.roundaround.armorstands.network;

import java.util.Arrays;

import me.roundaround.armorstands.ArmorStandsMod;
import me.roundaround.armorstands.util.ArmorStandEditor;
import me.roundaround.armorstands.util.ArmorStandHelper;
import me.roundaround.armorstands.util.Clipboard;
import me.roundaround.armorstands.util.actions.ArmorStandAction;
import me.roundaround.armorstands.util.actions.HoldingAction;
import me.roundaround.armorstands.util.actions.PrepareAction;
import me.roundaround.armorstands.util.actions.SnapToGroundAction;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public enum UtilityAction {
  COPY("copy"),
  PASTE("paste"),
  PREPARE("prepare"),
  PREPARE_SMALL("prepare_small"),
  FLOATING_ITEM("floating_item"),
  FLOATING_ITEM_SMALL("floating_item_small"),
  FLAT_ITEM("flat_item"),
  FLAT_ITEM_SMALL("flat_item_small"),
  BLOCK("block"),
  BLOCK_SMALL("block_small"),
  TOOL("tool"),
  TOOL_SMALL("tool_small"),
  SNAP_CORNER("snap_corner"),
  SNAP_CENTER("snap_center"),
  SNAP_STANDING("snap_standing"),
  SNAP_SITTING("snap_sitting"),
  SNAP_PLAYER("snap_player"),
  FACE_TOWARD("face_toward"),
  FACE_AWAY("face_away"),
  FACE_WITH("face_with"),
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
      case PREPARE:
      case PREPARE_SMALL:
        editor.applyAction(PrepareAction.create(armorStand, this == PREPARE_SMALL));
        break;
      case FLOATING_ITEM:
      case FLOATING_ITEM_SMALL:
        editor.applyAction(HoldingAction.floatingItem(armorStand, this == FLOATING_ITEM_SMALL));
        break;
      case FLAT_ITEM:
      case FLAT_ITEM_SMALL:
        editor.applyAction(HoldingAction.flatItem(armorStand, this == FLAT_ITEM_SMALL));
        break;
      case BLOCK:
      case BLOCK_SMALL:
        editor.applyAction(HoldingAction.block(armorStand, this == BLOCK_SMALL));
        break;
      case TOOL:
      case TOOL_SMALL:
        editor.applyAction(HoldingAction.tool(armorStand, this == TOOL_SMALL));
        break;
      case SNAP_CORNER:
        editor.setPos(ArmorStandHelper.getCornerPos(armorStand));
        break;
      case SNAP_CENTER:
        editor.setPos(ArmorStandHelper.getCenterPos(armorStand));
        break;
      case SNAP_STANDING:
      case SNAP_SITTING:
        editor.applyAction(SnapToGroundAction.create(armorStand, this == SNAP_SITTING));
        break;
      case SNAP_PLAYER:
        editor.setPos(player.getPos());
        break;
      case FACE_TOWARD:
        editor.setRotation(ArmorStandHelper.getLookYaw(armorStand, player.getEyePos()));
        break;
      case FACE_AWAY:
        editor.setRotation(180 + ArmorStandHelper.getLookYaw(armorStand, player.getEyePos()));
        break;
      case FACE_WITH:
        editor.setRotation(player.getYaw());
        break;
      case UNKNOWN:
      default:
        editor.applyAction(ArmorStandAction.noop());
    }
  }

  public UtilityAction forSmall(boolean small) {
    switch (this) {
      case PREPARE:
      case PREPARE_SMALL:
        return small ? PREPARE_SMALL : PREPARE;
      case FLOATING_ITEM:
      case FLOATING_ITEM_SMALL:
        return small ? FLOATING_ITEM_SMALL : FLOATING_ITEM;
      case FLAT_ITEM:
      case FLAT_ITEM_SMALL:
        return small ? FLAT_ITEM_SMALL : FLAT_ITEM;
      case BLOCK:
      case BLOCK_SMALL:
        return small ? BLOCK_SMALL : BLOCK;
      case TOOL:
      case TOOL_SMALL:
        return small ? TOOL_SMALL : TOOL;
      default:
        return this;
    }
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
