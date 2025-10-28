package net.dravigen.let_me_move.animation.actions;

import net.dravigen.let_me_move.animation.poses.AnimCommon;
import net.dravigen.let_me_move.utils.GeneralUtils;
import net.minecraft.src.*;

import static net.dravigen.let_me_move.utils.AnimationUtils.*;
import static net.dravigen.let_me_move.utils.AnimationUtils.addAllRotationPoint;
import static net.dravigen.let_me_move.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.let_me_move.utils.GeneralUtils.pi;

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
		
		smoothRotateAll(model.bipedHead, 0, 0, 0, 0.75f * delta);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		offsetAllRotationPoints(model, -4, 0, 0);
		
		smoothRotateAll(model.bipedBody, pi(1, 8), 0, 0, 1);
		
		addAllRotationPoint(model.bipedRightArm, 1, 0, 0);
		addAllRotationPoint(model.bipedLeftArm, 1, 0, 0);
		
		smoothRotateAll(model.bipedRightArm, pi(1, 16), 0, pi(1, 6), 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, 0, 0, -pi(4, 5), 0.3f * delta);
		
		addAllRotationPoint(model.bipedRightLeg, 0, -2, 4);
		addAllRotationPoint(model.bipedLeftLeg, 0, -2, 4);
		
		smoothRotateAll(model.bipedRightLeg, pi(1, 7), 0, -pi(1, 10), 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, 0, 0, -pi(1, 5), 0.3f * delta);
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
}
