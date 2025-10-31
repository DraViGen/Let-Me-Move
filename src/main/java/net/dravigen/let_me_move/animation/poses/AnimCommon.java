package net.dravigen.let_me_move.animation.poses;

import net.dravigen.let_me_move.animation.BaseAnimation;
import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.let_me_move.utils.AnimationUtils.*;
import static net.dravigen.let_me_move.utils.GeneralUtils.*;

public class AnimCommon extends BaseAnimation {
	public AnimCommon(ResourceLocation id, float height, float speedModifier, boolean needYOffsetUpdate,
			int maxCooldown, int duration, boolean shouldAutoUpdate, float yOffset) {
		super(id, height, speedModifier, needYOffsetUpdate, maxCooldown, duration, shouldAutoUpdate, yOffset);
	}
	
	public AnimCommon(ResourceLocation id, float height, float speedModifier, boolean needYOffsetUpdate,
			int maxCooldown, int duration, boolean shouldAutoUpdate) {
		super(id, height, speedModifier, needYOffsetUpdate, maxCooldown, duration, shouldAutoUpdate, 0);
	}
	
	public AnimCommon(ResourceLocation id, float height, float speedModifier, boolean needYOffsetUpdate,
			int maxCooldown, int duration) {
		super(id, height, speedModifier, needYOffsetUpdate, maxCooldown, duration, true, 0);
	}
	
	public AnimCommon(ResourceLocation id, float height, float speedModifier, boolean needYOffsetUpdate,
			int maxCooldown) {
		super(id, height, speedModifier, needYOffsetUpdate, maxCooldown, 0, true, 0);
	}
	
	public AnimCommon(ResourceLocation id, float height, float speedModifier, boolean needYOffsetUpdate) {
		super(id, height, speedModifier, needYOffsetUpdate, 0, 0, true, 0);
	}
	
	public AnimCommon(ResourceLocation id, float height, float speedModifier) {
		super(id, height, speedModifier, false, 0, 0, true, 0);
	}
	
	public AnimCommon(ResourceLocation id, float height) {
		super(id, height, 1, false, 0, 0, true, 0);
	}
	
	public AnimCommon(ResourceLocation id) {
		super(id, 1.8f, 1, false, 0, 0, true, 0);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return false;
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return true;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
		
		EntityPlayer player = (EntityPlayer) entity;
		
		resetAnimationRotationPoints(model);
		
		i %= 360;
		
		i = i < -180 ? i + 360 : i > 180 ? i - 360 : i;
		
		float[] body = new float[]{0, 0, 0};
		float[] head = new float[]{j * (pi / 180.0f) / 1.25f, i * (pi / 180.0f) / 1.25f, i * (pi / 180.0f) / 6};
		float[] rArm = new float[]{0, 0, 0};
		float[] lArm = new float[]{0, 0, 0};
		float[] rLeg = new float[]{0, 0, 0};
		float[] lLeg = new float[]{0, 0, 0};
		float k = 1;
		
		boolean isCrouching = customMoveEntity.llm_$isAnimation(AnimCrouching.id);
		
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
		float mul = 0.5f;
		boolean backward = player.moveForward < 0 && player.moveStrafing == 0;
		
		f *= backward ? -0.75f : 1;
		g *= backward ? 0.75f : 1;
		
		f *= isCrouching ? backward ? 3f : 3.5f : 1;
		g *= isCrouching ? 2f : 1;
		
		g *= model.aimedBow || model.heldItemRight == 3 ? 2f : 1;
		
		f = player.inWater ? h / 3 : f;
		g = player.inWater ? 0.5f : g;
		
		if ((player.moveForward != 0 || player.moveStrafing != 0)) {
			rArm[0] = cos(f * mul) * 2.0F * g * (bSprint ? 0.75f : 0.5F) / k;
			lArm[0] = cos(f * mul + pi) * 2.0F * g * (bSprint ? 0.75f : 0.5F) / k;
			
			body[0] = (cos(f * mul * 2) + 1) * g * (bSprint ? 0.1f : 0) / k + (bSprint ? pi(1, 32) : 0);
			body[1] = cos(f * mul) * g * (bSprint ? 0.0f : backward ? 0.5f : 0.3F) * (isCrouching ? 0.25f : 1) / k;
			
			if (!isCrouching) {
				rLeg[1] = pi(1, 64);
				lLeg[1] = -pi(1, 64);
			}
			
			rLeg[0] = Math.max(-2, (cos(f * mul + pi)) * g * (bSprint ? 0.9f : 0.6f) / k);
			rLeg[2] = pi(1, 100);
			
			lLeg[0] = Math.max(-2, (cos(f * mul)) * g * (bSprint ? 0.9f : 0.6f) / k);
			lLeg[2] = -pi(1, 100);
			
			if (player.inWater && !isHeadInsideWater(player)) {
				body[0] = (cos(f * mul * 2) + 1) * g * 0.25F / k;
				body[1] = cos(f * mul) * g * 0.6F / k;
				
				rArm[2] += pi(1, 3);
				lArm[2] -= pi(1, 3);
				
				rArm[0] = cos(f * mul) * 2.0F * g * 1f / k;
				lArm[0] = cos(f * mul + pi) * 2.0F * g * 1f / k;
				
				model.bipedRightArm.rotationPointY -= 1;
				model.bipedLeftArm.rotationPointY -= 1;
			}
			
			setAllRotationPoint(model.bipedHead, 0, 12 - cos(body[0]) * 12, -sin(body[0]) * 12);
			
			setAllRotationPoint(model.bipedRightArm,
								-cos(body[1]) * 5.0F,
								12 - cos(body[0]) * 10,
								sin(body[1]) * 5.0F - sin(body[0]) * 12);
			
			setAllRotationPoint(model.bipedLeftArm,
								cos(body[1]) * 5.0F,
								12 - cos(body[0]) * 10,
								-sin(body[1]) * 5.0F - sin(body[0]) * 12);
			
			setAllRotationPoint(model.bipedRightLeg,
								-cos(body[1]) * 2f,
								Math.max((sin(f * mul) - 1) * g * (bSprint ? 1.75f : 1.3f) + 12, (bSprint ? 6 : 8)),
								-sin(body[1]) * 2f);
			
			setAllRotationPoint(model.bipedLeftLeg,
								cos(body[1]) * 2f,
								Math.max((sin(f * mul + pi) - 1) * g * (bSprint ? 1.75f : 1.3f) + 12,
										 (bSprint ? 6 : 8)),
								sin(body[1]) * 2f);
			
			rArm[1] += body[1] + (bSprint ? cos(f * mul) * pi(1, 12) : 0);
			lArm[1] += body[1] + (bSprint ? cos(f * mul) * pi(1, 12) : 0);
			head[0] += body[0];
			//head[1] += -body[1] / 3;
			
			float v = 2f;
			float v1 = bSprint ? 3 : backward ? 0.2f : 2f;
			model.bipedBody.rotationPointY -= Math.min(cos(f * mul * v) * g * v1, 2);
			model.bipedRightArm.rotationPointY -= Math.min(cos(f * mul * v) * g * v1, 2);
			model.bipedLeftArm.rotationPointY -= Math.min(cos(f * mul * v) * g * v1, 2);
			model.bipedHead.rotationPointY -= Math.min(cos(f * mul * v) * g * v1, 2);
			model.bipedRightLeg.rotationPointY -= Math.min(cos(f * mul * v) * g * v1, 2);
			model.bipedLeftLeg.rotationPointY -= Math.min(cos(f * mul * v) * g * v1, 2);
		}
		else {
			rArm[1] = rArm[1] + body[1];
			lArm[1] = lArm[1] + body[1];
			body[1] += head[1] * 0.5f;
			rArm[1] += body[1] * 0.75f;
			lArm[1] += body[1] * 0.75f;
			
			model.bipedRightArm.rotationPointX = -cos(body[1] * 0.75f) * 5.0F;
			model.bipedRightArm.rotationPointZ = sin(body[1] * 0.75f) * 5.0F;
			model.bipedLeftArm.rotationPointX = cos(body[1] * 0.75f) * 5.0F;
			model.bipedLeftArm.rotationPointZ = -sin(body[1] * 0.75f) * 5.0F;
			
			rLeg[0] -= pi(1, 80);
			rLeg[2] += pi(1, 50);
			lLeg[0] += pi(1, 128);
			lLeg[2] -= pi(1, 50);
			rLeg[1] += pi(1, 32);
			lLeg[1] -= pi(1, 12);
			
			model.bipedRightLeg.rotationPointZ -= 0.6f;
			model.bipedLeftLeg.rotationPointZ += 0.5f;
		}
		
		swingArm(model, body, rArm, lArm, head);
		
		if (isCrouching) {
			body[0] = 0.5F;
			
			rArm[0] += 0.4F;
			lArm[0] += 0.4F;
			
			model.bipedRightArm.rotationPointY += 3.2F;
			
			model.bipedLeftArm.rotationPointY += 3.2F;
			
			model.bipedRightLeg.rotationPointY += 0.2F;
			model.bipedRightLeg.rotationPointZ += 4.0F;
			
			model.bipedLeftLeg.rotationPointY += 0.2F;
			model.bipedLeftLeg.rotationPointZ += 4.0F;
			
			model.bipedHead.rotationPointY += 4.2F;
			model.bipedHeadwear.rotationPointY += 4.2f;
			
			model.bipedBody.rotationPointY += 1.0F;
			model.bipedBody.rotationPointZ = 4.8F;
		}
		
		moveAround(model, h, head, rArm, lArm, rLeg, lLeg, body);
		
		if (model.aimedBow) {
			float var8 = 0.0f;
			float var9 = 0.0f;
			
			rArm[0] = (-pi / 2) + head[0];
			rArm[1] = -0.1F + head[1];
			rArm[2] = 0.1F + head[1] + 0.4F;
			
			lArm[0] = (-pi / 2) + head[0];
			
			rArm = new float[]{-1.5707964f + head[0], -(0.1f - var8 * 0.6f) + head[1], 0};
			
			rArm[0] -= var8 * 1.2f - var9 * 0.4f;
			rArm[2] += MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
			rArm[0] += MathHelper.sin(h * 0.067f) * 0.05f;
			
			lArm = new float[]{-1.5707964f + head[0], 0.1f - var8 * 0.6f + head[1] + 0.4f, 0};
			
			lArm[0] -= var8 * 1.2f - var9 * 0.4f;
			lArm[2] -= MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
			lArm[0] -= MathHelper.sin(h * 0.067f) * 0.05f;
		}
		else {
			rArm[0] = model.heldItemRight != 0 ? rArm[0] * 0.5f - 0.31415927f * (float) model.heldItemRight : rArm[0];
			
			lArm[0] = model.heldItemLeft != 0 ? lArm[0] * 0.5f - 0.31415927f * (float) model.heldItemLeft : lArm[0];
		}
		
		eatFood(model, h, player, head, rArm);
		
		smoothRotateAll(model.bipedBody, body, 0.7f * delta);
		
		smoothRotateAll(model.bipedHead, head, 0.5f * delta);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.5f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.5f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.5f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.5f * delta);
		
		/*
		smoothRotateAll(model.bipedHead, j * (pi / 180.0f), i * (pi / 180.0f), 0, 1);
		
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
		
		rArm[0] = model.aimedBow
				  ? (-pi / 2) + model.bipedHead.rotateAngleX
				  : model.heldItemRight != 0 ? rArm[0] * 0.5f - 0.31415927f * (float) model.heldItemRight : rArm[0];
		
		lArm[0] = model.aimedBow
				  ? (-pi / 2) + model.bipedHead.rotateAngleX
				  : model.heldItemLeft != 0 ? lArm[0] * 0.5f - 0.31415927f * (float) model.heldItemLeft : lArm[0];
		
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
		
		smoothRotateAll(model.bipedRightLeg,
						model.isRiding ? -1.4137167F : MathHelper.cos(f * 0.6662F) * 1.4F * g / k,
						model.isRiding ? (pi / 10) : 0,
						model.isRiding ? 0.07853982F : 0,
						0.4f * delta,
						0.3f * delta,
						0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg,
						model.isRiding ? -1.4137167F : MathHelper.cos(f * 0.6662F + pi) * 1.4F * g / k,
						model.isRiding ? (pi / 10) : 0,
						model.isRiding ? 0.07853982F : 0,
						0.4f * delta,
						0.3f * delta,
						0.3f * delta);
		
		smoothRotateAll(model.bipedBody, body[0], body[1], body[2], 0.6f * delta);
		
		if (model.aimedBow) {
			float var8 = 0.0f;
			float var9 = 0.0f;
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
		
		if (player instanceof EntityPlayer) {
			if (player.getCurrentItemOrArmor(2) == null) {
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
		}*/
	}
	
	protected static void eatFood(ModelBiped model, float h, EntityLivingBase player, float[] head, float[] rArm) {
		if (player.isEating()) {
			head[0] = sin(h * 2) * pi(1, 32) + pi(1, 12);
			head[1] = 0;
			
			head[2] += sin(h) * pi(1, 16);
			
			rArm[0] -= pi(8, 16);
			rArm[2] += pi(3, 16);
			
			model.bipedRightArm.rotationPointZ += 1;
			model.bipedRightArm.rotationPointY += 2;
		}
	}
	
	protected static void swingArm(ModelBiped model, float[] body, float[] rArm, float[] lArm, float[] head) {
		float onGround = model.onGround;
		if (!(onGround <= 0.0F)) {
			onGround *= 1.25f;
			
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
			float v1 = MathHelper.sin(model.onGround * pi) * -(head[0] - 0.7F) * 0.75F;
			
			rArm[0] = (float) (rArm[0] - (v * 1.2 + v1));
			rArm[1] = rArm[1] + body[1] * 2.0F;
			rArm[2] = rArm[2] + MathHelper.sin(model.onGround * pi) * -0.4F;
		}
	}
	
	protected static void breath(ModelBiped model, float h, float[] head, float[] rArm, float[] lArm, float[] rLeg,
			float[] lLeg, float[] body) {
		head[0] += sin(h / 8) * pi(1, 80);
		head[2] += sin(h / 6) * pi(1, 64);
		
		rArm[0] += (sin(h / 16f)) * pi(1, 128);
		rArm[2] += (cos(h / 8f + pi(1, 5)) + 1) * pi(1, 64);
		
		lArm[0] += -(sin(h / (16f))) * pi(1, 128);
		lArm[2] += -(cos(h / (8f) + pi(1, 5)) + 1) * pi(1, 64);
		
		float v0 = cos(h / 12) * 0.05f;
		float v2 = cos(h / 16) * 0.025f;
		
		rLeg[0] += v0;
		rLeg[2] += v2;
		
		lLeg[0] += v0;
		lLeg[2] += v2;
		
		body[0] -= v0 * 0.5f;
		body[2] -= v2 * 0.5f;
		
		model.bipedHead.rotationPointY += (sin(h / 8) + 0.65f) * 0.25f;
		model.bipedBody.rotationPointY += (sin(h / 8) + 0.65f) * 0.25f;
		model.bipedRightArm.rotationPointY += (sin(h / 8) + 0.65f) * 0.25f;
		model.bipedLeftArm.rotationPointY += (sin(h / 8) + 0.65f) * 0.25f;
	}
	
	protected static void moveAround(ModelBiped model, float h, float[] head, float[] rArm, float[] lArm, float[] rLeg,
			float[] lLeg, float[] body) {
		breath(model, h, head, rArm, lArm, rLeg, lLeg, body);
		
		float v0 = cos(h / 12) * 0.05f;
		float v2 = cos(h / 16) * 0.025f;
		
		model.bipedRightLeg.rotationPointZ -= sin(v0) * 12;
		model.bipedLeftLeg.rotationPointZ -= sin(v0) * 12;
		
		model.bipedBody.rotationPointZ -= sin(v0) * 12;
		model.bipedRightArm.rotationPointZ -= sin(v0) * 12;
		model.bipedLeftArm.rotationPointZ -= sin(v0) * 12;
		model.bipedHead.rotationPointZ -= sin(v0) * 12;
		
		model.bipedRightLeg.rotationPointX += sin(v2) * 12;
		model.bipedLeftLeg.rotationPointX += sin(v2) * 12;
		model.bipedBody.rotationPointX += sin(v2) * 12;
		model.bipedRightArm.rotationPointX += sin(v2) * 12;
		model.bipedLeftArm.rotationPointX += sin(v2) * 12;
		model.bipedHead.rotationPointX += sin(v2) * 12;
	}
	
	@Override
	public void updateLeaning(EntityLivingBase entity) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		float goal = 0;
		
		customEntity.llm_$setLeaningPitch(goal);
	}
	
	@Override
	public boolean getHungerCost(EntityPlayer player, double distX, double distY, double distZ) {
		return false;
	}
	
	@Override
	public boolean getCustomMove(EntityPlayer player) {
		return false;
	}
	
	@Override
	public boolean customBodyHeadRotation(EntityLivingBase entity) {
		return false;
	}
}
