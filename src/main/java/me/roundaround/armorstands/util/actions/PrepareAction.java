package me.roundaround.armorstands.util.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.util.ArmorStandHelper;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class PrepareAction extends ComboAction {
  private PrepareAction(Collection<ArmorStandAction> actions) {
    super(Text.translatable("armorstands.action.prepare"), actions);
  }

  public static PrepareAction create(ArmorStandEntity armorStand) {
    ArrayList<ArmorStandAction> actions = new ArrayList<>();
    actions.add(FlagAction.set(ArmorStandFlag.ARMS, true));
    actions.add(FlagAction.set(ArmorStandFlag.BASE, true));
    actions.add(FlagAction.set(ArmorStandFlag.GRAVITY, true));

    Optional<Vec3d> maybeGround = ArmorStandHelper.getStandingPos(armorStand, false);
    if (maybeGround.isPresent()) {
      actions.add(MoveAction.absolute(maybeGround.get()));
    }

    return new PrepareAction(actions);
  }

  @Deprecated
  public static PrepareAction of(Collection<ArmorStandAction> actions) {
    throw new UnsupportedOperationException("Please use PrepareAction.create");
  }

  @Deprecated
  public static PrepareAction of(ArmorStandAction... actions) {
    throw new UnsupportedOperationException("Please use PrepareAction.create");
  }
}
