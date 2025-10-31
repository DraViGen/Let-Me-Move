package net.dravigen.let_me_move.animation.poses;

import net.minecraft.src.*;

import static net.dravigen.let_me_move.utils.AnimationUtils.resetAnimationRotationPoints;
import static net.dravigen.let_me_move.utils.AnimationUtils.smoothRotateAll;
import static net.dravigen.let_me_move.utils.GeneralUtils.*;

public class AnimFlying extends AnimCommon{
	public static final ResourceLocation id = new ResourceLocation("LMM", "flying");
	
	public AnimFlying() {
		super(id);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return player.capabilities.isFlying;
	}
	
	@Override
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i,
			float j, float u, float delta) {
		resetAnimationRotationPoints(model);
		
		i %= 360;
		
		i = i < -180 ? i + 360 : i > 180 ? i - 360 : i;
		
		float[] head = new float[]{j * (pi / 180.0f) / 1.25f, i * (pi / 180.0f) / 1.5f, 0};
		float[] body = new float[]{0, 0, 0};
		float[] rArm = new float[]{0, 0, 0};
		float[] lArm = new float[]{0, 0, 0};
		float[] rLeg = new float[]{0, 0, 0};
		float[] lLeg = new float[]{0, 0, 0};
		
		float forw = entity.moveForward;
		float straf = entity.moveStrafing;
		
		boolean backward = forw < 0;
		
		float yaw = forw > 0
					? (straf == 0 ? 0 : straf > 0 ? -45 : straf < 0 ? 45 : 0)
					: forw == 0
					  ? (straf > 0 ? -90 : straf < 0 ? 90 : 0)
					  : forw < 0 ? (straf > 0 ? 45 : straf < 0 ? -45 : 0) : 0;
	
		
		
		entity.renderYawOffset = incrementAngleUntilGoal(entity.renderYawOffset,
														 entity.rotationYaw + yaw,
														 delta * 0.1f);
	
		float mul = entity.motionY < 0 ? (float) -(entity.motionY * 48) : entity.motionY > 0 ? 0 : 1;
		
		rArm[2] = pi(1, 48) * mul;
		lArm[2] = -pi(1, 48) * mul;
		rLeg[2] = pi(1, 128);
		lLeg[2] = -pi(1, 128);
		
		if (forw != 0 || straf != 0) {
			g *= backward ? -1 : 1;
			
			head[0] += -g * pi(1, 8);
			rArm[2] += pi(1, 16);
			lArm[2] -= pi(1, 16);
			rArm[0] = g * pi(1, 4);
			lArm[0] = g * pi(1, 4);
			rLeg[0] = g * pi(1, 4);
			lLeg[0] = g * pi(1, 4);
			rLeg[2] += pi(1, 64);
			lLeg[2] -= pi(1, 64);
			
		}
		
		
		head[0] += sin(h / 8) * pi(1, 80);
		
		rArm[0] += (sin(h / 18f)) * pi(1, 16);
		lArm[0] += -(sin(h / (14f))) * pi(1, 16);
		rLeg[0] += (cos(h / 6f)) * pi(1, 16);
		lLeg[0] += (cos(h / 6f + pi)) * pi(1, 16);
		
		rArm[2] += (cos(h / 10f - pi(1, 4)) + 1) * pi(1, (int) (12 + 40 * Math.abs(entity.motionY)));
		lArm[2] += -(cos(h / (10f) - pi(1, 4)) + 1) * pi(1, (int) (12 + 40 * Math.abs(entity.motionY)));
		rLeg[2] += (cos(h / 10f - pi(1, 6)) + 1) * pi(1, 128);
		lLeg[2] += -(cos(h / (10f) - pi(1, 6)) + 1) * pi(1, 128);
		
		model.bipedHead.rotationPointY += sin(h / 10) * 1.25f;
		model.bipedBody.rotationPointY += sin(h / 10) * 1.25f;
		model.bipedRightArm.rotationPointY += sin(h / 10) * 1.25f;
		model.bipedLeftArm.rotationPointY += sin(h / 10) * 1.25f;
		model.bipedRightLeg.rotationPointY += sin(h / 10) * 1.25f;
		model.bipedLeftLeg.rotationPointY += sin(h / 10) * 1.25f;
		
		if (entity.moveForward > 0 || entity.moveStrafing != 0 && !(entity.moveForward < 0)) {
			model.bipedRightLeg.rotationPointZ -= 3;
			model.bipedRightLeg.rotationPointY -= 2;
		}
		
		
		moveAround(model, h, head, rArm, lArm, rLeg, lLeg, body);
		
		swingArm(model, body, rArm, lArm, head);
		
		smoothRotateAll(model.bipedBody, body, 0.5f * delta);
		
		smoothRotateAll(model.bipedHead, head, 0.5f * delta);
		
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		
		smoothRotateAll(model.bipedRightArm, rArm, 0.5f * delta);
		
		smoothRotateAll(model.bipedLeftArm, lArm, 0.5f * delta);
		
		smoothRotateAll(model.bipedRightLeg, rLeg, 0.5f * delta);
		
		smoothRotateAll(model.bipedLeftLeg, lLeg, 0.5f * delta);
		
	}
}
