package net.dravigen.letMeMove.render;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.AnimationUtils;
import net.minecraft.src.*;

import static net.dravigen.letMeMove.LetMeMoveAddon.*;
import static net.dravigen.letMeMove.utils.GeneralUtils.*;

public class AnimationRegistry {
    public final static ResourceLocation EXAMPLE_ID = new ResourceLocation("LMM", "example");
    public final static ResourceLocation STANDING_ID = new ResourceLocation("LMM", "standing");
    public final static ResourceLocation CROUCHING_ID = new ResourceLocation("LMM", "crouching");
    public final static ResourceLocation SWIMMING_ID = new ResourceLocation("LMM", "crawling");
    public final static ResourceLocation DIVING_ID = new ResourceLocation("LMM", "diving");
    public final static ResourceLocation HIGH_FALLING_ID = new ResourceLocation("LMM", "highFalling");
    public final static ResourceLocation SKYDIVING_ID = new ResourceLocation("LMM", "skyDiving");
    public final static ResourceLocation LOW_FALLING_ID = new ResourceLocation("LMM", "lowFalling");
    public final static ResourceLocation DASHING_ID = new ResourceLocation("LMM", "dashing");
    public final static ResourceLocation ROLLING_ID = new ResourceLocation("LMM", "rolling");

    public final static int lFalling_height = 3;
    public final static int hFalling_height = 24;
    private static final float pi = (float) Math.PI;
    static int pressTime = 0;

    /**
     * Register the animations here, the higher the animation the higher the priority (will be checked first)
     */
    public static void registerAllAnimation() {
        registerStanding();

        registerCrouching();

        registerSwimming();

        registerDiving();

        registerDashing();

        registerRolling();

        registerSkyDiving();

        registerHighFalling();

        registerLowFalling();
    }

    /**
     * Copy-paste this method to register a new animation.
     * Create an animation through {@code AnimationUtils.createAnimation();}, with the animation identifier, the height,
     * the movement speed modifier.
     * <p>
     * After creating the animation, register the general condition, activation condition, rendering logic and leaning
     * update logic with {@code registerAnimation();}
     * </p>
     */
    private static void registerExample() {
        AnimationCustom example = AnimationUtils.createAnimation(
                EXAMPLE_ID,
                1.8f, 1, false);

        example.registerAnimation(
                (player, axisAlignedBB) ->
                        player.inWater && player.onGround,
                (player, axisAlignedBB) ->
                        player.isSneaking() && player.inWater && player.onGround,
                AnimationRegistry::exampleAnimation,
                AnimationRegistry::commonLeaningUpdate
        );
    }

    /**
     * Animation registry methods
     */
    private static void registerStanding() {
        AnimationCustom standing = AnimationUtils.createAnimation(
                STANDING_ID,
                1.8f, 1, false);

        standing.registerAnimation(
                (player, axisAlignedBB) ->
                        false,
                (player, axisAlignedBB) ->
                        false,
                AnimationRegistry::commonAnimation,
                AnimationRegistry::commonLeaningUpdate
        );
    }

    private static void registerCrouching() {
        AnimationCustom crouching = AnimationUtils.createAnimation(
                CROUCHING_ID,
                1.4f, 0.3f, false);

        crouching.registerAnimation(
                (player, axisAlignedBB) ->
                        player.onGround
                                || player.fallDistance < 10,
                (player, axisAlignedBB) ->
                        player.isSneaking(),
                AnimationRegistry::commonAnimation,
                AnimationRegistry::commonLeaningUpdate
        );
    }

    private static void registerSwimming() {
        AnimationCustom swimming = AnimationUtils.createAnimation(
                SWIMMING_ID,
                0.8f, 0.15f, true);

        swimming.registerAnimation(
                (player, axisAlignedBB) ->
                        !player.capabilities.isFlying &&
                                (player.onGround || isInsideWater(player)),
                (player, axisAlignedBB) -> {
                    boolean conditionA =
                            crawl_key.pressed
                                    && (player.onGround || isInsideWater(player));

                    boolean conditionB =
                            (isInsideWater(player) && player.getLookVec().yCoord < 0.45 || isHeadInsideWater(player))
                                    && player.isUsingSpecialKey()
                                    && player.moveForward > 0
                                    && !player.capabilities.isFlying
                                    && !player.doesStatusPreventSprinting();

                    return conditionA || conditionB;
                },
                AnimationRegistry::swimmingAnimation,
                AnimationRegistry::swimmingLeaningUpdate
        );
    }

    private static void registerDiving() {
        AnimationCustom diving = AnimationUtils.createAnimation(
                DIVING_ID,
                0.8f, 0.015f, true);

        diving.registerAnimation(
                (player, axisAlignedBB) ->
                        !player.onGround
                                && !isInsideWater(player),
                (player, axisAlignedBB) ->
                        crawl_key.pressed,
                AnimationRegistry::divingAnimation,
                AnimationRegistry::divingLeaningUpdate
        );
    }

    private static void registerHighFalling() {
        AnimationCustom highFalling = AnimationUtils.createAnimation(
                HIGH_FALLING_ID,
                1.8f, 0.005f, true);

        highFalling.registerAnimation(
                (player, axisAlignedBB) ->
                        player.fallDistance >= hFalling_height
                                && !player.isSneaking()
                                && !player.capabilities.isFlying,
                (player, axisAlignedBB) ->
                        !crawl_key.pressed,
                AnimationRegistry::highFallingAnimation,
                AnimationRegistry::highFallingLeaningUpdate
        );
    }

    private static void registerSkyDiving() {
        AnimationCustom skyDiving = AnimationUtils.createAnimation(
                SKYDIVING_ID,
                1f, 0.2f, true);

        skyDiving.registerAnimation(
                (player, axisAlignedBB) ->
                        player.fallDistance >= 10
                                && !player.capabilities.isFlying,
                (player, axisAlignedBB) ->
                        !crawl_key.pressed
                                && player.isSneaking(),
                AnimationRegistry::skyDivingAnimation,
                AnimationRegistry::skyDivingLeaningUpdate
        );
    }

    private static void registerLowFalling() {
        AnimationCustom lowFalling = AnimationUtils.createAnimation(
                LOW_FALLING_ID,
                1.8f, 0.02f, false);

        lowFalling.registerAnimation(
                (player, axisAlignedBB) ->
                        player.fallDistance >= lFalling_height
                                && player.fallDistance < hFalling_height
                                && !player.isSneaking()
                                && !player.capabilities.isFlying,
                (player, axisAlignedBB) ->
                        !crawl_key.pressed,
                AnimationRegistry::lowFallingAnimation,
                AnimationRegistry::lowFallingLeaningUpdate
        );
    }

    private static void registerDashing() {
        AnimationCustom dashing = AnimationUtils.createAnimation(
                DASHING_ID,
                1.8f, 1, false, 20, 5);

        dashing.registerAnimation(
                (player, axisAlignedBB) ->
                        player.moveForward == 0 && player.onGround && !player.doesStatusPreventSprinting(),
                (player, axisAlignedBB) -> {
                    if (player.isUsingSpecialKey() || player.moveStrafing != 0) {
                        if (pressTime < 5
                                && player.isUsingSpecialKey()
                                && player.moveStrafing != 0) {

                            return true;
                        }

                        pressTime++;
                    }
                    else {
                        pressTime = 0;
                    }

                    return false;
                },
                AnimationRegistry::dashingAnimation,
                AnimationRegistry::commonLeaningUpdate
        );
    }

    private static void registerRolling() {
        AnimationCustom rolling = AnimationUtils.createAnimation(
                ROLLING_ID,
                1.8f, 1, true, 20, 25,0f);

        rolling.registerAnimation(
                (player, axisAlignedBB) ->
                        !player.doesStatusPreventSprinting()
                                && !player.inWater,
                (player, axisAlignedBB) ->
                        roll_key.pressed,
                AnimationRegistry::rollingAnimation,
                AnimationRegistry::rollingLeaningUpdate
        );
    }

    /**
     * Leaning update methods, handle player rotation (0 straight, 1 horizontal, 2 upside down)
     */
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
        float pitch = (float) ((-entity.motionY) * 1.25f);
        float goal = (pitch > 1 ? 1 : pitch) + 1;

        entity.limbSwingAmount = 0;
        entity.limbSwing = 0;

        customEntity.llm_$setLeaningPitch(goal);
    }

    public static void highFallingLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
        float goal = (entity.fallDistance - hFalling_height) / 6;

        customEntity.llm_$setLeaningPitch(goal);
    }

    public static void skyDivingLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
        float goal = entity.moveForward > 0 ? 1.2f : 1;

        customEntity.llm_$setLeaningPitch(goal);
    }

    public static void lowFallingLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        float goal = entity.ticksExisted % 200 / 1.75f;

        customEntity.llm_$setLeaningPitch(goal);
    }

    public static void rollingLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        AnimationCustom animation = customEntity.llm_$getAnimation();
        float goal = (float) animation.timeRendered / ((animation.duration - 5) / 4.5f);
        if (animation.timeRendered > (animation.duration - 5) / 4.5f * 4) goal = 4;

        customEntity.llm_$setLeaningPitch(goal);
    }

    /**
     * Copy-paste this method to make new animation, use the same style and order (rotate right arm->left arm->right leg->left leg)
     */
    private static void exampleAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, float delta) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
        float leaning = customEntity.llm_$getLeaningPitch();

        AnimationUtils.resetAnimationRotationPoints(model);

        AnimationUtils.smoothRotateAll(model.bipedBody, 0, 0, 0,
                0.6f * delta);

        model.bipedHead.rotateAngleY = i * (pi / 180.0f);
        model.bipedHead.rotateAngleX = j * (pi / 180.0f);
        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        AnimationUtils.smoothRotateAll(model.bipedRightArm, 0.5f, 0, 0.5f,
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftArm, 0.5f, 0, -0.5f,
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedRightLeg, 0.5f, 0, 0,
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftLeg, 0.5f, 0, -0,
                0.3f * delta);
    }

    /**
     * Animation rendering methods
     */
    private static void commonAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, float delta) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;

        AnimationUtils.resetAnimationRotationPoints(model);

        AnimationUtils.smoothRotateAll(model.bipedHead,
                j * (pi / 180.0f),
                i * (pi / 180.0f),
                0,
                0.75f * delta);

        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        float[] rArm = new float[3];
        float[] lArm = new float[3];
        float[] body = new float[3];

        float k = 1.0F;

        rArm[0] = MathHelper.cos(f * 0.6662F + pi) * 2.0F * g * 0.5F / k
                + (model.isRiding ? (-pi / 5) : 0);
        rArm[1] = model.aimedBow ? -0.1F + model.bipedHead.rotateAngleY : 0.0F;
        rArm[2] = model.aimedBow ? 0.1F + model.bipedHead.rotateAngleY + 0.4F : 0.0F;

        lArm[0] = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k
                + (model.isRiding ? (-pi / 5) : 0);
        lArm[1] = 0.0F;
        lArm[2] = 0.0F;

        body[0] = 0;
        body[1] = 0;
        body[2] = 0;

        rArm[0] = model.aimedBow ?
                (-pi / 2) + model.bipedHead.rotateAngleX
                : model.heldItemRight != 0 ?
                rArm[0] * 0.5f - 0.31415927f * (float) model.heldItemRight
                : rArm[0];

        lArm[0] = model.aimedBow ?
                (-pi / 2) + model.bipedHead.rotateAngleX
                : model.heldItemLeft != 0 ?
                lArm[0] * 0.5f - 0.31415927f * (float) model.heldItemLeft
                : lArm[0];

        if (!(model.onGround <= 0.0F)) {
            float onGround = model.onGround;
            body[1] = MathHelper.sin(MathHelper.sqrt_float(onGround) * (pi * 2)) * 0.2F;

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
            float v = MathHelper.sin(onGround * pi);
            float v1 = MathHelper.sin(model.onGround * pi) * -(model.bipedHead.rotateAngleX - 0.7F) * 0.75F;

            rArm[0] = (float) (rArm[0] - (v * 1.2 + v1));
            rArm[1] = rArm[1] + body[1] * 2.0F;
            rArm[2] = rArm[2] + MathHelper.sin(model.onGround * pi) * -0.4F;
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

        AnimationUtils.smoothRotateAll(model.bipedRightArm,
                rArm[0], rArm[1], rArm[2],
                0.4f * delta, 0.3f * delta, 0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftArm,
                lArm[0], lArm[1], lArm[2],
                0.4f * delta, 0.3f * delta, 0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedRightLeg,
                model.isRiding ? -1.4137167F : MathHelper.cos(f * 0.6662F) * 1.4F * g / k,
                model.isRiding ? (pi / 10) : 0,
                model.isRiding ? 0.07853982F : 0,
                0.4f * delta, 0.3f * delta, 0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftLeg,
                model.isRiding ? -1.4137167F : MathHelper.cos(f * 0.6662F + pi) * 1.4F * g / k,
                model.isRiding ? (pi / 10) : 0,
                model.isRiding ? 0.07853982F : 0,
                0.4f * delta, 0.3f * delta, 0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedBody, body[0], body[1], body[2],
                0.6f * delta);

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

    private static void swimmingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, float delta) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
        float leaningPitch = Math.min(1.0F, customMoveEntity.llm_$getLeaningPitch());

        AnimationUtils.resetAnimationRotationPoints(model);

        AnimationUtils.smoothRotateAll(model.bipedBody, 0, 0, 0,
                0.4f * delta);

        leaningPitch = entity.inWater ? 1 : leaningPitch;
        f = entity.inWater ? f : f * 2;
        g = entity.inWater ? g : g * 2;

        float[] rArm = new float[3];
        float[] lArm = new float[3];
        float[] rLeg = new float[3];
        float[] lLeg = new float[3];

        AnimationUtils.smoothRotateAll(
                model.bipedHead, leaningPitch > 0.0F ?
                        lerpAngle(leaningPitch, model.bipedHead.rotateAngleX, -pi / 4)
                        : j * (pi / 180.0f),
                i * (pi / 180.0f),
                0,
                0.4f * delta);

        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        float k = 1.0F;

        rArm[0] = MathHelper.cos(f * 0.6662F + pi) * 2.0F * g * 0.5F / k;
        rArm[1] = 0.0F;
        rArm[2] = 0.0F;

        lArm[0] = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;
        lArm[1] = 0.0F;
        lArm[2] = 0.0F;

        rLeg[0] = entity.inWater ? MathHelper.cos(f * 0.6662F) * 1.4F * g / k : 0;
        rLeg[1] = 0.0F;
        rLeg[2] = 0.0F;

        lLeg[0] = entity.inWater ? MathHelper.cos(f * 0.6662F + pi) * 1.4F * g / k : 0;
        lLeg[1] = 0.0F;
        lLeg[2] = 0.0F;

        rArm[0] = model.heldItemRight != 0 ? rArm[0] * 0.5f - 0.31415927f * (float) model.heldItemRight : rArm[0];
        lArm[0] = model.heldItemLeft != 0 ? lArm[0] * 0.5f - 0.31415927f * (float) model.heldItemLeft : lArm[0];

        if (leaningPitch > 0.0F) {
            float l = f % 26.0F;

            if (l < 14.0F) {
                rArm[0] = lerp(leaningPitch, rArm[0], 0.0F);
                rArm[1] = lerp(leaningPitch, rArm[1], pi);
                rArm[2] = lerp(leaningPitch, rArm[2], pi - 1.8707964F * method_2807(l) / method_2807(14.0F));

                lArm[0] = lerpAngle(leaningPitch, lArm[0], 0.0F);
                lArm[1] = lerpAngle(leaningPitch, lArm[1], pi);
                lArm[2] = lerpAngle(leaningPitch, lArm[2], pi + 1.8707964F * method_2807(l) / method_2807(14.0F));
            }
            else if (l >= 14.0F && l < 22.0F) {
                float o = (l - 14.0F) / 8.0F;
                rArm[0] = lerp(leaningPitch, rArm[0], (pi / 2) * o);
                rArm[1] = lerp(leaningPitch, rArm[1], pi);
                rArm[2] = lerp(leaningPitch, rArm[2], 1.2707963F + 1.8707964F * o);

                lArm[0] = lerpAngle(leaningPitch, lArm[0], (pi / 2) * o);
                lArm[1] = lerpAngle(leaningPitch, lArm[1], pi);
                lArm[2] = lerpAngle(leaningPitch, lArm[2], 5.012389F - 1.8707964F * o);
            }
            else if (l >= 22.0F && l < 26.0F) {
                float o = (l - 22.0F) / 4.0F;
                rArm[0] = lerp(leaningPitch, rArm[0], (pi / 2) - (pi / 2) * o);
                rArm[1] = lerp(leaningPitch, rArm[1], pi);
                rArm[2] = lerp(leaningPitch, rArm[2], pi);

                lArm[0] = lerpAngle(leaningPitch, lArm[0], (pi / 2) - (pi / 2) * o);
                lArm[1] = lerpAngle(leaningPitch, lArm[1], pi);
                lArm[2] = lerpAngle(leaningPitch, lArm[2], pi);
            }

            lLeg[0] = lerp(leaningPitch, lLeg[0], 0.3F * MathHelper.cos(f * 0.33333334F + pi));
            rLeg[0] = lerp(leaningPitch, rLeg[0], 0.3F * MathHelper.cos(f * 0.33333334F));

        }

        rArm[0] += model.onGround * 2;
        rArm[2] += model.onGround * 2;

        AnimationUtils.smoothRotateAll(model.bipedRightArm, rArm[0], rArm[1], rArm[2],
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftArm, lArm[0], lArm[1], lArm[2],
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedRightLeg, rLeg[0], rLeg[1], rLeg[2],
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftLeg, lLeg[0], lLeg[1], lLeg[2],
                0.3f * delta);

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

    private static void divingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, float delta) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
        float leaningPitch = Math.min(1.0F, customMoveEntity.llm_$getLeaningPitch());

        AnimationUtils.resetAnimationRotationPoints(model);

        AnimationUtils.smoothRotateAll(model.bipedBody, 0, 0, 0,
                0.5f * delta);

        AnimationUtils.smoothRotateAll(
                model.bipedHead, leaningPitch > 0.0F ?
                        lerpAngle(leaningPitch, model.bipedHead.rotateAngleX, -pi / 4)
                        : j * (pi / 180.0f),
                i * (pi / 180.0f),
                0,
                0.4f * delta);

        if (leaningPitch > 0.0F) {
            AnimationUtils.smoothRotateAll(model.bipedRightArm,
                    lerp(leaningPitch, model.bipedRightArm.rotateAngleX, 0.0F),
                    lerp(leaningPitch, model.bipedRightArm.rotateAngleY, pi),
                    lerp(leaningPitch, model.bipedRightArm.rotateAngleZ, pi - 1.8707964F * method_2807(0) / method_2807(14.0F)),
                    0.15f * delta
            );

            AnimationUtils.smoothRotateAll(model.bipedLeftArm,
                    lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleX, 0.0F),
                    lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleY, pi),
                    lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleZ, pi + 1.8707964F * method_2807(0) / method_2807(14.0F)),
                    0.15f * delta
            );

            float n = customMoveEntity.llm_$getLeaningPitch();

            model.bipedRightLeg.rotationPointY = 11.3f;
            model.bipedLeftLeg.rotationPointY = 11.3f;

            AnimationUtils.smoothRotateAll(model.bipedRightLeg, -1f + n / 2, 0, 0,
                    delta);

            AnimationUtils.smoothRotateAll(model.bipedLeftLeg, -1f + n / 2, 0, 0,
                    delta);
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

    private static void highFallingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, float delta) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
        float leaning = customEntity.llm_$getLeaningPitch();

        AnimationUtils.resetAnimationRotationPoints(model);

        AnimationUtils.smoothRotateAll(model.bipedBody, 0, 0, 0,
                0.4f * delta);

        AnimationUtils.smoothRotateAll(model.bipedHead, 0.25f, i * (pi / 180.0f), 0,
                0.4f * delta);

        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        float sin = MathHelper.sin(leaning);
        float cos = MathHelper.cos(leaning);
        float cos1 = MathHelper.cos(leaning + 2);
        float sin1 = MathHelper.sin(leaning + 2);

        AnimationUtils.smoothRotateAll(model.bipedRightArm, cos, 0, 1.75f + sin,
                0.4f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftArm, cos1, 0, -1.75f - sin1,
                0.4f * delta);

        AnimationUtils.smoothRotateAll(model.bipedRightLeg, cos * 1.5f, 0, 0,
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftLeg, sin * 1.5f, 0, 0,
                0.3f * delta);
    }

    private static void skyDivingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, float delta) {
        AnimationUtils.resetAnimationRotationPoints(model);

        AnimationUtils.smoothRotateAll(model.bipedBody, 0, 0, 0,
                0.4f * delta);

        AnimationUtils.smoothRotateAll(model.bipedHead, -0.5f, i * (pi / 180.0f), 0,
                0.4f * delta);

        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        boolean fMove = entity.moveForward > 0;

        AnimationUtils.smoothRotateAll(model.bipedRightArm, 0.5f, 0, fMove ? 0.5f : 2,
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftArm, 0.5f, 0, fMove ? -0.5f : -2,
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedRightLeg, 0.5f, 0, 0.15f,
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftLeg, 0.5f, 0, -0.15f,
                0.3f * delta);
    }

    private static void lowFallingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, float delta) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
        float leaning = customEntity.llm_$getLeaningPitch();

        AnimationUtils.resetAnimationRotationPoints(model);

        AnimationUtils.smoothRotateAll(model.bipedBody, 0, 0, 0,
                0.6f * delta);

        AnimationUtils.smoothRotateAll(model.bipedHead, j * (pi / 180.0f), i * (pi / 180.0f), 0,
                0.4f * delta);

        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        float sin = MathHelper.sin(leaning);
        float cos = MathHelper.cos(leaning);
        float cos1 = MathHelper.cos(leaning + 2);
        float sin1 = MathHelper.sin(leaning + 2);

        AnimationUtils.smoothRotateAll(model.bipedRightArm, cos * 0.65f, 0, 1.75f + sin * 0.65f,
                0.6f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftArm, cos1 * 0.65f, 0, -1.75f - sin1 * 0.65f,
                0.6f * delta);

        AnimationUtils.smoothRotateAll(model.bipedRightLeg, -sin * 0.5f, 0, 0,
                0.7f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftLeg, sin * 0.5f, 0, 0,
                0.7f * delta);
    }

    private static void dashingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, float delta) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        AnimationUtils.resetAnimationRotationPoints(model);

        AnimationUtils.smoothRotateAll(model.bipedBody, 0, 0, 0,
                0.8f * delta);

        AnimationUtils.smoothRotateAll(model.bipedHead,
                j * (pi / 180.0f),
                i * (pi / 180.0f),
                0,
                0.4f * delta);

        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        float side = customEntity.llm_$getSideValue();

        entity.renderYawOffset = entity.rotationYawHead + 45 * side;

        AnimationUtils.smoothRotateAll(model.bipedRightArm, pi * 0.5f, side, side * pi * 0.25f,
                0.6f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftArm, pi * 0.5f, side, side * pi * 0.25f,
                0.6f * delta);

        AnimationUtils.smoothRotateAll(model.bipedRightLeg, pi * 0.25f, 0, side * -pi * 0.125f,
                0.6f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftLeg, pi * 0.25f, 0, side * -pi * 0.125f,
                0.6f * delta);
    }

    private static void rollingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, float delta) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        AnimationUtils.resetAnimationRotationPoints(model);

        AnimationUtils.smoothRotateAll(model.bipedBody, 0, 0, 0,
                0.6f * delta);

        AnimationCustom animation = customEntity.llm_$getAnimation();

        float[] head = new float[]{0, 0, 0};
        float[] rArm = new float[]{0, 0, 0};
        float[] lArm = new float[]{0, 0, 0};
        float[] rLeg = new float[]{0, 0, 0};
        float[] lLeg = new float[]{0, 0, 0};

        int t = animation.timeRendered;
        if (t < 10) {
            customEntity.llm_$getAnimation().height = 1.8f - t / 10f;
            rArm[0] = -pi;
            lArm[0] = -pi;
            rLeg[0] = -pi * 0.25f;
            lLeg[0] = -pi * 0.25f;
            head[0] = pi * 0.25f;
            head[1] = 0;
        }
        else if (t < 20) {
            if (t > 15) customEntity.llm_$getAnimation().height = 0.8f + (t - 15) / 5f;
            else customEntity.llm_$getAnimation().height = 0.8f;

            rArm[0] = -pi * 0.8f;
            lArm[0] = -pi * 0.8f;
            rLeg[0] = -pi * 0.5f;
            lLeg[0] = -pi * 0.5f;
            head[0] = pi * 0.4f;
            head[1] = 0;
        }
        else {
            customEntity.llm_$getAnimation().height = 1.8f;
            head[0] = j * (pi / 180.0f);
            head[1] = i * (pi / 180.0f);
        }

        AnimationUtils.smoothRotateAll(model.bipedHead, head,
                0.4f * delta);

        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        AnimationUtils.smoothRotateAll(model.bipedRightArm, rArm,
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftArm, lArm,
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedRightLeg, rLeg,
                0.3f * delta);

        AnimationUtils.smoothRotateAll(model.bipedLeftLeg, lLeg,
                0.3f * delta);
    }
}
