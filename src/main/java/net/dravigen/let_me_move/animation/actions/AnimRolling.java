package net.dravigen.let_me_move.animation.actions;

import net.dravigen.let_me_move.animation.BaseAnimation;
import net.dravigen.let_me_move.animation.poses.AnimCommon;
import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.let_me_move.LetMeMoveAddon.roll_key;
import static net.dravigen.let_me_move.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.let_me_move.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.let_me_move.utils.GeneralUtils.checkIfEntityFacingWall;
import static net.dravigen.let_me_move.utils.GeneralUtils.pi;

public class AnimRolling extends AnimCommon {
	public static final ResourceLocation id = new ResourceLocation("LMM", "rolling");
	
	public AnimRolling() {
		super(id, 1.8f, 1, true, 40, 25, true);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !player.isEating() &&
				!player.doesStatusPreventSprinting() &&
				!player.inWater &&
				!checkIfEntityFacingWall(player);
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return roll_key.pressed;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		if (entity.isEating()) {
			stopAnimation();
		}
		
		resetAnimationRotationPoints(model);
		
		smoothRotateAll(model.bipedBody, 0, 0, 0, 0.6f * delta);
		
		float[] head = new float[]{0, 0, 0};
		float[] rArm = new float[]{0, 0, 0};
		float[] lArm = new float[]{0, 0, 0};
		float[] rLeg = new float[]{0, 0, 0};
		float[] lLeg = new float[]{0, 0, 0};
		
		int t = this.timeRendered;

		if (t < 10) {
			this.height = 1.8f - t / 10f;
			rArm[0] = -pi;
			lArm[0] = -pi;
			rLeg[0] = -pi * 0.25f;
			lLeg[0] = -pi * 0.25f;
			head[0] = pi * 0.25f;
			head[1] = 0;
		}
		else if (t < 20) {
			if (t > 15) this.height = 0.8f + (t - 15) / 5f;
			else this.height = 0.8f;
			
			rArm[0] = -pi * 0.8f;
			lArm[0] = -pi * 0.8f;
			rLeg[0] = -pi * 0.5f;
			lLeg[0] = -pi * 0.5f;
			head[0] = pi * 0.4f;
			head[1] = 0;
		}
		else {
			this.height = 1.8f;
			head[0] = j * (pi / 180.0f);
			head[1] = i * (pi / 180.0f);
		}
		
		smoothRotateAll(model.bipedHead, head, 0.6f * delta, 1, 1);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.3f * delta);
	}
	
	@Override
	public void updateLeaning(EntityLivingBase entity) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		
		BaseAnimation animation = customEntity.llm_$getAnimation();
		float goal = (float) animation.timeRendered / ((animation.totalDuration - 5) / 4.5f);
		if (animation.timeRendered > (animation.totalDuration - 5) / 4.5f * 4) goal = 4;
		
		customEntity.llm_$setLeaningPitch(goal);
	}
	
	@Override
	public boolean getHungerCost(EntityPlayer player, double distX, double distY, double distZ) {
		int var7 = Math.round(MathHelper.sqrt_double(distX * distX + distZ * distZ) * 100.0f);
		
		if (var7 > 0) {
			player.addStat(StatList.distanceWalkedStat, var7);
			float par2 = 1.25f * (float) var7 * 0.001f * getHungerDifficultyMultiplier(player);
			player.addExhaustion(par2);
		}
		
		return true;
	}
	
	@Override
	public boolean getCustomMove(EntityPlayer player) {
		float par1 = player.moveStrafing;
		float par2 = 1.5f * player.getSpeedModifier() * player.getMovementSpeedModifierFromEffects();
		float var3 = 0.91f;
		
		if (player.onGround) {
			var3 = player.getDefaultSlipperinessOnGround();
			int var4 = player.worldObj.getBlockId(MathHelper.floor_double(player.posX),
												  MathHelper.floor_double(player.boundingBox.minY - 0.25),
												  MathHelper.floor_double(player.posZ));
			if (var4 > 0) {
				var3 = player.getSlipperinessRelativeToBlock(var4);
			}
		}
		
		float var8 = 0.16277136f / (var3 * var3 * var3);
		float var5 = player.onGround ? player.getAIMoveSpeed() * var8 : player.jumpMovementFactor;
		player.moveFlying(par1, par2, var5);
		var3 = 0.91f;
		
		if (player.onGround) {
			var3 = player.getDefaultSlipperinessOnGround();
			int var6 = player.worldObj.getBlockId(MathHelper.floor_double(player.posX),
												  MathHelper.floor_double(player.boundingBox.minY - 0.25),
												  MathHelper.floor_double(player.posZ));
			if (var6 > 0) {
				var3 = player.getSlipperinessRelativeToBlock(var6);
			}
		}
		
		player.moveEntity(player.motionX, player.motionY, player.motionZ);
		if (player.isCollidedHorizontally && player.isOnLadder()) {
			player.motionY = 0.2;
		}
		player.motionY = !(!player.worldObj.isRemote ||
				player.worldObj.blockExists((int) player.posX, 0, (int) player.posZ) &&
						player.worldObj.getChunkFromBlockCoords((int) ((int) player.posX),
																(int) ((int) player.posZ)).isChunkLoaded)
						 ? (player.posY > 0.0 ? -0.1 : 0.0)
						 : (player.motionY -= 0.08);
		player.motionY *= (double) 0.98f;
		player.motionX *= (double) var3;
		player.motionZ *= (double) var3;
		
		return true;
	}
}
