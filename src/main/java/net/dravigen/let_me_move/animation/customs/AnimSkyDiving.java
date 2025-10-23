package net.dravigen.let_me_move.animation.customs;

import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.let_me_move.LetMeMoveAddon.crawl_key;
import static net.dravigen.let_me_move.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.let_me_move.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.let_me_move.utils.GeneralUtils.pi;

public class AnimSkyDiving extends AnimCommon {
	public static final ResourceLocation id = new ResourceLocation("LMM", "skyDiving");
	
	public AnimSkyDiving() {
		super(id, 1f, 0.2f, true);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !player.isEating() &&
				player.fallDistance >= 10 &&
				!player.capabilities.isFlying &&
				!player.doesStatusPreventSprinting();
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !crawl_key.pressed && player.isSneaking();
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		resetAnimationRotationPoints(model);
		
		smoothRotateAll(model.bipedBody, 0, 0, 0, 0.4f * delta);
		
		smoothRotateAll(model.bipedHead, -0.5f, i * (pi / 180.0f), 0, 1);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		boolean fMove = entity.moveForward > 0;
		
		smoothRotateAll(model.bipedRightArm, 0.5f, 0, fMove ? 0.5f : 2, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, 0.5f, 0, fMove ? -0.5f : -2, 0.3f * delta);
		
		smoothRotateAll(model.bipedRightLeg, 0.5f, 0, 0.15f, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, 0.5f, 0, -0.15f, 0.3f * delta);
	}
	
	@Override
	public void updateLeaning(EntityLivingBase entity) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		float goal = entity.moveForward > 0 ? 1.2f : 1;
		
		customEntity.llm_$setLeaningPitch(goal);
	}
	
	@Override
	public boolean getHungerCost(EntityPlayer player, double distX, double distY, double distZ) {
		int total = Math.round(MathHelper.sqrt_double(distX * distX + distY * distY + distZ * distZ) * 100.0f);
		
		if (total > 0) {
			float par2 = 0.05f * total * 0.001f * getHungerDifficultyMultiplier(player);
			
			if (player.moveForward > 0) {
				par2 *= 1.5f;
				player.addExhaustion(par2);
			}
			else {
				player.addExhaustionWithoutVisualFeedback(par2);
			}
			
			player.addStat(StatList.distanceFallenStat, total);
		}
		
		return true;
	}
}
