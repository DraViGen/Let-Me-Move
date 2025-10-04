package net.dravigen.letMeMove.render;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.AnimationUtils;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;


import static net.dravigen.letMeMove.utils.GeneralUtils.*;


public class AnimationRegistry {
    public final static ResourceLocation STANDING_ID = new ResourceLocation("LMM","standing");
    public final static ResourceLocation CROUCHING_ID = new ResourceLocation("LMM","crouching");
    public final static ResourceLocation SWIMMING_ID = new ResourceLocation("LMM","crawling");
    public final static ResourceLocation DIVING_ID = new ResourceLocation("LMM","diving");
    public final static ResourceLocation FALLING_ID = new ResourceLocation("LMM","falling");


    public static void registerAllAnimation() {
        AnimationCustom standing = AnimationUtils.registerAnimation(STANDING_ID, 1.8f, 1);
        registerStanding(standing);

        AnimationCustom crouching = AnimationUtils.registerAnimation(CROUCHING_ID, 1.4f, 0.3f);
        registerCrouching(crouching);

        AnimationCustom swimming = AnimationUtils.registerAnimation(SWIMMING_ID, 0.8f, 0.15f);
        registerSwimming(swimming);

        AnimationCustom diving = AnimationUtils.registerAnimation(DIVING_ID, 1f, 0.15f);
        registerDiving(diving);

        //AnimationCustom falling = registerAnimation(FALLING_ID, 1.8f, 1);

    }

    private static void registerStanding(AnimationCustom standing) {
        standing.setConditions(((player, bb) -> false));
        standing.setAnimationRender(((mc, model, entity, f, g, h, i, j, u) -> commonAnimation(model, entity, f, g, h, i, j)));
        standing.setLeaningUpdate((AnimationRegistry::commonLeaningUpdate));
    }

    private static void registerCrouching(AnimationCustom crouching) {
        crouching.setConditions(((player, bb) -> (player.isSneaking() && player.worldObj.getCollidingBlockBounds(bb).isEmpty())));
        crouching.setAnimationRender(((mc, model, entity, f, g, h, i, j, u) -> commonAnimation(model, entity, f, g, h, i, j)));
        crouching.setLeaningUpdate((AnimationRegistry::commonLeaningUpdate));
    }

    private static void registerSwimming(AnimationCustom swimming) {
        swimming.setConditions(((player, bb) -> (Keyboard.isKeyDown(Keyboard.KEY_C) && (player.onGround || isInsideWater(player)))
                || ((isInsideWater(player) && player.getLookVec().yCoord < 0.45 || isHeadInsideWater(player)) && player.isUsingSpecialKey() && player.moveForward > 0 && !player.capabilities.isFlying)));
        swimming.setAnimationRender(((mc, model, entity, f, g, h, i, j, u) -> swimmingAnimation(model, entity, f, g, h, i, j)));
        swimming.setLeaningUpdate((AnimationRegistry::swimmingLeaningUpdate));
    }

    private static void registerDiving(AnimationCustom diving) {
        diving.setConditions(((player, bb) -> Keyboard.isKeyDown(Keyboard.KEY_C) && !player.onGround && !isInsideWater(player)));
        diving.setAnimationRender(((mc, model, entity, f, g, h, i, j, u) -> divingAnimation(model, entity, h, i, j)));
        diving.setLeaningUpdate((AnimationRegistry::divingLeaningUpdate));
    }

    public static void commonLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
        float goal = 0;

        smoothUpdate(goal, customEntity);
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

        smoothUpdate(goal, customEntity);
    }

    public static void divingLeaningUpdate(EntityLivingBase entity) {
        ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;

        entity.limbSwingAmount = 0;
        entity.limbSwing = 0;

        float pitch = (float) ((-entity.motionY) * 1.25f);
        float goal = (pitch > 1 ? 1 : pitch) + 1;

        smoothUpdate(goal, customEntity);
    }

    private static void smoothUpdate(float goal, ICustomMovementEntity customEntity) {
        float leaningPitch = customEntity.llm_$getLeaningPitch();
        float difference = goal - leaningPitch;

        if (Math.abs(difference) <= 0.0125) {
            customEntity.llm_$setLeaningPitch(goal);
        }
        else {
            if (difference > 0) {
                customEntity.llm_$setLeaningPitch(leaningPitch + 0.0125f);
            }
            else {
                customEntity.llm_$setLeaningPitch(leaningPitch - 0.0125f);
            }
        }
    }

    private static void exampleAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j) {
        GL11.glPushMatrix();

        model.bipedHead.rotateAngleY = i * (float) (Math.PI / 180.0);
        model.bipedHead.rotateAngleX = j * (float) (Math.PI / 180.0);
        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        model.bipedBody.rotateAngleY = 0.0F;

        model.bipedRightArm.rotationPointZ = 0.0F;
        model.bipedRightArm.rotationPointX = -5.0F;

        model.bipedLeftArm.rotationPointZ = 0.0F;
        model.bipedLeftArm.rotationPointX = 5.0F;

        float k = 1.0F;

        model.bipedRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / k;
        model.bipedLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;

        model.bipedRightArm.rotateAngleZ = 0.0F;
        model.bipedLeftArm.rotateAngleZ = 0.0F;

        model.bipedRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * g / k;
        model.bipedLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / k;

        model.bipedRightLeg.rotateAngleY = 0.0F;
        model.bipedLeftLeg.rotateAngleY = 0.0F;

        model.bipedRightLeg.rotateAngleZ = 0.0F;
        model.bipedLeftLeg.rotateAngleZ = 0.0F;

        if (model.isRiding) {
            model.bipedRightArm.rotateAngleX += (float) (-Math.PI / 5);
            model.bipedLeftArm.rotateAngleX += (float) (-Math.PI / 5);

            model.bipedRightLeg.rotateAngleX = -1.4137167F;
            model.bipedRightLeg.rotateAngleY = (float) (Math.PI / 10);

            model.bipedRightLeg.rotateAngleZ = 0.07853982F;
            model.bipedLeftLeg.rotateAngleX = -1.4137167F;

            model.bipedLeftLeg.rotateAngleY = (float) (-Math.PI / 10);
            model.bipedLeftLeg.rotateAngleZ = -0.07853982F;
        }

        if (model.heldItemLeft != 0) {
            model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX * 0.5f - 0.31415927f * (float) model.heldItemLeft;
        }
        if (model.heldItemRight != 0) {
            model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX * 0.5f - 0.31415927f * (float) model.heldItemRight;
        }

        model.bipedRightArm.rotateAngleY = 0.0F;
        model.bipedLeftArm.rotateAngleY = 0.0F;

        if (model.aimedBow) {
            model.bipedRightArm.rotateAngleY = -0.1F + model.bipedHead.rotateAngleY;
            model.bipedLeftArm.rotateAngleY = 0.1F + model.bipedHead.rotateAngleY + 0.4F;
            model.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2) + model.bipedHead.rotateAngleX;
            model.bipedLeftArm.rotateAngleX = (float) (-Math.PI / 2) + model.bipedHead.rotateAngleX;
        }

        handSwinging(entity, model, h);

        model.bipedBody.rotateAngleX = 0.0F;

        model.bipedRightLeg.rotationPointZ = 0.1F;
        model.bipedLeftLeg.rotationPointZ = 0.1F;

        model.bipedRightLeg.rotationPointY = 12.0F;
        model.bipedLeftLeg.rotationPointY = 12.0F;

        model.bipedHead.rotationPointY = 0.0F;
        model.bipedHeadwear.rotationPointY = 0.0f;

        model.bipedBody.rotationPointY = 0.0F;

        model.bipedLeftArm.rotationPointY = 2.0F;
        model.bipedRightArm.rotationPointY = 2.0F;

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
        GL11.glPopMatrix();
    }

    private static void commonAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
        GL11.glPushMatrix();

        model.bipedHead.rotateAngleY = i * (float) (Math.PI / 180.0);
        model.bipedHead.rotateAngleX = j * (float) (Math.PI / 180.0);
        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        model.bipedBody.rotateAngleY = 0.0F;

        model.bipedRightArm.rotationPointZ = 0.0F;
        model.bipedRightArm.rotationPointX = -5.0F;

        model.bipedLeftArm.rotationPointZ = 0.0F;
        model.bipedLeftArm.rotationPointX = 5.0F;

        float k = 1.0F;

        model.bipedRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / k;
        model.bipedLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;

        model.bipedRightArm.rotateAngleZ = 0.0F;
        model.bipedLeftArm.rotateAngleZ = 0.0F;

        model.bipedRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * g / k;
        model.bipedLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / k;

        model.bipedRightLeg.rotateAngleY = 0.0F;
        model.bipedLeftLeg.rotateAngleY = 0.0F;

        model.bipedRightLeg.rotateAngleZ = 0.0F;
        model.bipedLeftLeg.rotateAngleZ = 0.0F;

        if (model.isRiding) {
            model.bipedRightArm.rotateAngleX += (float) (-Math.PI / 5);
            model.bipedLeftArm.rotateAngleX += (float) (-Math.PI / 5);

            model.bipedRightLeg.rotateAngleX = -1.4137167F;
            model.bipedRightLeg.rotateAngleY = (float) (Math.PI / 10);

            model.bipedRightLeg.rotateAngleZ = 0.07853982F;
            model.bipedLeftLeg.rotateAngleX = -1.4137167F;

            model.bipedLeftLeg.rotateAngleY = (float) (-Math.PI / 10);
            model.bipedLeftLeg.rotateAngleZ = -0.07853982F;
        }

        if (model.heldItemLeft != 0) {
            model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX * 0.5f - 0.31415927f * (float) model.heldItemLeft;
        }
        if (model.heldItemRight != 0) {
            model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX * 0.5f - 0.31415927f * (float) model.heldItemRight;
        }

        model.bipedRightArm.rotateAngleY = 0.0F;
        model.bipedLeftArm.rotateAngleY = 0.0F;

        if (model.aimedBow) {
            model.bipedRightArm.rotateAngleY = -0.1F + model.bipedHead.rotateAngleY;
            model.bipedLeftArm.rotateAngleY = 0.1F + model.bipedHead.rotateAngleY + 0.4F;
            model.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2) + model.bipedHead.rotateAngleX;
            model.bipedLeftArm.rotateAngleX = (float) (-Math.PI / 2) + model.bipedHead.rotateAngleX;
        }

        handSwinging(entity, model, h);

        boolean isCrouching = customMoveEntity.llm_$isAnimation(CROUCHING_ID);
        if (isCrouching) {
            model.bipedBody.rotateAngleX = 0.5F;

            model.bipedRightArm.rotateAngleX += 0.4F;
            model.bipedLeftArm.rotateAngleX += 0.4F;

            model.bipedRightLeg.rotationPointZ = 4.0F;
            model.bipedLeftLeg.rotationPointZ = 4.0F;

            model.bipedRightLeg.rotationPointY = 12.2F;
            model.bipedLeftLeg.rotationPointY = 12.2F;

            model.bipedHead.rotationPointY = 4.2F;
            model.bipedHeadwear.rotationPointY = 4.2f;

            model.bipedBody.rotationPointY = 3.2F;

            model.bipedLeftArm.rotationPointY = 5.2F;
            model.bipedRightArm.rotationPointY = 5.2F;
        }
        else {
            model.bipedBody.rotateAngleX = 0.0F;

            model.bipedRightLeg.rotationPointZ = 0.1F;
            model.bipedLeftLeg.rotationPointZ = 0.1F;

            model.bipedRightLeg.rotationPointY = 12.0F;
            model.bipedLeftLeg.rotationPointY = 12.0F;

            model.bipedHead.rotationPointY = 0.0F;
            model.bipedHeadwear.rotationPointY = 0.0f;

            model.bipedBody.rotationPointY = 0.0F;

            model.bipedLeftArm.rotationPointY = 2.0F;
            model.bipedRightArm.rotationPointY = 2.0F;
        }

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
        GL11.glPopMatrix();
    }

    private static void swimmingAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
        GL11.glPushMatrix();

        float leaningPitch = Math.min(1.0F, customMoveEntity.llm_$getLeaningPitch());

        rotateEntity(leaningPitch);

        leaningPitch = entity.inWater ? 1 : leaningPitch;

        model.bipedHead.rotateAngleY = i * (float) (Math.PI / 180.0);
        if (leaningPitch > 0.0F) {
            model.bipedHead.rotateAngleX = lerpAngle(leaningPitch, model.bipedHead.rotateAngleX, (float) (-Math.PI / 4));
        }
        else {
            model.bipedHead.rotateAngleX = j * (float) (Math.PI / 180.0);
        }
        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        model.bipedBody.rotateAngleY = 0.0F;

        model.bipedRightArm.rotationPointZ = 0.0F;
        model.bipedRightArm.rotationPointX = -5.0F;

        model.bipedLeftArm.rotationPointZ = 0.0F;
        model.bipedLeftArm.rotationPointX = 5.0F;

        float k = 1.0F;

        model.bipedRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / k;
        model.bipedLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;

        model.bipedRightArm.rotateAngleZ = 0.0F;
        model.bipedLeftArm.rotateAngleZ = 0.0F;

        if (entity.inWater) {
            model.bipedRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * g / k;
            model.bipedLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / k;
        }
        else {
            model.bipedRightLeg.rotateAngleX = 0;
            model.bipedLeftLeg.rotateAngleX = 0;
        }

        model.bipedRightLeg.rotateAngleY = 0.0F;
        model.bipedLeftLeg.rotateAngleY = 0.0F;

        model.bipedRightLeg.rotateAngleZ = 0.0F;
        model.bipedLeftLeg.rotateAngleZ = 0.0F;

        if (model.heldItemLeft != 0) {
            model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX * 0.5f - 0.31415927f * (float) model.heldItemLeft;
        }
        if (model.heldItemRight != 0) {
            model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX * 0.5f - 0.31415927f * (float) model.heldItemRight;
        }

        model.bipedRightArm.rotateAngleY = 0.0F;
        model.bipedLeftArm.rotateAngleY = 0.0F;

        if (model.aimedBow) {
            model.bipedRightArm.rotateAngleY = -0.1F + model.bipedHead.rotateAngleY;
            model.bipedLeftArm.rotateAngleY = 0.1F + model.bipedHead.rotateAngleY + 0.4F;
            model.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2) + model.bipedHead.rotateAngleX;
            model.bipedLeftArm.rotateAngleX = (float) (-Math.PI / 2) + model.bipedHead.rotateAngleX;
        }

        handSwinging(entity, model, h);

        model.bipedBody.rotateAngleX = 0.0F;

        model.bipedRightLeg.rotationPointZ = 0.1F;
        model.bipedLeftLeg.rotationPointZ = 0.1F;

        model.bipedRightLeg.rotationPointY = 12.0F;
        model.bipedLeftLeg.rotationPointY = 12.0F;

        model.bipedHead.rotationPointY = 0.0F;
        model.bipedHeadwear.rotationPointY = 0.0f;

        model.bipedBody.rotationPointY = 0.0F;

        model.bipedLeftArm.rotationPointY = 2.0F;
        model.bipedRightArm.rotationPointY = 2.0F;

        if (leaningPitch > 0.0F) {
            float l = f % 26.0F;
            float m = model.onGround > 0.0F ? 0.0F : leaningPitch;
            if (l < 14.0F) {
                model.bipedLeftArm.rotateAngleX = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleX, 0.0F);
                model.bipedRightArm.rotateAngleX = lerp(m, model.bipedRightArm.rotateAngleX, 0.0F);

                model.bipedLeftArm.rotateAngleY = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleY, (float) Math.PI);
                model.bipedRightArm.rotateAngleY = lerp(m, model.bipedRightArm.rotateAngleY, (float) Math.PI);

                model.bipedLeftArm.rotateAngleZ = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleZ, (float) Math.PI + 1.8707964F * method_2807(l) / method_2807(14.0F));
                model.bipedRightArm.rotateAngleZ = lerp(m, model.bipedRightArm.rotateAngleZ, (float) Math.PI - 1.8707964F * method_2807(l) / method_2807(14.0F));
            }
            else if (l >= 14.0F && l < 22.0F) {
                float o = (l - 14.0F) / 8.0F;
                model.bipedLeftArm.rotateAngleX = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleX, (float) (Math.PI / 2) * o);
                model.bipedRightArm.rotateAngleX = lerp(m, model.bipedRightArm.rotateAngleX, (float) (Math.PI / 2) * o);

                model.bipedLeftArm.rotateAngleY = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleY, (float) Math.PI);
                model.bipedRightArm.rotateAngleY = lerp(m, model.bipedRightArm.rotateAngleY, (float) Math.PI);

                model.bipedLeftArm.rotateAngleZ = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleZ, 5.012389F - 1.8707964F * o);
                model.bipedRightArm.rotateAngleZ = lerp(m, model.bipedRightArm.rotateAngleZ, 1.2707963F + 1.8707964F * o);
            }
            else if (l >= 22.0F && l < 26.0F) {
                float o = (l - 22.0F) / 4.0F;
                model.bipedLeftArm.rotateAngleX = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleX, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
                model.bipedRightArm.rotateAngleX = lerp(m, model.bipedRightArm.rotateAngleX, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);

                model.bipedLeftArm.rotateAngleY = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleY, (float) Math.PI);
                model.bipedRightArm.rotateAngleY = lerp(m, model.bipedRightArm.rotateAngleY, (float) Math.PI);

                model.bipedLeftArm.rotateAngleZ = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleZ, (float) Math.PI);
                model.bipedRightArm.rotateAngleZ = lerp(m, model.bipedRightArm.rotateAngleZ, (float) Math.PI);
            }

            model.bipedLeftLeg.rotateAngleX = lerp(leaningPitch, model.bipedLeftLeg.rotateAngleX, 0.3F * MathHelper.cos(f * 0.33333334F + (float) Math.PI));
            model.bipedRightLeg.rotateAngleX = lerp(leaningPitch, model.bipedRightLeg.rotateAngleX, 0.3F * MathHelper.cos(f * 0.33333334F));

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
        GL11.glPopMatrix();
    }

    private static void divingAnimation(ModelBiped model, EntityLivingBase entity, float h, float i, float j) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;

        GL11.glPushMatrix();

        float leaningPitch = Math.min(1.0F, customMoveEntity.llm_$getLeaningPitch());

        rotateEntity(leaningPitch);

        model.bipedHead.rotateAngleY = i * (float) (Math.PI / 180.0);
        if (leaningPitch > 0.0F) {
            model.bipedHead.rotateAngleX = lerpAngle(leaningPitch, model.bipedHead.rotateAngleX, (float) (-Math.PI / 4));
        }
        else {
            model.bipedHead.rotateAngleX = j * (float) (Math.PI / 180.0);
        }
        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;

        model.bipedBody.rotateAngleY = 0.0F;

        model.bipedRightArm.rotationPointZ = 0.0F;
        model.bipedRightArm.rotationPointX = -5.0F;

        model.bipedLeftArm.rotationPointZ = 0.0F;
        model.bipedLeftArm.rotationPointX = 5.0F;

        model.bipedRightArm.rotateAngleX = 0;
        model.bipedLeftArm.rotateAngleX = 0;

        model.bipedRightArm.rotateAngleZ = 0.0F;
        model.bipedLeftArm.rotateAngleZ = 0.0F;

        model.bipedRightLeg.rotateAngleX = 0;
        model.bipedLeftLeg.rotateAngleX = 0;

        model.bipedRightLeg.rotateAngleY = 0.0F;
        model.bipedLeftLeg.rotateAngleY = 0.0F;

        model.bipedRightLeg.rotateAngleZ = 0.0F;
        model.bipedLeftLeg.rotateAngleZ = 0.0F;

        if (model.heldItemLeft != 0) {
            model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX * 0.5f - 0.31415927f * (float) model.heldItemLeft;
        }
        if (model.heldItemRight != 0) {
            model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX * 0.5f - 0.31415927f * (float) model.heldItemRight;
        }

        model.bipedRightArm.rotateAngleY = 0.0F;
        model.bipedLeftArm.rotateAngleY = 0.0F;

        handSwinging(entity, model, h);

        model.bipedBody.rotateAngleX = 0.0F;

        model.bipedRightLeg.rotationPointZ = 0.1F;
        model.bipedLeftLeg.rotationPointZ = 0.1F;

        model.bipedRightLeg.rotationPointY = 12.0F;
        model.bipedLeftLeg.rotationPointY = 12.0F;

        model.bipedHead.rotationPointY = 0.0F;
        model.bipedHeadwear.rotationPointY = 0.0f;

        model.bipedBody.rotationPointY = 0.0F;

        model.bipedLeftArm.rotationPointY = 2.0F;
        model.bipedRightArm.rotationPointY = 2.0F;

        if (leaningPitch > 0.0F) {
            float m = model.onGround > 0.0F ? 0.0F : leaningPitch;
            model.bipedLeftArm.rotateAngleX = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleX, 0.0F);
            model.bipedRightArm.rotateAngleX = lerp(m, model.bipedRightArm.rotateAngleX, 0.0F);

            model.bipedLeftArm.rotateAngleY = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleY, (float) Math.PI);
            model.bipedRightArm.rotateAngleY = lerp(m, model.bipedRightArm.rotateAngleY, (float) Math.PI);

            model.bipedLeftArm.rotateAngleZ = lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleZ, (float) Math.PI + 1.8707964F * method_2807(0) / method_2807(14.0F));
            model.bipedRightArm.rotateAngleZ = lerp(m, model.bipedRightArm.rotateAngleZ, (float) Math.PI - 1.8707964F * method_2807(0) / method_2807(14.0F));

            model.bipedLeftLeg.rotateAngleX = lerp(leaningPitch, model.bipedLeftLeg.rotateAngleX, 0.3F * MathHelper.cos((float) Math.PI));
            model.bipedRightLeg.rotateAngleX = lerp(leaningPitch, model.bipedRightLeg.rotateAngleX, 0.3F * MathHelper.cos(0));

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
        GL11.glPopMatrix();
    }

    private static void rotateEntity(float leaningPitch) {
        float var1 = 1.62f - (1.62f - 1.25f) * leaningPitch;
        GL11.glTranslatef(0, var1, 0);
        GL11.glRotatef(90 * leaningPitch, 1, 0, 0);
    }
}
