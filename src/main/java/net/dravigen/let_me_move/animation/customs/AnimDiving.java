package net.dravigen.let_me_move.animation.customs;

import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.let_me_move.LetMeMoveAddon.crawl_key;
import static net.dravigen.let_me_move.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.let_me_move.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.let_me_move.utils.GeneralUtils.*;
import static net.dravigen.let_me_move.utils.GeneralUtils.method_2807;

public class AnimDiving extends AnimCommon {
	public static ResourceLocation id = new ResourceLocation("LMM", "diving");
	
	public AnimDiving() {
		super(id, 0.8f, 0.005f, true);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !player.isEating() && !player.onGround && !isInsideWater(player);
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return crawl_key.pressed;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
		float leaningPitch = Math.min(1.0F, customMoveEntity.llm_$getLeaningPitch());
		
		resetAnimationRotationPoints(model);
		
		smoothRotateAll(model.bipedBody, 0, 0, 0, 0.5f * delta);
		
		smoothRotateAll(model.bipedHead,
						leaningPitch > 0.0F
						? lerpAngle(leaningPitch, model.bipedHead.rotateAngleX, -pi / 4)
						: j * (pi / 180.0f),
						i * (pi / 180.0f),
						0,
						1);
		
		if (leaningPitch > 0.0F) {
			smoothRotateAll(model.bipedRightArm,
							lerp(leaningPitch, model.bipedRightArm.rotateAngleX, 0.0F),
							lerp(leaningPitch, model.bipedRightArm.rotateAngleY, pi),
							lerp(leaningPitch,
								 model.bipedRightArm.rotateAngleZ,
								 pi - 1.8707964F * method_2807(0) / method_2807(14.0F)),
							0.15f * delta);
			
			smoothRotateAll(model.bipedLeftArm,
							lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleX, 0.0F),
							lerpAngle(leaningPitch, model.bipedLeftArm.rotateAngleY, pi),
							lerpAngle(leaningPitch,
									  model.bipedLeftArm.rotateAngleZ,
									  pi + 1.8707964F * method_2807(0) / method_2807(14.0F)),
							0.15f * delta);
			
			float n = customMoveEntity.llm_$getLeaningPitch();
			
			model.bipedRightLeg.rotationPointY = 11.3f;
			model.bipedLeftLeg.rotationPointY = 11.3f;
			
			smoothRotateAll(model.bipedRightLeg, -1f + n / 2, 0, 0, delta);
			
			smoothRotateAll(model.bipedLeftLeg, -1f + n / 2, 0, 0, delta);
		}
		
		if (entity instanceof EntityPlayer) {
			if (entity.getCurrentItemOrArmor(2) == null) {
				model.bipedCloak.rotationPointZ = 0.0F;
				model.bipedCloak.rotationPointY = 0.0F;
			}
			else {
				model.bipedCloak.rotationPointZ = -1.1F;
				model.bipedCloak.rotationPointY = -0.85F;
			}
		}
	}
	
	@Override
	public void updateLeaning(EntityLivingBase entity) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		float pitch = (float) ((-entity.motionY) * 1.25f);
		float goal = (pitch > 1 ? 1 : pitch) + 1;
		
		entity.limbSwingAmount = 0;
		entity.limbSwing = 0;
		
		customEntity.llm_$setLeaningPitch(goal);
	}
	
	@Override
	public boolean getHungerCost(EntityPlayer player, double distX, double distY, double distZ) {
		if (distY > 0) {
			float par2 = 1.33f * 0.05f * getHungerDifficultyMultiplier(player);
			player.addExhaustion(par2);
		}
		
		return false;
	}
}
