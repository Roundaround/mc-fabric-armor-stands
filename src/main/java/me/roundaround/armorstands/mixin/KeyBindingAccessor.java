package me.roundaround.armorstands.mixin;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
  @Invoker("reset")
  void invokeReset();
}
