package net.dravigen.letMeMove.render;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.GeneralUtils;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;


import static net.dravigen.letMeMove.utils.AnimationUtils.*;
import static net.dravigen.letMeMove.utils.GeneralUtils.*;


public class AnimationRegistry {
    public final static ResourceLocation STANDING_ID = new ResourceLocation("LMM","standing");
    public final static ResourceLocation CROUCHING_ID = new ResourceLocation("LMM","crouching");
    public final static ResourceLocation SWIMMING_ID = new ResourceLocation("LMM","crawling");
    public final static ResourceLocation DIVING_ID = new ResourceLocation("LMM","diving");
    public final static ResourceLocation HIGH_FALLING_ID = new ResourceLocation("LMM","falling");
    public final static ResourceLocation SKYDIVING_ID = new ResourceLocation("LMM","skyDiving");


    public static void registerAllAnimation() {
        AnimationCustom standing = registerAnimation(STANDING_ID, 1.8f, 1, false);
        registerStanding(standing);

        AnimationCustom crouching = registerAnimation(CROUCHING_ID, 1.4f, 0.3f, false);
        registerCrouching(crouching);

        AnimationCustom swimming = registerAnimation(SWIMMING_ID, 0.8f, 0.15f, true);
        registerSwimming(swimming);

        AnimationCustom diving = registerAnimation(DIVING_ID, 1f, 0.1f, true);
        registerDiving(diving);

        AnimationCustom highFalling = registerAnimation(HIGH_FALLING_ID, 1.8f, 0.05f, true);
        registerHighFalling(highFalling);

        AnimationCustom skyDiving = registerAnimation(SKYDIVING_ID, 1f, 0.2f, true);
        registerSkyDiving(skyDiving);

    }

    private static void registerStanding(AnimationCustom standing) {
        standing.setConditions(((player, bb) ->
                false));
        standing.setAnimationRender(((mc, model, entity, f, g, h, i, j, u) ->
                commonAnimation(model, entity, f, g, h, i, j)));
        standing.setLeaningUpdate((AnimationRegistry::commonLeaningUpdate));
    }

    private static void registerCrouching(AnimationCustom crouching) {
        crouching.setConditions(((player, bb) ->
                player.isSneaking() && (player.onGround || player.fallDistance < 5)));
        crouching.setAnimationRender(((mc, model, entity, f, g, h, i, j, u) ->
                commonAnimation(model, entity, f, g, h, i, j)));
        crouching.setLeaningUpdate((AnimationRegistry::commonLeaningUpdate));
    }

    private static void registerSwimming(AnimationCustom swimming) {
        swimming.setConditions(((player, bb) ->
                (Keyboard.isKeyDown(Keyboard.KEY_C) && (player.onGround || isInsideWater(player)))
                || ((isInsideWater(player) && player.getLookVec().yCoord < 0.45 || isHeadInsideWater(player))
                && player.isUsingSpecialKey() && player.moveForward > 0 && !player.capabilities.isFlying)));
        swimming.setAnimationRender(((mc, model, entity, f, g, h, i, j, u) ->
                swimmingAnimation(model, entity, f, g, h, i, j)));
        swimming.setLeaningUpdate((AnimationRegistry::swimmingLeaningUpdate));
    }

    private static void registerDiving(AnimationCustom diving) {
        diving.setConditions(((player, bb) ->
                Keyboard.isKeyDown(Keyboard.KEY_C) && !player.onGround && !isInsideWater(player)));
        diving.setAnimationRender(((mc, model, entity, f, g, h, i, j, u) ->
                divingAnimation(model, entity, h, i, j)));
        diving.setLeaningUpdate((AnimationRegistry::divingLeaningUpdate));
    }

    private static void registerHighFalling(AnimationCustom highFalling) {
        highFalling.setConditions(((player, axisAlignedBB) ->
                !Keyboard.isKeyDown(Keyboard.KEY_C) && player.fallDistance > 23 && !player.isSneaking() && !player.capabilities.isFlying));
        highFalling.setAnimationRender(((mc, model, entity, f, g, h, i, j, u) ->
                highFallingAnimation(model,entity,f,g,h,i,j)));
        highFalling.setLeaningUpdate((AnimationRegistry::highFallingLeaningUpdate));
    }

    private static void registerSkyDiving(AnimationCustom skyDiving) {
        skyDiving.setConditions(((player, axisAlignedBB) ->
                !Keyboard.isKeyDown(Keyboard.KEY_C) && player.fallDistance > 23 && player.isSneaking() && !player.capabilities.isFlying));
        skyDiving.setAnimationRender(((mc, model, entity, f, g, h, i, j, u) ->
                skyDivingAnimation(model,entity,f,g,h,i,j)));
        skyDiving.setLeaningUpdate((AnimationRegistry::skyDivingLeaningUpdate));
    }

    public static void commonLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
        float goal = 0;

        customEntity.llm_$setLeaningPitch(goal);
    }

    public static void swimmingLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
        float goal;

        if (entity.inWater) {
            goal = (entity.rotationPitch + 90) / 90f;
        }
        else {
            goal = 1;
        }

        customEntity.llm_$setLeaningPitch(goal);
    }

    public static void divingLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        entity.limbSwingAmount = 0;
        entity.limbSwing = 0;

        float pitch = (float) ((-entity.motionY) * 1.25f);
        float goal = (pitch > 1 ? 1 : pitch) + 1;

        customEntity.llm_$setLeaningPitch(goal);
    }

    public static void highFallingLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        float goal = (entity.fallDistance - 23) / 12;

        customEntity.llm_$setLeaningPitch(goal);
    }

    public static void skyDivingLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        float goal = entity.moveForward > 0 ? 1.2f : 1;

        customEntity.llm_$setLeaningPitch(goal);
    }


    private static void commonAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;

        resetAnimationRotationPoints(model);

        model.bipedHead.rotateAngleY = i * (float) (Math.PI / 180.0);
        model.bipedHead.rotateAngleX = j * (float) (Math.PI / 180.0);
        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        float[] rArm = new float[3];
        float[] lArm = new float[3];
        float[] body = new float[3];

        float k = 1.0F;

        rArm[0] = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / k
                + (model.isRiding ? (float) (-Math.PI / 5) : 0);
        rArm[1] = model.aimedBow ? -0.1F + model.bipedHead.rotateAngleY : 0.0F;
        rArm[2] = model.aimedBow ? 0.1F + model.bipedHead.rotateAngleY + 0.4F : 0.0F;

        lArm[0] = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k
                + (model.isRiding ? (float) (-Math.PI / 5) : 0);
        lArm[1] = 0.0F;
        lArm[2] = 0.0F;

        body[0] = 0;
        body[1] = 0;
        body[2] = 0;

        rArm[0] = model.aimedBow ?
                (float) (-Math.PI / 2) + model.bipedHead.rotateAngleX
                : model.heldItemRight != 0 ?
                rArm[0] * 0.5f - 0.31415927f * (float) model.heldItemRight
                : rArm[0];

        lArm[0] = model.aimedBow ?
                (float) (-Math.PI / 2) + model.bipedHead.rotateAngleX
                : model.heldItemLeft != 0 ?
                lArm[0] * 0.5f - 0.31415927f * (float) model.heldItemLeft
                : lArm[0];

        if (!(model.onGround <= 0.0F)) {
            float onGround = model.onGround;
            body[1] = MathHelper.sin(MathHelper.sqrt_float(onGround) * (float) (Math.PI * 2)) * 0.2F;

            model.bipedRightArm.rotationPointZ = MathHelper.sin(body[1]) * 5.0F;
            model.bipedRightArm.rotationPointX = -MathHelper.cos(body[1]) * 5.0F;

            model.bipedLeftArm.rotationPointZ = -MathHelper.sin(body[1]) * 5.0F;
            model.bipedLeftArm.rotationPointX = MathHelper.cos(body[1]) * 5.0F;

            rArm[1] = rArm[1] + body[1];

            lArm[1] = lArm[1] + body[1];
            lArm[0] = lArm[0] + body[1];

            onGround = 1.0F - model.onGround;
            onGround *= onGround;
            onGround *= onGround;
            onGround = 1.0F - onGround;
            float v = MathHelper.sin(onGround * (float) Math.PI);
            float v1 = MathHelper.sin(model.onGround * (float) Math.PI) * -(model.bipedHead.rotateAngleX - 0.7F) * 0.75F;

            rArm[0] = (float) (rArm[0] - (v * 1.2 + v1));
            rArm[1] = rArm[1] + body[1] * 2.0F;
            rArm[2] = rArm[2] + MathHelper.sin(model.onGround * (float) Math.PI) * -0.4F;
        }

        boolean isCrouching = customMoveEntity.llm_$isAnimation(CROUCHING_ID);

        if (isCrouching) {
            body[0] = 0.5F;

            rArm[0] += 0.4F;
            model.bipedRightArm.rotationPointY = 5.2F;

            lArm[0] += 0.4F;
            model.bipedLeftArm.rotationPointY = 5.2F;

            model.bipedRightLeg.rotationPointY = 12.2F;
            model.bipedRightLeg.rotationPointZ = 4.0F;

            model.bipedLeftLeg.rotationPointY = 12.2F;
            model.bipedLeftLeg.rotationPointZ = 4.0F;

            model.bipedHead.rotationPointY = 4.2F;
            model.bipedHeadwear.rotationPointY = 4.2f;

            model.bipedBody.rotationPointY = 3.2F;
        }

        setSmoothAllRotation(model.bipedRightArm, rArm[0], rArm[1], rArm[2], 0.05f);

        setSmoothAllRotation(model.bipedLeftArm, lArm[0], lArm[1], lArm[2], 0.05f);

        setSmoothAllRotation(model.bipedRightLeg,
                model.isRiding ? -1.4137167F : MathHelper.cos(f * 0.6662F) * 1.4F * g / k,
                model.isRiding ? (float) (Math.PI / 10) : 0 ,
                model.isRiding ? 0.07853982F : 0,
                0.05f
        );

        setSmoothAllRotation(model.bipedLeftLeg,
                model.isRiding ? -1.4137167F : MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / k,
                model.isRiding ? (float) (Math.PI / 10) : 0 ,
                model.isRiding ? 0.07853982F : 0,
                0.05f
        );

        setSmoothAllRotation(model.bipedBody, body[0], body[1], body[2], 0.1f);

        if (entity instanceof EntityPlayer) {
            if (entity.getCurrentItemOrArmor(2) == null) {
                if (isCrouching) {
                    model.bipedCloak.rotationPointZ = 1.4F;
                    model.bipedCloak.rotationPointY = 1.85F;
                }
                else {
                    model.bipedCloak.rotationPointZ = 0.0F;
                    model.bipedCloak.rotationPointY = 0.0F;
                }
            }
            else if (isCrouching) {
                model.bipedCloak.rotationPointZ = 0.3F;
                model.bipedCloak.rotationPointY = 0.8F;
            }
            else {
                model.bipedCloak.rotationPointZ = -1.1F;
                model.bipedCloak.rotationPointY = -0.85F;
            }
        }
    }

    private static void swimmingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
        float leaningPitch = Math.min(1.0F, customMoveEntity.llm_$getLeaningPitch());

        resetAnimationRotationPoints(model);

        setSmoothAllRotation(model.bipedBody,0,0,0,0.1f);

        leaningPitch = entity.inWater ? 1 : leaningPitch;
        f = entity.inWater ? f : f * 2;
        g = entity.inWater ? g : g * 2;

        model.bipedHead.rotateAngleY = i * (float) (Math.PI / 180.0);

        if (leaningPitch > 0.0F) {
            model.bipedHead.rotateAngleX = lerpAngle(leaningPitch, model.bipedHead.rotateAngleX, (float) (-Math.PI / 4));
        }
        else {
            model.bipedHead.rotateAngleX = j * (float) (Math.PI / 180.0);
        }

        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        float[] rArm = new float[3];
        float[] lArm = new float[3];
        float[] rLeg = new float[3];
        float[] lLeg = new float[3];

        float k = 1.0F;

        rArm[0] = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / k;
        rArm[1] = 0.0F;
        rArm[2] = 0.0F;

        lArm[0] = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;
        lArm[1] = 0.0F;
        lArm[2] = 0.0F;

        rLeg[0] = entity.inWater ? MathHelper.cos(f * 0.6662F) * 1.4F * g / k : 0;
        rLeg[1] = 0.0F;
        rLeg[2] = 0.0F;

        lLeg[0] = entity.inWater ? MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / k : 0;
        lLeg[1] = 0.0F;
        lLeg[2] = 0.0F;


        rArm[0] = model.heldItemRight != 0 ? rArm[0] * 0.5f - 0.31415927f * (float) model.heldItemRight : rArm[0];
        lArm[0] = model.heldItemLeft != 0 ? lArm[0] * 0.5f - 0.31415927f * (float) model.heldItemLeft : lArm[0];

        rArm[0] = model.aimedBow ? (float) (-Math.PI / 2) + model.bipedHead.rotateAngleX : rArm[0];
        lArm[0] = model.aimedBow ? (float) (-Math.PI / 2) + model.bipedHead.rotateAngleX : lArm[0];

        rArm[1] = model.aimedBow ? -0.1F + model.bipedHead.rotateAngleY : rArm[1];
        lArm[1] = model.aimedBow ? 0.1F + model.bipedHead.rotateAngleY + 0.4F : lArm[1];

        if (leaningPitch > 0.0F) {
            float l = f % 26.0F;
            float m = model.onGround > 0.0F ? 0.0F : leaningPitch;
            if (l < 14.0F) {
                rArm[0] = lerp(m, rArm[0], 0.0F);
                rArm[1] = lerp(m, rArm[1], (float) Math.PI);
                rArm[2] = lerp(m, rArm[2], (float) Math.PI - 1.8707964F * method_2807(l) / method_2807(14.0F));

                lArm[0] = lerpAngle(leaningPitch, lArm[0], 0.0F);
                lArm[1] = lerpAngle(leaningPitch, lArm[1], (float) Math.PI);
                lArm[2] = lerpAngle(leaningPitch, lArm[2], (float) Math.PI + 1.8707964F * method_2807(l) / method_2807(14.0F));
            }
            else if (l >= 14.0F && l < 22.0F) {
                float o = (l - 14.0F) / 8.0F;
                rArm[0] = lerp(m, rArm[0], (float) (Math.PI / 2) * o);
                rArm[1] = lerp(m, rArm[1], (float) Math.PI);
                rArm[2] = lerp(m, rArm[2], 1.2707963F + 1.8707964F * o);

                lArm[0] = lerpAngle(leaningPitch, lArm[0], (float) (Math.PI / 2) * o);
                lArm[1] = lerpAngle(leaningPitch, lArm[1], (float) Math.PI);
                lArm[2] = lerpAngle(leaningPitch, lArm[2], 5.012389F - 1.8707964F * o);
            }
            else if (l >= 22.0F && l < 26.0F) {
                float o = (l - 22.0F) / 4.0F;
                rArm[0] = lerp(m, rArm[0], (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
                rArm[1] = lerp(m, rArm[1], (float) Math.PI);
                rArm[2] = lerp(m, rArm[2], (float) Math.PI);

                lArm[0] = lerpAngle(leaningPitch, lArm[0], (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
                lArm[1] = lerpAngle(leaningPitch, lArm[1], (float) Math.PI);
                lArm[2] = lerpAngle(leaningPitch, lArm[2], (float) Math.PI);
            }

            lLeg[0] = lerp(leaningPitch, lLeg[0], 0.3F * MathHelper.cos(f * 0.33333334F + (float) Math.PI));
            rLeg[0] = lerp(leaningPitch, rLeg[0], 0.3F * MathHelper.cos(f * 0.33333334F));

        }

        setSmoothAllRotation(model.bipedRightArm, rArm[0], rArm[1], rArm[2], 0.03f);
        setSmoothAllRotation(model.bipedLeftArm, lArm[0], lArm[1], lArm[2], 0.03f);

        setSmoothAllRotation(model.bipedRightLeg, rLeg[0], rLeg[1], rLeg[2], 0.03f);
        setSmoothAllRotation(model.bipedLeftLeg, lLeg[0], lLeg[1], lLeg[2], 0.03f);

        if (entity instanceof EntityPlayer) {
            if (entity.getCurrentItemOrArmor(2) == null) {
                model.bipedCloak.rotationPointZ = 0.0F;
                model.bipedCloak.rotationPointY = 0.0F;
            }
            else {
                model.bipedCloak.rotationPointZ = -1.1F;
                model.bipedCloak.rotationPointY = -0.85F;
            }
        }
    }

    private static void divingAnimation(ModelBiped model, EntityLivingBase entity, float h, float i, float j) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
        float leaningPitch = Math.min(1.0F, customMoveEntity.llm_$getLeaningPitch());

        resetAnimationRotationPoints(model);

        setSmoothAllRotation(model.bipedBody,0,0,0,0.1f);

        model.bipedHead.rotateAngleY = i * (float) (Math.PI / 180.0);
        if (leaningPitch > 0.0F) {
            model.bipedHead.rotateAngleX = lerpAngle(leaningPitch, model.bipedHead.rotateAngleX, (float) (-Math.PI / 4));
        }
        else {
            model.bipedHead.rotateAngleX = j * (float) (Math.PI / 180.0);
        }
        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;


        if (leaningPitch > 0.0F) {
            float m = model.onGround > 0.0F ? 0.0F : leaningPitch;

            setSmoothAllRotation(model.bipedRightArm,
                    lerp(m, model.bipedRightArm.rotateAngleX, 0.0F),
                    lerp(m, model.bipedRightArm.rotateAngleY, (float) Math.PI),
                    lerp(m, model.bipedRightArm.rotateAngleZ, (float) Math.PI - 1.8707964F * method_2807(0) / method_2807(14.0F))
            );

            setSmoothAllRotation(model.bipedLeftArm,
                    lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleX, 0.0F),
                    lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleY, (float) Math.PI),
                    lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleZ, (float) Math.PI + 1.8707964F * method_2807(0) / method_2807(14.0F))
            );

            float n = customMoveEntity.llm_$getLeaningPitch();

            model.bipedRightLeg.rotationPointY = 11.3f;
            model.bipedLeftLeg.rotationPointY = 11.3f;

            setSmoothAllRotation(model.bipedRightLeg, -1f + n / 2, 0, 0);
            setSmoothAllRotation(model.bipedLeftLeg, -1f + n / 2, 0, 0);
        }

        if (entity instanceof EntityPlayer) {
            if (entity.getCurrentItemOrArmor(2) == null) {
                model.bipedCloak.rotationPointZ = 0.0F;
                model.bipedCloak.rotationPointY = 0.0F;
            }
            else {
                model.bipedCloak.rotationPointZ = -1.1F;
                model.bipedCloak.rotationPointY = -0.85F;
            }
        }
    }

    private static void highFallingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
        float leaning = customEntity.llm_$getLeaningPitch();

        resetAnimationRotationPoints(model);

        setSmoothAllRotation(model.bipedBody,0,0,0,0.1f);

        model.bipedHead.rotateAngleY = i * (float) (Math.PI / 180.0);
        model.bipedHead.rotateAngleX = 0.25f + MathHelper.cos(leaning) / 2;
        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        float halfSinLean = -MathHelper.sin(leaning) * 1 / 2;

        setSmoothAllRotation(model.bipedRightArm, halfSinLean, halfSinLean, 1.5f + MathHelper.sin(leaning));
        setSmoothAllRotation(model.bipedLeftArm, halfSinLean, halfSinLean, -1.5f - MathHelper.cos(leaning));

        setSmoothAllRotation(model.bipedRightLeg, MathHelper.cos(leaning), 0, 0);
        setSmoothAllRotation(model.bipedLeftLeg, MathHelper.sin(leaning), 0, 0);
    }

    private static void skyDivingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j) {
        resetAnimationRotationPoints(model);

        setSmoothAllRotation(model.bipedBody,0,0,0,0.1f);

        model.bipedHead.rotateAngleY = i * (float) (Math.PI / 180.0);
        model.bipedHead.rotateAngleX = -0.5f;
        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        boolean fMove = entity.moveForward > 0;

        setSmoothAllRotation(model.bipedRightArm, 0.5f, 0, fMove ? 0.5f : 2);
        setSmoothAllRotation(model.bipedLeftArm, 0.5f, 0, fMove ? -0.5f : -2);

        setSmoothAllRotation(model.bipedRightLeg, 0.5f, 0, 0.15f);
        setSmoothAllRotation(model.bipedLeftLeg, 0.5f, 0, -0.15f);
    }
}
