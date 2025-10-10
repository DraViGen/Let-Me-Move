package net.dravigen.letMeMove.mixin.render;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.render.AnimationCustom;
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

    @Unique float prevXRotation = 0;
    @Unique float prevYRotation = 0;
    @Unique float prevZRotation = 0;
    @Unique float prevOffset = 0;
    @Unique long transitionTime = 0;
    @Unique ResourceLocation prevAnimation;
    @Unique long prevTime;

    @Inject(method = "render",at = @At("HEAD"))
    private void rotateBody(Entity entity, float f, float g, float h, float i, float j, float u, CallbackInfo ci) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        AnimationCustom animation = customEntity.llm_$getAnimation();
        if (animation == null) return;

        float leaningPitch = customEntity.llm_$getLeaningPitch();

        if (prevAnimation != animation.getID()) {
            prevAnimation = animation.getID();
            transitionTime = 1000;
        }

        float delta = (System.currentTimeMillis() - prevTime) / 25f;

        boolean tr = transitionTime > 0;

        if (animation.needLeaningUpdate) {
            if (customEntity.llm_$isAnimation(HIGH_FALLING_ID)) {
                prevOffset = GeneralUtils.incrementUntilGoal(prevOffset, 0.5f, tr ? 0.4f * delta : 1);
                prevYRotation = GeneralUtils.incrementAngleUntilGoal(prevYRotation, (7.5f * leaningPitch) % 360, tr ? 0.3f * delta : 1);
                prevZRotation = GeneralUtils.incrementAngleUntilGoal(prevZRotation, (16f * leaningPitch) % 360, tr ? 0.3f * delta : 1);
                prevXRotation = GeneralUtils.incrementAngleUntilGoal(prevXRotation, (30f * leaningPitch) % 360, tr ? 0.3f * delta : 1);
            }
            else {
                prevOffset = 1.98f - (entity.yOffset + 0.18f);
                prevYRotation = GeneralUtils.incrementAngleUntilGoal(prevYRotation, 0, tr ? 0.1f * delta : 1);
                prevZRotation = GeneralUtils.incrementAngleUntilGoal(prevZRotation, 0, tr ? 0.1f * delta : 1);
                prevXRotation = GeneralUtils.incrementAngleUntilGoal(prevXRotation, 90 * leaningPitch, tr ? 0.2f * delta : 1);
            }
        }
        else {
            prevOffset = GeneralUtils.incrementUntilGoal(prevOffset, 0, tr ? 0.4f * delta : 1);
            prevYRotation = GeneralUtils.incrementAngleUntilGoal(prevYRotation, 0, tr ? 0.2f * delta : 1);
            prevZRotation = GeneralUtils.incrementAngleUntilGoal(prevZRotation, 0, tr ? 0.2f * delta : 1);
            prevXRotation = GeneralUtils.incrementAngleUntilGoal(prevXRotation, 90 * leaningPitch, tr ? 0.35f * delta : 1);
        }

        if (tr) {
            transitionTime -= System.currentTimeMillis() - prevTime;

            if (transitionTime < 0) transitionTime = 0;
        }

        prevTime = System.currentTimeMillis();

        GL11.glTranslatef(0, prevOffset, 0);
        GL11.glRotatef(prevYRotation, 0, 1, 0);
        GL11.glRotatef(prevZRotation, 0, 0, 1);
        GL11.glRotatef(prevXRotation, 1, 0, 0);

        if (customEntity.llm_$isAnimation(HIGH_FALLING_ID)) GL11.glTranslatef(0, -prevOffset, 0);
    }

    @Inject(method = "setRotationAngles",at = @At("HEAD"),cancellable = true)
    public void setAngles(float f, float g, float h, float i, float j, float u, Entity livingEntity, CallbackInfo ci) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) livingEntity;

        if (customEntity.llm_$getAnimation() == null) return;

        if (livingEntity instanceof EntityPlayer player) {

            ci.cancel();

            customEntity.llm_$getAnimation().renderAnimation(Minecraft.getMinecraft(), (ModelBiped) (Object) this, player, f, g, h, i, j, u, transitionTime > 0);
/*
            if (!Minecraft.getMinecraft().getIsGamePaused()) {
                if (prevTime + 50 <= System.currentTimeMillis()) {
                }
            }*/
        }
    }
}
