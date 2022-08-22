package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor {
  @Accessor("trackedStacks")
  public DefaultedList<ItemStack> getTrackedStacks();

  @Accessor("previousTrackedStacks")
  public DefaultedList<ItemStack> getPreviousTrackedStacks();
}
