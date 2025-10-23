package net.dravigen.let_me_move.mixin.client.render;

import btw.block.BTWBlocks;
import btw.entity.model.PlayerArmorModel;
import net.dravigen.let_me_move.animation.BaseAnimation;
import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.dravigen.let_me_move.utils.GeneralUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.let_me_move.animation.AnimRegistry.*;

@Mixin(ModelBiped.class)
public abstract class ModelBipedMixin extends ModelBase {
	
	@Shadow public ModelRenderer bipedHead;
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
	
	@Inject(method = "render", at = @At("HEAD"))
	private void rotateBody(Entity entity, float f, float g, float h, float i, float j, float u, CallbackInfo ci) {
		if (!(entity instanceof EntityPlayer player) || (ModelBiped)(Object)this instanceof PlayerArmorModel) return;
		
		ICustomMovementEntity customEntity = (ICustomMovementEntity) entity;
		BaseAnimation animation = customEntity.llm_$getAnimation();
		
		if (animation == null) return;
		
		float leaningPitch = customEntity.llm_$getLeaningPitch();
		
		if (prevAnimation != animation.getID() ||
				(animation.getID().equals(SKY_DIVING.getID()) && player.moveForward > 0 != prevForward)) {
			prevAnimation = animation.getID();
			transitionTime = 1000;
		}
		
		prevForward = player.moveForward > 0;
		float delta = (System.currentTimeMillis() - prevTime) / 25f;
		delta = delta > 8 ? 8 : delta;
		
		boolean tr = transitionTime > 0;
		
		delta = tr ? delta * 0.8f : delta;
		
		customEntity.llm_$setDelta(delta);
		
		if (animation.needYOffsetUpdate) {
			if (customEntity.llm_$isAnimation(HIGH_FALLING.getID())) {
				prevOffset = GeneralUtils.incrementUntilGoal(prevOffset, 0.5f, 0.4f * delta);
				prevYRotation = GeneralUtils.incrementAngleUntilGoal(prevYRotation,
																	 (12f * leaningPitch) % 360,
																	 0.3f * delta);
				prevZRotation = GeneralUtils.incrementAngleUntilGoal(prevZRotation,
																	 (22.5f * leaningPitch) % 360,
																	 0.3f * delta);
				prevXRotation = GeneralUtils.incrementAngleUntilGoal(prevXRotation,
																	 (45f * leaningPitch) % 360,
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
		
		if (customEntity.llm_$isAnimation(HIGH_FALLING.getID())) GL11.glTranslatef(0, -prevOffset, 0);
		
		if (customEntity.llm_$isAnimation(WALL_SLIDING.getID())) {
			GeneralUtils.coords side = GeneralUtils.getWallSide(player, 0, entity.height);
			
			if (side != null) {
				player.renderYawOffset = side == GeneralUtils.coords.EAST
										 ? 45
										 : side == GeneralUtils.coords.SOUTH
										   ? 135
										   : side == GeneralUtils.coords.WEST ? 225 : 315;
			}
		}
		else if (customEntity.llm_$isAnimation(PULLING_UP.getID())) {
			GeneralUtils.coords side = GeneralUtils.getWallSide(player, 0, entity.height);
			
			if (side != null) {
				player.renderYawOffset = side == GeneralUtils.coords.EAST
										 ? 270
										 : side == GeneralUtils.coords.SOUTH
										   ? 0
										   : side == GeneralUtils.coords.WEST ? 90 : 180;
			}
		}
		else if (customEntity.llm_$isAnimation(CLIMBING.getID())) {
			int x = MathHelper.floor_double(entity.posX);
			int y = MathHelper.floor_double(entity.boundingBox.minY);
			int z = MathHelper.floor_double(entity.posZ);
			
			if (entity.worldObj.getBlockId(x, y, z) == BTWBlocks.ladder.blockID) {
				int ladderMeta = entity.worldObj.getBlockMetadata(x, y, z);
				
				player.renderYawOffset = switch (ladderMeta) {
					case 0 -> GeneralUtils.incrementAngleUntilGoal(player.renderYawOffset, 0, 1);
					case 1 -> GeneralUtils.incrementAngleUntilGoal(player.renderYawOffset, 180, 1);
					case 2 -> GeneralUtils.incrementAngleUntilGoal(player.renderYawOffset, 270, 1);
					case 3 -> GeneralUtils.incrementAngleUntilGoal(player.renderYawOffset, 90, 1);
					default -> player.renderYawOffset;
				};
			}
		}
	}
	
	@Inject(method = "setRotationAngles", at = @At("HEAD"), cancellable = true)
	public void setAngles(float f, float g, float h, float i, float j, float u, Entity livingEntity, CallbackInfo ci) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) livingEntity;
		
		if (customEntity.llm_$getAnimation() == null) return;
		
		if (livingEntity instanceof EntityPlayer player) {
			ci.cancel();
			customEntity.llm_$getAnimation()
					.renderAnimation((ModelBiped) (Object) this, player, f, g, h, i, j, u, customEntity.llm_$getDelta());
		}
	}
}
