package net.dravigen.letMeMove.mixin.client.render;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.animation.AnimationCustom;
import net.dravigen.letMeMove.utils.GeneralUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.letMeMove.animation.AnimationRegistry.*;
import static net.dravigen.letMeMove.utils.GeneralUtils.checkEntityAgainstWall;

@Mixin(ModelBiped.class)
public abstract class ModelBipedMixin extends ModelBase {
	
	@Unique
	float prevXRotation = 0;
	@Unique
	float prevYRotation = 0;
	@Unique
	float prevZRotation = 0;
	@Unique
	float prevOffset = 0;
	@Unique
	long transitionTime = 0;
	@Unique
	ResourceLocation prevAnimation;
	@Unique
	long prevTime;
	@Unique
	boolean prevForward;
	@Unique
	float delta = 0;
	
	@Inject(method = "render", at = @At("HEAD"))
	private void rotateBody(Entity entity, float f, float g, float h, float i, float j, float u, CallbackInfo ci) {
		if (!(entity instanceof EntityPlayer player)) return;
		
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		AnimationCustom animation = customEntity.llm_$getAnimation();
		
		if (animation == null) return;
		
		float leaningPitch = customEntity.llm_$getLeaningPitch();
		
		if (prevAnimation != animation.getID() || (animation.getID().equals(
				SKYDIVING_ID) && player.moveForward > 0 != prevForward)) {
			prevAnimation = animation.getID();
			transitionTime = 1000;
		}
		
		prevForward = player.moveForward > 0;
		delta = (System.currentTimeMillis() - prevTime) / 25f;
		delta = delta > 8 ? 8 : delta;
		
		boolean tr = transitionTime > 0;
		
		delta = tr ? delta * 0.8f : delta;
		
		if (animation.needYOffsetUpdate) {
			if (customEntity.llm_$isAnimation(HIGH_FALLING_ID)) {
				prevOffset = GeneralUtils.incrementUntilGoal(prevOffset, 0.5f, 0.4f * delta);
				prevYRotation = GeneralUtils.incrementAngleUntilGoal(prevYRotation, (12f * leaningPitch) % 360,
						0.3f * delta);
				prevZRotation = GeneralUtils.incrementAngleUntilGoal(prevZRotation, (22.5f * leaningPitch) % 360,
						0.3f * delta);
				prevXRotation = GeneralUtils.incrementAngleUntilGoal(prevXRotation, (45f * leaningPitch) % 360,
						0.3f * delta);
			}
			else {
				prevOffset = animation.yOffset != 0 ? animation.yOffset : 1.98f - (entity.yOffset + 0.18f);
				prevYRotation = GeneralUtils.incrementAngleUntilGoal(prevYRotation, 0, 0.1f * delta);
				prevZRotation = GeneralUtils.incrementAngleUntilGoal(prevZRotation, 0, 0.1f * delta);
				prevXRotation = GeneralUtils.incrementAngleUntilGoal(prevXRotation, 90 * leaningPitch, 0.2f * delta);
			}
		}
		else {
			prevOffset = GeneralUtils.incrementUntilGoal(prevOffset, 0, 0.4f * delta);
			prevYRotation = GeneralUtils.incrementAngleUntilGoal(prevYRotation, 0, 0.2f * delta);
			prevZRotation = GeneralUtils.incrementAngleUntilGoal(prevZRotation, 0, 0.2f * delta);
			prevXRotation = GeneralUtils.incrementAngleUntilGoal(prevXRotation, 0, 0.35f * delta);
		}
		
		if (tr) {
			transitionTime -= System.currentTimeMillis() - prevTime;
			
			if (transitionTime < 0) transitionTime = 0;
		}
		
		prevTime = System.currentTimeMillis();
		
		GL11.glTranslatef(0, prevOffset, 0);
		GL11.glRotatef(prevYRotation, 0, 1, 0);
		GL11.glRotatef(prevZRotation, 0, 0, 1);
		GL11.glRotatef(prevXRotation, 1, 0, 0);
		
		if (customEntity.llm_$isAnimation(HIGH_FALLING_ID)) GL11.glTranslatef(0, -prevOffset, 0);
		if (customEntity.llm_$isAnimation(WALL_SLIDING_ID)) {
			GeneralUtils.coords side = checkEntityAgainstWall(player);
			player.renderYawOffset = side == GeneralUtils.coords.EAST ? 45 : side == GeneralUtils.coords.SOUTH ? 135 : side == GeneralUtils.coords.WEST ? 225 : 315;
		}
	}
	
	@Inject(method = "setRotationAngles", at = @At("HEAD"), cancellable = true)
	public void setAngles(float f, float g, float h, float i, float j, float u, Entity livingEntity, CallbackInfo ci) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) livingEntity;
		
		if (customEntity.llm_$getAnimation() == null) return;
		
		if (livingEntity instanceof EntityPlayer player) {
			ci.cancel();
			
			customEntity.llm_$getAnimation().renderAnimation((ModelBiped) (Object) this, player, f, g, h, i, j, u,
					delta);
		}
	}
}
