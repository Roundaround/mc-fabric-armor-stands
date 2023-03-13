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
  public void invokeSetSmall(boolean small);

  @Invoker("setShowArms")
  public void invokeSetShowArms(boolean showArms);

  @Invoker("setHideBasePlate")
  public void invokeSetHideBasePlate(boolean hideBasePlate);

  @Accessor("heldItems")
  public DefaultedList<ItemStack> getHeldItems();

  @Accessor("armorItems")
  public DefaultedList<ItemStack> getArmorItems();

  @Accessor("disabledSlots")
  public int getDisabledSlots();

  @Accessor("disabledSlots")
  public void setDisabledSlots(int disabledSlots);
}
