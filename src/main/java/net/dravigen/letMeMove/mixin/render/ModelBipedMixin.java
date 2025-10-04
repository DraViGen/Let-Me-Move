package net.dravigen.letMeMove.mixin.render;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.letMeMove.render.AnimationRegistry.*;


@Mixin(ModelBiped.class)
public abstract class ModelBipedMixin extends ModelBase {
    
    @Inject(method = "render",at = @At("HEAD"))
    private void rotateBody(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7, CallbackInfo ci) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
        float leaningPitch = customMoveEntity.llm_$getLeaningPitch();
        if (((ICustomMovementEntity) entity).llm_$isAnimation(SWIMMING_ID) || ((ICustomMovementEntity) entity).llm_$isAnimation(DIVING_ID)) {
            float var1 = 1.62f - (1.62f - 1.25f) * leaningPitch;
            GL11.glTranslatef(0, var1, 0);
            GL11.glRotatef(90 * leaningPitch, 1, 0, 0);
        }
    }

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
