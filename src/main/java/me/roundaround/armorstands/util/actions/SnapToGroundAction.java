package me.roundaround.armorstands.util.actions;

import java.util.Collection;
import java.util.List;

import me.roundaround.armorstands.network.ArmorStandFlag;
import net.minecraft.text.Text;

public class SnapToGroundAction extends ComboAction {
  private SnapToGroundAction(Collection<ArmorStandAction> actions) {
    super(Text.translatable("armorstands.action.snapToGround"), actions);
  }

  public static SnapToGroundAction standing() {
    return create(false);
  }

  public static SnapToGroundAction sitting() {
    return create(true);
  }

  public static SnapToGroundAction create(boolean sitting) {
    return new SnapToGroundAction(List.of(
        FlagAction.set(ArmorStandFlag.NO_GRAVITY, true),
        MoveToGroundAction.create(sitting)));
  }
}
