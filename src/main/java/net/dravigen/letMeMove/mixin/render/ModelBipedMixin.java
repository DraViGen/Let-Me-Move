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
    private void rotateBody(Entity entity, float f, float g, float h, float i, float j, float u, CallbackInfo ci) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        if (customEntity.llm_$getAnimation().needLeaningUpdate) {
            float leaningPitch = customEntity.llm_$getLeaningPitch();
            float var1 = 1.62f - (1.62f - 1.25f) * (leaningPitch > 1 ? 1 : leaningPitch);
            if (customEntity.llm_$isAnimation(HIGH_FALLING_ID)) {
                GL11.glTranslatef(0, 0, 0);
                GL11.glRotatef(22.5f * leaningPitch, 0, 1, 0);
                GL11.glRotatef(10 * leaningPitch, 0, 0, 1);
                GL11.glRotatef(45 * leaningPitch, 1, 0, 0);

            }
            else {
                GL11.glTranslatef(0, var1, 0);
                GL11.glRotatef(90 * leaningPitch, 1, 0, 0);
            }
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
