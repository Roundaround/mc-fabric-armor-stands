package me.roundaround.armorstands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.option.KeyBinding;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
  @Invoker("reset")
  public void invokeReset();
}
