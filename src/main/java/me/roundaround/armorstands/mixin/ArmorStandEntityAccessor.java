package me.roundaround.armorstands.mixin;

import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandEntityAccessor {
  @Invoker("setSmall")
  void invokeSetSmall(boolean small);

  @Invoker("setShowArms")
  void invokeSetShowArms(boolean showArms);

  @Invoker("setHideBasePlate")
  void invokeSetHideBasePlate(boolean hideBasePlate);

  @Accessor("disabledSlots")
  int getDisabledSlots();

  @Accessor("disabledSlots")
  void setDisabledSlots(int disabledSlots);
}
