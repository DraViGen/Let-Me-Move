package net.dravigen.let_me_move.animation.actions;

import net.dravigen.let_me_move.animation.poses.AnimCommon;
import net.dravigen.let_me_move.utils.GeneralUtils;
import net.minecraft.src.*;

import static net.dravigen.let_me_move.utils.AnimationUtils.*;
import static net.dravigen.let_me_move.utils.AnimationUtils.addAllRotationPoint;
import static net.dravigen.let_me_move.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.let_me_move.utils.GeneralUtils.*;

public class AnimWallSliding extends AnimCommon {
	public static final ResourceLocation id = new ResourceLocation("LMM", "wallSliding");
	
	public AnimWallSliding() {
		super(id, 1.8f, 1);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !player.isEating() &&
				player.fallDistance > 1.5 &&
				!player.onGround &&
				!player.inWater &&
				GeneralUtils.getWallSide(player, 0, player.height) != null &&
				player.canJump();
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return player.isSneaking();
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		resetAnimationRotationPoints(model);
		
		float[] head = new float[]{0, 0, 0};
		float[] body = new float[]{pi(1, 8), 0, 0};
		float[] rArm = new float[]{pi(1, 16), 0, pi(1, 6)};
		float[] lArm = new float[]{0, 0, -pi(4, 5)};
		float[] rLeg = new float[]{pi(1, 7), 0, -pi(1, 10)};
		float[] lLeg = new float[]{0, 0, -pi(1, 5)};
		
		offsetAllRotationPoints(model, -4, 0, 0);
		
		model.bipedBody.rotationPointZ = sin(model.bipedBody.rotateAngleX) * 12;
		model.bipedHead.rotationPointY += 1;
		addAllRotationPoint(model.bipedRightArm, 1, 0, 0);
		addAllRotationPoint(model.bipedLeftArm, 1, 0, 0);
		addAllRotationPoint(model.bipedRightLeg, 0, -2, 4);
		addAllRotationPoint(model.bipedLeftLeg, 0, -2, 4);
		
		breath(model, h, head, rArm, lArm, rLeg, lLeg, body);
		
		float onGround = model.onGround;
		if (!(onGround <= 0.0F)) {
			body[1] = MathHelper.sin(MathHelper.sqrt_float(onGround) * (pi * 2)) * 0.05F;
			
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
		
		smoothRotateAll(model.bipedHead, head, 0.75f * delta);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedBody, body, 1);
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.3f * delta);
	}
	
	@Override
	public boolean getHungerCost(EntityPlayer player, double distX, double distY, double distZ) {
		int total = Math.round(MathHelper.sqrt_double(distY * distY) * 100.0f);
		
		if (total > 0) {
			player.addStat(StatList.distanceFallenStat, total);
			player.addExhaustion(0.75f *
									   total *
									   0.001f *
									   getHungerDifficultyMultiplier(player));
		}
		
		return true;
	}
	
	@Override
	public boolean customBodyHeadRotation(EntityLivingBase entity) {
		return true;
	}
}
