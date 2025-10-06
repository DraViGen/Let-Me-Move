package net.dravigen.letMeMove.mixin.render;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.GeneralUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.letMeMove.render.AnimationRegistry.*;


@Mixin(ModelBiped.class)
public abstract class ModelBipedMixin extends ModelBase {

    @Unique
    float prevXRotation = 0;
    @Unique
    float prevYRotation = 0;
    @Unique
    float prevZRotation = 0;
    @Unique
    float prevOffset = 0;

    @Inject(method = "render",at = @At("HEAD"))
    private void rotateBody(Entity entity, float f, float g, float h, float i, float j, float u, CallbackInfo ci) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        float leaningPitch = customEntity.llm_$getLeaningPitch();

        if (customEntity.llm_$getAnimation().needLeaningUpdate) {
            if (customEntity.llm_$isAnimation(HIGH_FALLING_ID)) {
                GL11.glTranslatef(0, prevOffset = GeneralUtils.incrementUntilGoal(prevOffset, 0.5f, 0.025f), 0);
                GL11.glRotatef(prevYRotation = GeneralUtils.incrementAngleUntilGoal(prevYRotation,(10f * leaningPitch) % 360, 0.01f), 0, 1, 0);
                GL11.glRotatef(prevZRotation = GeneralUtils.incrementAngleUntilGoal(prevZRotation,(22.5f * leaningPitch) % 360, 0.01f), 0, 0, 1);
                GL11.glRotatef(prevXRotation = GeneralUtils.incrementAngleUntilGoal(prevXRotation,(40f * leaningPitch) % 360, 0.01f), 1, 0, 0);
                GL11.glTranslatef(0, -prevOffset, 0);
            }
            else {
                GL11.glTranslatef(0, 1.98f - (entity.yOffset + 0.18f), 0);
                GL11.glRotatef(prevYRotation = GeneralUtils.incrementAngleUntilGoal(prevYRotation,0,0.01f), 0, 1, 0);
                GL11.glRotatef(prevZRotation = GeneralUtils.incrementAngleUntilGoal(prevZRotation,0,0.01f), 0, 0, 1);
                GL11.glRotatef(prevXRotation = GeneralUtils.incrementAngleUntilGoal(prevXRotation,90 * leaningPitch,0.02f), 1, 0, 0);
            }
        }
        else {
            GL11.glTranslatef(0, prevOffset = GeneralUtils.incrementUntilGoal(prevOffset, 0, 0.025f), 0);
            GL11.glRotatef(prevYRotation = GeneralUtils.incrementAngleUntilGoal(prevYRotation,0,0.025f), 0, 1, 0);
            GL11.glRotatef(prevZRotation = GeneralUtils.incrementAngleUntilGoal(prevZRotation,0,0.025f), 0, 0, 1);
            GL11.glRotatef(prevXRotation = GeneralUtils.incrementAngleUntilGoal(prevXRotation,90 * leaningPitch,0.05f), 1, 0, 0);
        }
    }

    @Inject(method = "setRotationAngles",at = @At("HEAD"),cancellable = true)
    public void setAngles(float f, float g, float h, float i, float j, float u, Entity livingEntity, CallbackInfo ci) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) livingEntity;

        ci.cancel();

        if (!Minecraft.getMinecraft().getIsGamePaused()) {
            if (livingEntity instanceof EntityPlayer player) {
                customMoveEntity.llm_$getAnimation().renderAnimation(Minecraft.getMinecraft(), (ModelBiped) (Object) this, player, f, g, h, i, j, u);
            }
        }
    }
}
