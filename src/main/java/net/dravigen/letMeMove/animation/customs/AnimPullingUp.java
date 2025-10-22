package net.dravigen.letMeMove.animation.customs;

import net.dravigen.letMeMove.animation.BaseAnimation;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.letMeMove.utils.AnimationUtils.*;
import static net.dravigen.letMeMove.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.letMeMove.utils.GeneralUtils.*;
import static net.dravigen.letMeMove.utils.GeneralUtils.pi;

public class AnimPullingUp extends AnimCommon {
	public static final ResourceLocation id = new ResourceLocation("LMM", "wallPulling");
	private static double yBlockAboveWall;
	
	public AnimPullingUp() {
		super(id, 1.8f, 1f, false, 20, 40, false, 0);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		double minY = player.boundingBox.minY;
		
		return (player.onGround || player.motionY < 0) &&
				!player.inWater &&
				player.canJump() &&
				checkIfEntityFacingWall(player) &&
				(yBlockAboveWall = getWallTopYIfEmptySpace(player)) != -1 &&
				!(yBlockAboveWall - minY < 1.5 && player.onGround);
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return player.isSneaking() && player.moveForward > 0;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		BaseAnimation animation = customEntity.llm_$getAnimation();
		
		if (animation.timeRendered < 0 ||
				!entity.isSneaking() ||
				getWallSide(entity, 0, entity.height) == null ||
				getWallTopYIfEmptySpace(entity) == -1) {
			animation.timeRendered = animation.duration;
			
			return;
		}
		
		resetAnimationRotationPoints(model);
		
		float[] body = new float[]{0, 0, 0};
		float[] rArm = new float[]{0, 0, 0};
		float[] lArm = new float[]{0, 0, 0};
		float[] rLeg = new float[]{0, 0, 0};
		float[] lLeg = new float[]{0, 0, 0};
		
		animation.timeRendered = MathHelper.floor_double((2 - ((yBlockAboveWall + 0.05) - entity.boundingBox.minY)) *
																 animation.duration / 2);
		
		int t = animation.timeRendered;
		
		offsetAllRotationPoints(model, 0, 0, -2);
		
		rArm[0] = -pi(7, 8) + pi(7, 8) * t / 35;
		lArm[0] = -pi(7, 8) + pi(7, 8) * t / 35;
		
		if (entity.moveStrafing != 0 && entity.moveForward == 0) {
			rArm[2] = sin(entity.ticksExisted / 8f) / 2f;
			lArm[2] = sin(-entity.ticksExisted / 8f) / 2f;
			rArm[1] = sin(-entity.ticksExisted / 8f) / 2f;
			lArm[1] = sin(entity.ticksExisted / 8f) / 2f;
		}
		
		if (t > 20) {
			animation.height = 1.4f;
			
			body[0] = 0.5F;
			
			rArm[0] += 0.4F;
			model.bipedRightArm.rotationPointY = 5.2F;
			
			lArm[0] += 0.4F;
			model.bipedLeftArm.rotationPointY = 5.2F;
			
			model.bipedRightLeg.rotationPointY = 12.2F;
			model.bipedRightLeg.rotationPointZ += 4.0F;
			
			model.bipedLeftLeg.rotationPointY = 12.2F;
			model.bipedLeftLeg.rotationPointZ += 4.0F;
			
			model.bipedHead.rotationPointY = 4.2F;
			model.bipedHeadwear.rotationPointY = 4.2f;
			
			model.bipedBody.rotationPointY = 3.2F;
		}
		else {
			this.height = 1.8f;
		}
		
		smoothRotateAll(model.bipedBody, body, 1);
		
		smoothRotateAll(model.bipedHead, t > 20 ? pi(1, 4) : 0, 0, 0, 1);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.3f * delta);
	}
}
