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
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		resetAnimationRotationPoints(model);
		
		i %= 360;
		
		i = i < -180 ? i + 360 : i > 180 ? i - 360 : i;
		
		float[] head = new float[]{
				j * (pi / 180.0f),
				MathHelper.clamp_float(i * (pi / 180.0f) / 1.25f, -pi(2, 5), pi(2, 5)),
				i * (pi / 180.0f) / 6
		};
		float[] body = new float[]{0, 0, 0};
		float[] rArm = new float[]{0, 0, 0};
		float[] lArm = new float[]{0, 0, 0};
		float[] rLeg = new float[]{0, 0, 0};
		float[] lLeg = new float[]{0, 0, 0};
		
		rArm[0] = -pi(3, 5);
		lArm[0] = -pi(3, 5);
		
		rArm[0] += cos((float) entity.posY * 4) * pi(1, 4);
		lArm[0] += cos((float) entity.posY * 4 + pi) * pi(1, 4);
		
		rArm[1] += Math.max(0, -sin((float) entity.posY * 4 + pi)) * pi(1, 6);
		lArm[1] += Math.min(0, -sin((float) entity.posY * 4 + pi)) * pi(1, 6);
		
		body[1] += -cos((float) entity.posY * 4) * pi(1, 4) * pi(1, 16);
		model.bipedHead.rotationPointX -= cos((float) entity.posY * 4) * 0.75f;
		
		model.bipedBody.rotationPointY -= 1;
		
		model.bipedRightArm.rotationPointY = (cos((float) entity.posY * 4) + 0.75f) * 2;
		model.bipedLeftArm.rotationPointY = (cos((float) entity.posY * 4 + pi) + 0.75f) * 2;
		
		model.bipedRightArm.rotationPointZ = Math.max(-3, (sin((float) entity.posY * 4)) * 1.5f);
		model.bipedLeftArm.rotationPointZ = Math.max(-3, (sin((float) entity.posY * 4 + pi)) * 1.5f);
		
		
		rLeg[0] = -pi(1, 12);
		lLeg[0] = -pi(1, 12);
		
		rLeg[2] = pi(1, 100);
		lLeg[2] = -pi(1, 100);
		rLeg[1] = pi(1, 100);
		lLeg[1] = -pi(1, 100);
		
		body[0] = -pi(1, 20);
		
		
		model.bipedBody.rotationPointZ = sin(body[0]) * 12;

		model.bipedRightLeg.rotationPointZ = sin(body[0]) * 12 + Math.max((sin((float) (entity.posY * 4 + pi)) - 1), -1.5f);
		model.bipedLeftLeg.rotationPointZ = sin(body[0]) * 12 + Math.max((sin((float) (entity.posY * 4)) - 1), -1.5f);
		
		model.bipedRightLeg.rotationPointY += -2 + (cos((float) (entity.posY * 4 + pi)) - 1) * 2;
		model.bipedLeftLeg.rotationPointY += -2 + (cos((float) (entity.posY * 4)) - 1) * 2;
		
		breath(model, h, head, rArm, lArm, rLeg, lLeg, body);
		
		swingArm(model, body, rArm, lArm, head);
		
		smoothRotateAll(model.bipedBody, body, 0.75f);

		smoothRotateAll(model.bipedHead, head, 0.75f);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.5f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.5f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.5f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.5f * delta);
	}
}
