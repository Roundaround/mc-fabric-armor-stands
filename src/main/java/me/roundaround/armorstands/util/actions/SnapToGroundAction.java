package me.roundaround.armorstands.util.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.util.ArmorStandHelper;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class SnapToGroundAction extends ComboAction {
  private SnapToGroundAction(Collection<ArmorStandAction> actions) {
    super(Text.translatable("armorstands.action.snap"), actions);
  }

  public static SnapToGroundAction create(ArmorStandEntity armorStand, boolean sitting) {
    ArrayList<ArmorStandAction> actions = new ArrayList<>();
    actions.add(FlagAction.set(ArmorStandFlag.GRAVITY, true));

    Optional<Vec3d> maybeGround = ArmorStandHelper.getGroundPos(armorStand, sitting);
    if (maybeGround.isPresent()) {
      actions.add(MoveAction.absolute(maybeGround.get()));
    }

    return new SnapToGroundAction(actions);
  }

  @Deprecated
  public static SnapToGroundAction of(Collection<ArmorStandAction> actions) {
    throw new UnsupportedOperationException("Please use SnapToGroundAction.create");
  }

  @Deprecated
  public static SnapToGroundAction of(ArmorStandAction... actions) {
    throw new UnsupportedOperationException("Please use SnapToGroundAction.create");
  }
}
