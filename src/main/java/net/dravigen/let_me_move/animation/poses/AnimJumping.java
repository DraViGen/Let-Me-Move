package net.dravigen.let_me_move.animation.poses;

import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.let_me_move.utils.AnimationUtils.*;
import static net.dravigen.let_me_move.utils.AnimationUtils.setAllRotationPoint;
import static net.dravigen.let_me_move.utils.GeneralUtils.*;
import static net.dravigen.let_me_move.utils.GeneralUtils.sin;

public class AnimJumping extends AnimCommon {
	public static final ResourceLocation id = new ResourceLocation("LMM", "jumping");
	
	public AnimJumping() {
		super(id, 1.8f, 1, false, 0, Integer.MAX_VALUE, true, 0);
	}

	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !player.onGround &&
				!player.inWater &&
				player.fallDistance <= AnimLowFalling.minFallHeight &&
				!player.capabilities.isFlying &&
				!player.isOnLadder();
	}
	
	private int field_1 = 1;
	private int prevRenderTime = -1;
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
		
		EntityPlayer player = (EntityPlayer) entity;
		
		resetAnimationRotationPoints(model);
		
		i %= 360;
		
		i = i < -180 ? i + 360 : i > 180 ? i - 360 : i;
		
		float[] body = new float[]{0, 0, 0};
		float[] head = new float[]{j * (pi / 180.0f) / 1.25f, i * (pi / 180.0f) / 1.5f, i * (pi / 180.0f) / 6};
		float[] rArm = new float[]{0, 0, 0};
		float[] lArm = new float[]{0, 0, 0};
		float[] rLeg = new float[]{0, 0, 0};
		float[] lLeg = new float[]{0, 0, 0};
		float k = 1;
	
		boolean isCrouching = player.isSneaking();
		
		if (player.moveForward != 0 || player.moveStrafing != 0) {
			player.renderYawOffset = incrementAngleUntilGoal(player.renderYawOffset,
															 player.rotationYaw -
																	 90 *
																			 (player.moveStrafing -
																					 player.moveStrafing / 2 *
																							 player.moveForward),
															 delta * 0.1f);
		}
		
		boolean bSprint = player.isSprinting();
		float mul = bSprint ? 0.55f : 0.5f;
		float onGround = model.onGround;
		boolean backward = player.moveForward < 0 && player.moveStrafing == 0;
		
		if (this.timeRendered == 0) {
			if (this.prevRenderTime != 0) {
				field_1 *= -1;
				this.prevRenderTime = 0;
			}
		}
		else this.prevRenderTime = -1;
		
		double motYposRev = 1 - Math.max(0, player.motionY);
		if ((player.moveForward != 0 || player.moveStrafing != 0)) {
			body[0] = (bSprint ? pi(1, 32) : 0);
			body[1] = field_1 * pi(1, 16) * g;
			
			if (!isCrouching) {
				rLeg[1] = pi(1, 64);
				lLeg[1] = -pi(1, 64);
			}
			
			rLeg[2] = pi(1, 100);
			lLeg[2] = -pi(1, 100);
			
			if (bSprint) {
				setAllRotationPoint(model.bipedHead, 0, 12 - cos(body[0]) * 12, -sin(body[0]) * 12);
				
				setAllRotationPoint(model.bipedRightArm,
									-cos(body[1]) * 5.0F,
									12 - cos(body[0]) * 10,
									sin(body[1]) * 5.0F - sin(body[0]) * 12);
				
				setAllRotationPoint(model.bipedLeftArm,
									cos(body[1]) * 5.0F,
									12 - cos(body[0]) * 10,
									-sin(body[1]) * 5.0F - sin(body[0]) * 12);
				
				setAllRotationPoint(model.bipedRightLeg, -cos(body[1]) * 2f, 12, -sin(body[1]) * 2f);
				
				setAllRotationPoint(model.bipedLeftLeg, cos(body[1]) * 2f, 12, sin(body[1]) * 2f);
				
				rArm[1] += body[1];
				lArm[1] += body[1];
				head[0] += body[0];
				
				rArm[0] = (float) (field_1 * pi(1, 4) * g * (1 - player.motionY));
				lArm[0] = (float) (-field_1 * pi(1, 4) * g * (1 - player.motionY));
				rLeg[0] = (float) (-field_1 * pi(1, 4) * g * (1 - player.motionY));
				lLeg[0] = (float) (field_1 * pi(1, 4) * g * (1 - player.motionY));
				
				rLeg[2] = (float) Math.abs(field_1 * pi(1, 24) * g * (1 - player.motionY));
				lLeg[2] = (float) -Math.abs(field_1 * pi(1, 24) * g * (1 - player.motionY));
				
			}
			else {
				float v = isCrouching ? 0.25f : 1;
				float v1 = isCrouching ? 0.4f : 1;
				rArm[0] = (float) (field_1 * pi(1, 3) * (1 - player.motionY) * v1);
				lArm[0] = (float) (-field_1 * pi(1, 3) * (1 - player.motionY) * v1);
				
				rLeg[0] = pi(1, 16) * Math.max(0, field_1 * 2) * v;
				lLeg[0] = pi(1, 16) * Math.max(0, -field_1 * 2) * v;
				
				model.bipedRightLeg.rotationPointY = (float) (12 - Math.max(0, field_1 * 2) * 2 * motYposRev * v1);
				model.bipedLeftLeg.rotationPointY = (float) (12 - Math.max(0, -field_1 * 2) * 2 * motYposRev * v1);
				model.bipedRightLeg.rotationPointZ = (float) (- Math.max(0, field_1 * 2) * g * 2 * motYposRev * v);
				model.bipedLeftLeg.rotationPointZ = (float) (- Math.max(0, -field_1 * 2) * g * 2 * motYposRev * v);
			}
		}
		else {
			rArm[1] = rArm[1] + body[1];
			lArm[1] = lArm[1] + body[1];
			head[1] += -body[1] / 4;
			
			rLeg[2] += pi(1, 64);
			lLeg[2] -= pi(1, 64);
			
			float v = isCrouching ? 0.25f : 1;
			float v1 = isCrouching ? 0.4f : 1;
			
			rArm[0] = (float) (field_1 * pi(1, 3) * (1 - player.motionY) * v1);
			lArm[0] = (float) (-field_1 * pi(1, 3) * (1 - player.motionY) * v1);
			
			rLeg[0] = pi(1, 16) * Math.max(0, field_1 * 2) * v;
			lLeg[0] = pi(1, 16) * Math.max(0, -field_1 * 2) * v;
			
			model.bipedRightLeg.rotationPointY = (float) (12 - Math.max(0, field_1 * 4) * motYposRev * v1);
			model.bipedLeftLeg.rotationPointY = (float) (12 - Math.max(0, -field_1 * 4) * motYposRev * v1);
			model.bipedRightLeg.rotationPointZ = (float) (- Math.max(0, field_1 * 3) * motYposRev * v);
			model.bipedLeftLeg.rotationPointZ = (float) (- Math.max(0, -field_1 * 3) * motYposRev * v);
		}
		
		this.height = 1.8f;
		
		if (!(onGround <= 0.0F)) {
			body[1] = MathHelper.sin(MathHelper.sqrt_float(onGround) * (pi * 2)) * 0.2F;
			
			model.bipedRightArm.rotationPointZ = sin(body[1]) * 5.0F;
			model.bipedRightArm.rotationPointX = -cos(body[1]) * 5.0F;
			
			model.bipedLeftArm.rotationPointZ = -sin(body[1]) * 5.0F;
			model.bipedLeftArm.rotationPointX = cos(body[1]) * 5.0F;
			
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
		
		if (isCrouching) {
			this.height = 1.4f;
			
			body[0] = 0.5F;
			
			rArm[0] += 0.4F;
			lArm[0] += 0.4F;
			
			model.bipedRightArm.rotationPointY += 3.2F;
			
			model.bipedLeftArm.rotationPointY += 3.2F;
			
			model.bipedRightLeg.rotationPointY += 0.2F;
			model.bipedRightLeg.rotationPointZ = 4.0F;
			
			model.bipedLeftLeg.rotationPointY += 0.2F;
			model.bipedLeftLeg.rotationPointZ = 4.0F;
			
			model.bipedHead.rotationPointY += 4.2F;
			model.bipedHeadwear.rotationPointY += 4.2f;
			
			model.bipedBody.rotationPointY += 1.0F;
			model.bipedBody.rotationPointZ = 4.8F;
		}
		
		if (model.aimedBow) {
			float var8 = 0.0f;
			float var9 = 0.0f;
			
			rArm[0] = (-pi / 2) + model.bipedHead.rotateAngleX;
			rArm[1] = -0.1F + model.bipedHead.rotateAngleY;
			rArm[2] = 0.1F + model.bipedHead.rotateAngleY + 0.4F;
			
			lArm[0] = (-pi / 2) + model.bipedHead.rotateAngleX;
			
			model.bipedRightArm.rotateAngleZ = 0.0f;
			model.bipedLeftArm.rotateAngleZ = 0.0f;
			model.bipedRightArm.rotateAngleY = -(0.1f - var8 * 0.6f) + model.bipedHead.rotateAngleY;
			model.bipedLeftArm.rotateAngleY = 0.1f - var8 * 0.6f + model.bipedHead.rotateAngleY + 0.4f;
			model.bipedRightArm.rotateAngleX = -1.5707964f + model.bipedHead.rotateAngleX;
			model.bipedLeftArm.rotateAngleX = -1.5707964f + model.bipedHead.rotateAngleX;
			model.bipedRightArm.rotateAngleX -= var8 * 1.2f - var9 * 0.4f;
			model.bipedLeftArm.rotateAngleX -= var8 * 1.2f - var9 * 0.4f;
			model.bipedRightArm.rotateAngleZ += MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
			model.bipedLeftArm.rotateAngleZ -= MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
			model.bipedRightArm.rotateAngleX += MathHelper.sin(h * 0.067f) * 0.05f;
			model.bipedLeftArm.rotateAngleX -= MathHelper.sin(h * 0.067f) * 0.05f;
		}
		else {
			rArm[0] = model.heldItemRight != 0 ? rArm[0] * 0.5f - 0.31415927f * (float) model.heldItemRight : rArm[0];
			
			lArm[0] = model.heldItemLeft != 0 ? lArm[0] * 0.5f - 0.31415927f * (float) model.heldItemLeft : lArm[0];
		}
		
		eatFood(model, h, entity, head, rArm);
		
		smoothRotateAll(model.bipedBody, body, 0.7f * delta);
		
		smoothRotateAll(model.bipedHead, head, 0.5f * delta);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.5f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.5f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.5f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.5f * delta);
		
	}
}
