package net.dravigen.let_me_move.animation.customs;

import net.minecraft.src.*;

import static net.dravigen.let_me_move.utils.AnimationUtils.*;
import static net.dravigen.let_me_move.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.let_me_move.utils.GeneralUtils.*;
import static net.dravigen.let_me_move.utils.GeneralUtils.pi;

public class AnimPullingUp extends AnimCommon {
	public static final ResourceLocation id = new ResourceLocation("LMM", "pullingUp");
	private static double yBlockAboveWall;
	private coords prevSide;
	
	public AnimPullingUp() {
		super(id, 1.8f, 1f, false, 0, 40, false);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		double minY = player.boundingBox.minY;
		
		return !player.capabilities.isFlying &&
				!player.isEating() &&
				(player.onGround || player.motionY < 0) &&
				!player.inWater &&
				!player.doesStatusPreventSprinting() &&
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
		if (entity.isEating() ||
				this.timeRendered < 0 ||
				((EntityPlayer) entity).doesStatusPreventSprinting() ||
				!entity.isSneaking() ||
				getWallSide(entity, 0, entity.height) == null ||
				getWallTopYIfEmptySpace(entity) == -1) {
			this.timeRendered = this.totalDuration;
			
			return;
		}
		
		resetAnimationRotationPoints(model);
		
		float[] body = new float[]{0, 0, 0};
		float[] rArm = new float[]{0, 0, 0};
		float[] lArm = new float[]{0, 0, 0};
		float[] rLeg = new float[]{0, 0, 0};
		float[] lLeg = new float[]{0, 0, 0};
		
		this.timeRendered = MathHelper.floor_double((2 - ((yBlockAboveWall + 0.05) - entity.boundingBox.minY)) *
															this.totalDuration / 2);
		
		int t = this.timeRendered;
		
		offsetAllRotationPoints(model, 0, 0, -2);
		
		rArm[0] = -pi(7, 8) + pi(7, 8) * t / 35;
		lArm[0] = -pi(7, 8) + pi(7, 8) * t / 35;
		
		if (entity.moveStrafing != 0) {
			rArm[2] = sin(entity.ticksExisted / 8f) / 2f;
			lArm[2] = sin(-entity.ticksExisted / 8f) / 2f;
			rArm[1] = sin(-entity.ticksExisted / 8f) / 2f;
			lArm[1] = sin(entity.ticksExisted / 8f) / 2f;
		}
		
		if (t > 20) {
			this.height = 1.4f;
			
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
		
		i %= 360;
		
		i = i < -180 ? i + 360 : i > 180 ? i - 360 : i;
		
		smoothRotateAll(model.bipedHead,
						j * (pi / 180.0f),
						MathHelper.clamp_float(i * (pi / 180.0f), -pi / 2f, pi / 2f),
						0,
						1);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.3f * delta);
	}
	
	@Override
	public boolean getHungerCost(EntityPlayer player, double distX, double distY, double distZ) {
		player.addExhaustion(1.33f / 100f * getHungerDifficultyMultiplier(player));
		
		return true;
	}
	
	@Override
	public boolean getCustomMove(EntityPlayer player) {
		coords side = getWallSide(player, 0, player.height);
		
		if (side != null) {
			prevSide = side;
		}
		
		double x = 0;
		double z = 0;
		
		player.motionY *= 0.8f;
		player.fallDistance = 0;
		x = prevSide == coords.SOUTH ? player.moveStrafing : prevSide == coords.NORTH ? -player.moveStrafing : x;
		z = prevSide == coords.EAST ? -player.moveStrafing : prevSide == coords.WEST ? player.moveStrafing : z;
		
		if (player.moveForward != 0) {
			if (side == null && !movedOnce) {
				x = prevSide == coords.EAST ? 0.5 : prevSide == coords.WEST ? -0.5 : x;
				z = prevSide == coords.SOUTH ? 0.5 : prevSide == coords.NORTH ? -0.5 : z;
				
				this.timeRendered = this.totalDuration;
				
				movedOnce = true;
			}
			else {
				movedOnce = false;
			}
			
			float var1 = player.moveForward < 0 ? -1 : 1;
			
			player.moveEntity(x / 24, var1 * 1.75d / this.totalDuration, z / 24);
		}
		else {
			player.moveEntity(x / 24, -0.0125, z / 24);
		}
		
		return true;
	}
}
