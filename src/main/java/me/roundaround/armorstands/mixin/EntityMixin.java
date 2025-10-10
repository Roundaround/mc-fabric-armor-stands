package me.roundaround.armorstands.mixin;

import me.roundaround.armorstands.interfaces.EntityPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class EntityMixin implements EntityPosition {
  @Shadow
  private Vec3d pos;

  @Override
  public Vec3d armorstands$getPos() {
    // Copy so we don't accidentally modify the coords individually inside the pos object
    return new Vec3d(this.pos.x, this.pos.y, this.pos.z);
  }
}
