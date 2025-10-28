package net.dravigen.let_me_move.animation.poses;

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
		return !player.onGround && player.isOnLadder();
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return true;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		resetAnimationRotationPoints(model);
		
		i %= 360;
		
		i = i < -180 ? i + 360 : i > 180 ? i - 360 : i;
		
		float[] head = new float[]{j * (pi / 180.0f), MathHelper.clamp_float(i * (pi / 180.0f), -pi / 2f, pi / 2f), 0};
		float[] body = new float[]{0, 0, 0};
		float[] rArm = new float[]{0, 0, 0};
		float[] lArm = new float[]{0, 0, 0};
		float[] rLeg = new float[]{0, 0, 0};
		float[] lLeg = new float[]{0, 0, 0};
		
		rArm[0] = sin((float) entity.posY * 4) - pi(1, 2);
		lArm[0] = -sin((float) entity.posY * 4) - pi(1, 2);
		rLeg[0] = -pi(1, 16);
		lLeg[0] = -pi(1, 16);
		
		rLeg[2] = pi(1, 100);
		lLeg[2] = -pi(1, 100);
		rLeg[1] = pi(1, 100);
		lLeg[1] = -pi(1, 100);
		
		body[0] = -pi(1, 12);
		
		model.bipedRightArm.rotationPointZ = 0;
		model.bipedLeftArm.rotationPointZ = 0;
		
		model.bipedBody.rotationPointZ = sin(body[0]) * 12;
		
		model.bipedRightLeg.rotationPointZ = sin(body[0]) * 12 + 1 + (sin((float) (entity.posY * 4)) - 1);
		model.bipedLeftLeg.rotationPointZ = sin(body[0]) * 12 + 1 + (sin((float) (entity.posY * 4 + pi)) - 1);
		
		model.bipedRightLeg.rotationPointY += -2 + (cos((float) (entity.posY * 4)) - 1) * 2;
		model.bipedLeftLeg.rotationPointY += -2 + (cos((float) (entity.posY * 4 + pi)) - 1) * 2;
		
		moveAround(model, h, head, rArm, lArm, rLeg, lLeg, body);
		
		smoothRotateAll(model.bipedBody, body, 0.75f);

		smoothRotateAll(model.bipedHead, head, 0.75f);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.3f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.3f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.3f * delta);
	}
}
