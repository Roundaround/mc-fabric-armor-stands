package me.roundaround.armorstands.mixin;

import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
  @Accessor
  EntityEquipment getEquipment();
}
