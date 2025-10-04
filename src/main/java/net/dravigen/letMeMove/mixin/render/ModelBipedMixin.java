package net.dravigen.letMeMove.mixin.render;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ModelBiped.class)
public abstract class ModelBipedMixin extends ModelBase {

    @Inject(method = "setRotationAngles",at = @At("HEAD"),cancellable = true)
    public void setAngles(float f, float g, float h, float i, float j, float u, Entity livingEntity, CallbackInfo ci) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) livingEntity;

        ci.cancel();

        if (livingEntity instanceof EntityPlayer) {
            customMoveEntity.llm_$getAnimation().updateLeaning((EntityLivingBase) livingEntity);
            customMoveEntity.llm_$getAnimation().renderAnimation(Minecraft.getMinecraft(), (ModelBiped) (Object)this, (EntityLivingBase)livingEntity, f, g, h, i, j, u);
        }
    }
}
