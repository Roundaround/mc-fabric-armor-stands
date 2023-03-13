package me.roundaround.armorstands.util.actions;

import java.util.Collection;
import java.util.List;

import me.roundaround.armorstands.network.ArmorStandFlag;
import me.roundaround.armorstands.util.PosePreset;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

public class PrepareAction extends ComboAction {
  private PrepareAction(Collection<ArmorStandAction> actions) {
    super(Text.translatable("armorstands.action.prepare"), actions);
  }

  public static PrepareAction create(ArmorStandEntity armorStand) {
    return new PrepareAction(List.of(
        FlagAction.set(ArmorStandFlag.SHOW_ARMS, true),
        FlagAction.set(ArmorStandFlag.HIDE_BASE_PLATE, true),
        FlagAction.set(ArmorStandFlag.INVISIBLE, false),
        FlagAction.set(ArmorStandFlag.NO_GRAVITY, true),
        PoseAction.fromPose(PosePreset.DEFAULT.toPose())));
  }
}
