package net.dravigen.let_me_move.animation.customs;

import net.minecraft.src.*;

import static net.dravigen.let_me_move.utils.AnimationUtils.*;
import static net.dravigen.let_me_move.utils.GeneralUtils.*;
import static net.dravigen.let_me_move.utils.GeneralUtils.sin;

public class AnimClimbing extends AnimCommon{
	public static final ResourceLocation id = new ResourceLocation("LMM", "climbing");
	
	public AnimClimbing() {
		super(id, 1.8f, 1f);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return player.isOnLadder();
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return true;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		resetAnimationRotationPoints(model);
		
		float[] body = new float[]{0, 0, 0};
		float[] rArm = new float[]{0, 0, 0};
		float[] lArm = new float[]{0, 0, 0};
		float[] rLeg = new float[]{0, 0, 0};
		float[] lLeg = new float[]{0, 0, 0};
		
		rArm[0] = sin((float) entity.posY * 2) - pi(1, 2);
		lArm[0] = -sin((float) entity.posY * 2) - pi(1, 2);
		rLeg[0] = -sin((float) entity.posY * 2) / 2 - pi(1, 4);
		lLeg[0] = sin((float) entity.posY * 2) / 2 - pi(1, 4);
		
		smoothRotateAll(model.bipedBody, body, 1);
		
		i %= 360;
		
		i = i < -180 ? i + 360 : i > 180 ? i - 360 : i;
		
		System.out.println(i);
		
		smoothRotateAll(model.bipedHead,
						j * (pi / 180.0f),
						MathHelper.clamp_float(i * (pi / 180.0f), -pi / 2f, pi / 2f),
						0,
						1);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.3f * delta);
	}
}
