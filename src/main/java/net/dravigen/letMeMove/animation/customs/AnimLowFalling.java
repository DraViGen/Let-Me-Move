package net.dravigen.letMeMove.animation.customs;

import net.dravigen.letMeMove.animation.BaseAnimation;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.letMeMove.LetMeMoveAddon.crawl_key;
import static net.dravigen.letMeMove.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.letMeMove.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.letMeMove.utils.GeneralUtils.pi;

public class AnimLowFalling extends BaseAnimation {
	public static final ResourceLocation id = new ResourceLocation("LMM", "lowFalling");
	public final static int minFallHeight = 3;
	
	public AnimLowFalling() {
		super(id, 1.8f, 0.02f);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return player.fallDistance >= minFallHeight && player.fallDistance < AnimHighFalling.minFallHeight && !player.isSneaking() && !player.capabilities.isFlying;
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
		
		smoothRotateAll(model.bipedBody, 0, 0, 0, 0.6f * delta);
		
		smoothRotateAll(model.bipedHead, j * (pi / 180.0f), i * (pi / 180.0f), 0, 0.4f * delta);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		float sin = MathHelper.sin(leaning);
		float cos = MathHelper.cos(leaning);
		float cos1 = MathHelper.cos(leaning + 2);
		float sin1 = MathHelper.sin(leaning + 2);
		
		smoothRotateAll(model.bipedRightArm, cos * 0.65f, 0, 1.75f + sin * 0.65f, 0.6f * delta);
		
		smoothRotateAll(model.bipedLeftArm, cos1 * 0.65f, 0, -1.75f - sin1 * 0.65f, 0.6f * delta);
		
		smoothRotateAll(model.bipedRightLeg, -sin * 0.5f, 0, 0, 0.7f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, sin * 0.5f, 0, 0, 0.7f * delta);
	}
	
	@Override
	public void updateLeaning(EntityLivingBase entity) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		
		float goal = entity.ticksExisted % 200 / 1.75f;
		
		customEntity.llm_$setLeaningPitch(goal);
	}
}
