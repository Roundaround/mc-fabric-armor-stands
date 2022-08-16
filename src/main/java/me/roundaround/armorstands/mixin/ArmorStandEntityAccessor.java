package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.decoration.ArmorStandEntity;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandEntityAccessor {
  @Invoker("setSmall")
  public void invokeSetSmall(boolean small);
  
  @Invoker("setShowArms")
  public void invokeSetShowArms(boolean showArms);

  @Invoker("setHideBasePlate")
  public void invokeSetHideBasePlate(boolean hideBasePlate);

  @Invoker("setMarker")
  public void invokeSetMarker(boolean marker);

  @Accessor("disabledSlots")
  public void setDisabledSlots(int disabledSlots);
}
