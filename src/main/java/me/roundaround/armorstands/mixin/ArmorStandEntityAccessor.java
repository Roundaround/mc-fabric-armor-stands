package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandEntityAccessor {
  @Invoker("setSmall")
  void invokeSetSmall(boolean small);

  @Invoker("setShowArms")
  void invokeSetShowArms(boolean showArms);

  @Invoker("setHideBasePlate")
  void invokeSetHideBasePlate(boolean hideBasePlate);

  @Accessor("heldItems")
  DefaultedList<ItemStack> getHeldItems();

  @Accessor("armorItems")
  DefaultedList<ItemStack> getArmorItems();

  @Accessor("disabledSlots")
  int getDisabledSlots();

  @Accessor("disabledSlots")
  void setDisabledSlots(int disabledSlots);
}
