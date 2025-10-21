package net.dravigen.letMeMove.animation.customs;

import net.dravigen.letMeMove.animation.BaseAnimation;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.letMeMove.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.letMeMove.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.letMeMove.utils.GeneralUtils.pi;

public abstract class AnimCommon extends BaseAnimation {
	public AnimCommon(ResourceLocation id, float height, float speedModifier,
			boolean needYOffsetUpdate, int maxCooldown, int duration, boolean shouldAutoUpdate, float yOffset) {
		super(id, height, speedModifier, needYOffsetUpdate, maxCooldown, duration, shouldAutoUpdate,
				yOffset);
	}
	
	public AnimCommon(ResourceLocation id, float height, float speedModifier) {
		super(id, height, speedModifier);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return false;
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return false;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
		
		resetAnimationRotationPoints(model);
		
		smoothRotateAll(model.bipedHead, j * (pi / 180.0f), i * (pi / 180.0f), 0, 0.75f * delta);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		float[] rArm = new float[3];
		float[] lArm = new float[3];
		float[] body = new float[3];
		
		float k = 1.0F;
		
		rArm[0] = MathHelper.cos(f * 0.6662F + pi) * 2.0F * g * 0.5F / k + (model.isRiding ? (-pi / 5) : 0);
		rArm[1] = model.aimedBow ? -0.1F + model.bipedHead.rotateAngleY : 0.0F;
		rArm[2] = model.aimedBow ? 0.1F + model.bipedHead.rotateAngleY + 0.4F : 0.0F;
		
		lArm[0] = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k + (model.isRiding ? (-pi / 5) : 0);
		lArm[1] = 0.0F;
		lArm[2] = 0.0F;
		
		body[0] = 0;
		body[1] = 0;
		body[2] = 0;
		
		rArm[0] = model.aimedBow ? (-pi / 2) + model.bipedHead.rotateAngleX : model.heldItemRight != 0 ? rArm[0] * 0.5f - 0.31415927f * (float) model.heldItemRight : rArm[0];
		
		lArm[0] = model.aimedBow ? (-pi / 2) + model.bipedHead.rotateAngleX : model.heldItemLeft != 0 ? lArm[0] * 0.5f - 0.31415927f * (float) model.heldItemLeft : lArm[0];
		
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
		
		boolean isCrouching = customMoveEntity.llm_$isAnimation(AnimCrouching.id);
		
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
		
		smoothRotateAll(model.bipedRightArm, rArm[0], rArm[1], rArm[2], 0.4f * delta, 0.3f * delta, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm[0], lArm[1], lArm[2], 0.4f * delta, 0.3f * delta, 0.3f * delta);
		
		smoothRotateAll(model.bipedRightLeg, model.isRiding ? -1.4137167F : MathHelper.cos(f * 0.6662F) * 1.4F * g / k,
				model.isRiding ? (pi / 10) : 0, model.isRiding ? 0.07853982F : 0, 0.4f * delta, 0.3f * delta,
				0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg,
				model.isRiding ? -1.4137167F : MathHelper.cos(f * 0.6662F + pi) * 1.4F * g / k,
				model.isRiding ? (pi / 10) : 0, model.isRiding ? 0.07853982F : 0, 0.4f * delta, 0.3f * delta,
				0.3f * delta);
		
		smoothRotateAll(model.bipedBody, body[0], body[1], body[2], 0.6f * delta);
		
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
	
	@Override
	public void updateLeaning(EntityLivingBase entity) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		float goal = 0;
		
		customEntity.llm_$setLeaningPitch(goal);
	}
}
