package me.roundaround.armorstands.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.roundaround.armorstands.client.ClientSideConfig;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorStandEntityRenderer.class)
public abstract class ArmorStandEntityRendererMixin extends LivingEntityRenderer<ArmorStandEntity,
    ArmorStandEntityRenderState, ArmorStandArmorEntityModel> {

  @ModifyReturnValue(method = "hasLabel(Lnet/minecraft/entity/decoration/ArmorStandEntity;D)Z", at = @At("RETURN"))
  private boolean adjustHasLabel(boolean original, ArmorStandEntity entity, double squaredDistanceToCamera) {
    if (!original) {
      return false;
    }

    ClientSideConfig config = ClientSideConfig.getInstance();

    if (config.directOnlyNameRender.getPendingValue() && this.dispatcher.targetedEntity != entity) {
      return false;
    }

    int nameRenderDistance = config.nameRenderDistance.getPendingValue();
    int maxSquaredDistance = nameRenderDistance * nameRenderDistance;
    if (maxSquaredDistance > 0) {
      return squaredDistanceToCamera < maxSquaredDistance;
    }

    return true;
  }

  private ArmorStandEntityRendererMixin(
      EntityRendererFactory.Context ctx,
      ArmorStandArmorEntityModel model,
      float shadowRadius
  ) {
    super(ctx, model, shadowRadius);
  }
}
