package net.dravigen.letMeMove.animation.customs;

import net.dravigen.letMeMove.animation.BaseAnimation;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.letMeMove.LetMeMoveAddon.crawl_key;
import static net.dravigen.letMeMove.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.letMeMove.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.letMeMove.utils.GeneralUtils.*;

public class AnimSwimming extends BaseAnimation {
	public static final ResourceLocation id = new ResourceLocation("LMM", "swimming");
	
	public AnimSwimming() {
		super(id, 0.8f, 0.15f, true);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !player.capabilities.isFlying && (player.onGround || isInsideWater(player));
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		boolean conditionA = crawl_key.pressed && (player.onGround || isInsideWater(player));
		
		boolean conditionB = (isInsideWater(player) && player.getLookVec().yCoord < 0.45 ||
				isHeadInsideWater(player)) &&
				player.isUsingSpecialKey() &&
				player.moveForward > 0 &&
				!player.capabilities.isFlying &&
				!player.doesStatusPreventSprinting();
		
		return conditionA || conditionB;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
		float leaningPitch = Math.min(1.0F, customMoveEntity.llm_$getLeaningPitch());
		
		resetAnimationRotationPoints(model);
		
		smoothRotateAll(model.bipedBody, 0, 0, 0, 0.4f * delta);
		
		leaningPitch = entity.inWater ? 1 : leaningPitch;
		f = entity.inWater ? f : f * 2;
		g = entity.inWater ? g : g * 2;
		
		float[] rArm = new float[3];
		float[] lArm = new float[3];
		float[] rLeg = new float[3];
		float[] lLeg = new float[3];
		
		smoothRotateAll(model.bipedHead,
						leaningPitch > 0.0F
						? lerpAngle(leaningPitch, model.bipedHead.rotateAngleX, -pi / 4)
						: j * (pi / 180.0f),
						i * (pi / 180.0f),
						0,
						1);
		
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
		
		smoothRotateAll(model.bipedRightArm, rArm[0], rArm[1], rArm[2], 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm[0], lArm[1], lArm[2], 0.3f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg[0], rLeg[1], rLeg[2], 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg[0], lLeg[1], lLeg[2], 0.3f * delta);
		
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
	
	@Override
	public void updateLeaning(EntityLivingBase entity) {
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
}
