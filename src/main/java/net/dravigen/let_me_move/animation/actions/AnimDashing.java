package net.dravigen.let_me_move.animation.actions;

import net.dravigen.let_me_move.animation.poses.AnimCommon;
import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.let_me_move.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.let_me_move.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.let_me_move.utils.GeneralUtils.pi;

public class AnimDashing extends AnimCommon {
	public static final ResourceLocation id = new ResourceLocation("LMM", "dashing");
	private static int pressTime = 0;
	
	public AnimDashing() {
		super(id, 1.8f, 1, false, 40, 5, true, 0);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !player.isEating() && player.moveForward == 0 && player.onGround && !player.doesStatusPreventSprinting();
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		if (player.isUsingSpecialKey() || player.moveStrafing != 0) {
			if (pressTime < 5 && player.isUsingSpecialKey() && player.moveStrafing != 0) {
				
				return true;
			}
			
			pressTime++;
		}
		else {
			pressTime = 0;
		}
		
		return false;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		if (entity.isEating()) {
			stopAnimation();
		}
		
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		
		resetAnimationRotationPoints(model);
		
		smoothRotateAll(model.bipedBody, 0, 0, 0, 0.8f * delta);
		
		smoothRotateAll(model.bipedHead, j * (pi / 180.0f), i * (pi / 180.0f), 0, 1);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		float side = customEntity.llm_$getSideValue();
		
		entity.renderYawOffset = entity.rotationYawHead + 45 * side;
		
		smoothRotateAll(model.bipedRightArm, pi * 0.5f, side, side * pi * 0.25f, 0.6f * delta);
		
		smoothRotateAll(model.bipedLeftArm, pi * 0.5f, side, side * pi * 0.25f, 0.6f * delta);
		
		smoothRotateAll(model.bipedRightLeg, pi * 0.25f, 0, side * -pi * 0.125f, 0.6f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, pi * 0.25f, 0, side * -pi * 0.125f, 0.6f * delta);
	}
	
	@Override
	public boolean getHungerCost(EntityPlayer player, double distX, double distY, double distZ) {
		int total = Math.round(MathHelper.sqrt_double(distX * distX + distZ * distZ) * 100.0f);
		
		if (total > 0) {
			player.addStat(StatList.distanceWalkedStat, total);
			float par2 = 1.75f * (float) total * 0.001f * getHungerDifficultyMultiplier(player);
			player.addExhaustion(par2);
			
		}
		
		return true;
	}
	
	@Override
	public boolean getCustomMove(EntityPlayer player) {
		if (this.timeRendered == 0) {
			movedOnce = false;
		}
		
		if (!movedOnce) {
			float var1 = player.moveStrafing * 8;
			float var4 = var1 * var1;
			
			if (var4 >= 1.0E-4f) {
				if ((var4 = MathHelper.sqrt_float(var4)) < 1.0f) {
					var4 = 1.0f;
				}
				
				var4 = 1 / var4;
				float var5 = MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0f);
				float var6 = MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0f);
				
				
				float modifier = player.getSpeedModifier() * player.getMovementSpeedModifierFromEffects();
				player.motionX = (var1 *= var4) * var6 * modifier;
				player.motionZ = var1 * var5 * modifier;
			}
			
			player.moveEntity(player.motionX, player.motionY, player.motionZ);
			
			player.motionY *= 0.8f;
			
			movedOnce = true;
			
			return true;
		}
		
		return false;
	}
}
