package net.dravigen.letMeMove.animation.customs;

import net.dravigen.letMeMove.animation.BaseAnimation;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.letMeMove.LetMeMoveAddon.roll_key;
import static net.dravigen.letMeMove.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.letMeMove.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.letMeMove.utils.GeneralUtils.checkIfEntityFacingWall;
import static net.dravigen.letMeMove.utils.GeneralUtils.pi;

public class AnimRolling extends BaseAnimation {
	public static final ResourceLocation id = new ResourceLocation("LMM", "rolling");
	private static float prevPitch;
	
	public AnimRolling() {
		super(id, 1.8f, 1, true, 20, 25, true);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !player.doesStatusPreventSprinting() && !player.inWater && !checkIfEntityFacingWall(player);
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return roll_key.pressed;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		float leaning = customEntity.llm_$getLeaningPitch();
		
		resetAnimationRotationPoints(model);
		
		smoothRotateAll(model.bipedBody, 0, 0, 0, 0.6f * delta);
		
		BaseAnimation animation = customEntity.llm_$getAnimation();
		
		float[] head = new float[]{0, 0, 0};
		float[] rArm = new float[]{0, 0, 0};
		float[] lArm = new float[]{0, 0, 0};
		float[] rLeg = new float[]{0, 0, 0};
		float[] lLeg = new float[]{0, 0, 0};
		
		int t = animation.timeRendered;
		if (t < 10) {
			customEntity.llm_$getAnimation().height = 1.8f - t / 10f;
			rArm[0] = -pi;
			lArm[0] = -pi;
			rLeg[0] = -pi * 0.25f;
			lLeg[0] = -pi * 0.25f;
			head[0] = pi * 0.25f;
			head[1] = 0;
		}
		else if (t < 20) {
			if (t > 15) customEntity.llm_$getAnimation().height = 0.8f + (t - 15) / 5f;
			else customEntity.llm_$getAnimation().height = 0.8f;
			
			rArm[0] = -pi * 0.8f;
			lArm[0] = -pi * 0.8f;
			rLeg[0] = -pi * 0.5f;
			lLeg[0] = -pi * 0.5f;
			head[0] = pi * 0.4f;
			head[1] = 0;
		}
		else {
			customEntity.llm_$getAnimation().height = 1.8f;
			head[0] = j * (pi / 180.0f);
			head[1] = i * (pi / 180.0f);
		}
		
		smoothRotateAll(model.bipedHead, head, 0.4f * delta);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.3f * delta);
		
		if (Minecraft.getMinecraft().thePlayer == entity && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			if (leaning == 0) {
				prevPitch = entity.cameraPitch;
			}
			else if (leaning != 4) {
				entity.cameraPitch = prevPitch + leaning * 90;
			}
		}
	}
	
	@Override
	public void updateLeaning(EntityLivingBase entity) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		
		BaseAnimation animation = customEntity.llm_$getAnimation();
		float goal = (float) animation.timeRendered / ((animation.duration - 5) / 4.5f);
		if (animation.timeRendered > (animation.duration - 5) / 4.5f * 4) goal = 4;
		
		customEntity.llm_$setLeaningPitch(goal);
	}
}
