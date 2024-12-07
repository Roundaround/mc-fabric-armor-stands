package me.roundaround.armorstands.util.actions;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ScaleAction implements ArmorStandAction {
  private final float argument;
  private final boolean absolute;
  private final boolean round;
  private Float originalScale = null;

  private ScaleAction(float argument, boolean absolute, boolean round) {
    this.argument = argument;
    this.absolute = absolute;
    this.round = round;
  }

  public static ScaleAction absolute(float scale) {
    return absolute(scale, false);
  }

  public static ScaleAction absolute(float scale, boolean round) {
    return new ScaleAction(scale, true, round);
  }

  public static ScaleAction relative(float scale) {
    return relative(scale, true);
  }

  public static ScaleAction relative(float scale, boolean round) {
    return new ScaleAction(scale, false, round);
  }

  @Override
  public Text getName(ArmorStandEntity armorStand) {
    return Text.translatable("armorstands.action.scale");
  }

  @Override
  public void apply(PlayerEntity player, ArmorStandEntity armorStand) {
    this.originalScale = armorStand.getScale();

    float scale = this.argument;
    if (!this.absolute) {
      scale += this.originalScale;
    }

    setScale(armorStand, scale, this.round);
  }

  @Override
  public void undo(PlayerEntity player, ArmorStandEntity armorStand) {
    if (this.originalScale == null) {
      return;
    }
    setScale(armorStand, this.originalScale);
  }

  public static void setScale(ArmorStandEntity armorStand, float scale) {
    setScale(armorStand, scale, false);
  }

  public static void setScale(ArmorStandEntity armorStand, float scale, boolean round) {
    float target = MathHelper.clamp(scale, 0.01f, 10f);
    if (round) {
      if (target < 1) {
        target = Math.round(target * 4) / 4f;
      } else {
        target = Math.round(target * 2) / 2f;
      }
    }

    EntityAttributeInstance attribute = armorStand.getAttributes().getCustomInstance(EntityAttributes.SCALE);
    if (attribute != null) {
      attribute.setBaseValue(target);
    }
  }
}
