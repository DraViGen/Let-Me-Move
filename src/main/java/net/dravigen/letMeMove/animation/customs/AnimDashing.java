package net.dravigen.letMeMove.animation.customs;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;

import static net.dravigen.letMeMove.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.letMeMove.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.letMeMove.utils.GeneralUtils.pi;

public class AnimDashing extends AnimCommon {
	public static final ResourceLocation id = new ResourceLocation("LMM", "dashing");
	private static int pressTime = 0;
	
	public AnimDashing() {
		super(id, 1.8f, 1, false, 20, 5, true, 0);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return player.moveForward == 0 && player.onGround && !player.doesStatusPreventSprinting();
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
}
