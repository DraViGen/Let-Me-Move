package net.dravigen.let_me_move.animation.poses;

import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.let_me_move.LetMeMoveAddon.crawl_key;
import static net.dravigen.let_me_move.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.let_me_move.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.let_me_move.utils.GeneralUtils.*;

public class AnimHighFalling extends AnimCommon {
	public final static int minFallHeight = 24;
	public static ResourceLocation id = new ResourceLocation("LMM", "highFalling");
	
	public AnimHighFalling() {
		super(id, 1.8f, 0.005f, true);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return player.fallDistance >= minFallHeight &&
				(!player.isSneaking() || (player.isSneaking() && player.doesStatusPreventSprinting())) &&
				!player.capabilities.isFlying;
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !crawl_key.pressed;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		float leaning = customEntity.llm_$getLeaningPitch();
		
		resetAnimationRotationPoints(model);
		
		smoothRotateAll(model.bipedBody, 0, 0, 0, 0.4f * delta);
		
		smoothRotateAll(model.bipedHead, 0.25f, i * (pi / 180.0f), 0, 1);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		float sin = MathHelper.sin(leaning);
		float cos = MathHelper.cos(leaning);
		float cos1 = MathHelper.cos(leaning + 2);
		float sin1 = MathHelper.sin(leaning + 2);
		
		smoothRotateAll(model.bipedRightArm, cos, 0, 1.75f + sin, 0.4f * delta);
		
		smoothRotateAll(model.bipedLeftArm, cos1, 0, -1.75f - sin1, 0.4f * delta);
		
		smoothRotateAll(model.bipedRightLeg, cos * 1.5f, 0, 0, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, sin * 1.5f, 0, 0, 0.3f * delta);
	}
	
	@Override
	public void updateLeaning(EntityLivingBase entity) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		float goal = (entity.fallDistance - minFallHeight) / 6;
		
		customEntity.llm_$setLeaningPitch(goal);
	}
}
