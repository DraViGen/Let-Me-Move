package net.dravigen.let_me_move.mixin.client.render;

import net.dravigen.let_me_move.animation.AnimRegistry;
import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
	
	@Shadow
	private Minecraft mc;
	@Shadow
	private float thirdPersonDistance;
	@Shadow
	private float thirdPersonDistanceTemp;
	@Shadow
	private float prevDebugCamYaw;
	@Shadow
	private float debugCamYaw;
	@Shadow
	private float prevDebugCamPitch;
	@Shadow
	private float debugCamPitch;
	
	
	@Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/src/GameSettings;thirdPersonView:I", ordinal = 0, opcode = Opcodes.GETFIELD))
	private int betterThirdPerson(GameSettings instance, float par1) {
		if (instance.thirdPersonView > 0) {
			EntityLivingBase var2 = this.mc.renderViewEntity;
			double var4 = var2.prevPosX + (var2.posX - var2.prevPosX) * (double) par1;
			double var6 = var2.prevPosY + (var2.posY - var2.prevPosY) * (double) par1;
			double var8 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * (double) par1;
			double var27 = this.thirdPersonDistance + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * par1;
			
			if (this.mc.gameSettings.debugCamEnable) {
				float var28 = this.prevDebugCamYaw + (this.debugCamYaw - this.prevDebugCamYaw) * par1;
				float var13 = this.prevDebugCamPitch + (this.debugCamPitch - this.prevDebugCamPitch) * par1;
				
				GL11.glTranslatef(0.0f, 0.0f, (float) (-var27));
				GL11.glRotatef(var13, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(var28, 0.0f, 1.0f, 0.0f);
			}
			else {
				float var28 = var2.rotationYaw;
				float var13 = var2.rotationPitch;
				
				if (this.mc.gameSettings.thirdPersonView == 2) {
					var13 += 180.0f;
				}
				
				double var14 = (double) (-MathHelper.sin(var28 / 180.0f * (float) Math.PI) *
						MathHelper.cos(var13 / 180.0f * (float) Math.PI)) * var27;
				double var16 = (double) (MathHelper.cos(var28 / 180.0f * (float) Math.PI) *
						MathHelper.cos(var13 / 180.0f * (float) Math.PI)) * var27;
				double var18 = (double) (-MathHelper.sin(var13 / 180.0f * (float) Math.PI)) * var27;
				
				for (int var20 = 0; var20 < 8; ++var20) {
					double var25;
					float var21 = (var20 & 1) * 2 - 1;
					float var22 = (var20 >> 1 & 1) * 2 - 1;
					float var23 = (var20 >> 2 & 1) * 2 - 1;
					
					MovingObjectPosition var24 = this.mc.theWorld.clip(this.mc.theWorld.getWorldVec3Pool()
																			   .getVecFromPool(var4 +
																									   (double) (var21 *= 0.1f),
																							   var6 +
																									   (double) (var22 *= 0.1f),
																							   var8 +
																									   (double) (var23 *= 0.1f)),
																	   this.mc.theWorld.getWorldVec3Pool()
																			   .getVecFromPool(var4 - var14 +
																									   (double) var21 +
																									   (double) var23,
																							   var6 - var18 +
																									   (double) var22,
																							   var8 - var16 +
																									   (double) var23));
					
					if (var24 == null ||
							!((var25 = var24.hitVec.distanceTo(this.mc.theWorld.getWorldVec3Pool()
																	   .getVecFromPool(var4, var6, var8))) < var27))
						continue;
					
					var27 = var25;
				}
				
				if (this.mc.gameSettings.thirdPersonView == 2) {
					GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
				}
				
				GL11.glRotatef(var2.rotationPitch - var13, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(var2.rotationYaw - var28, 0.0f, 1.0f, 0.0f);
				GL11.glTranslatef(0.0f, 0.0f, (float) (-var27));
				GL11.glRotatef(var28 - var2.rotationYaw, 0.0f, 1.0f, 0.0f);
				GL11.glRotatef(var13 - var2.rotationPitch, 1.0f, 0.0f, 0.0f);
			}
		}
		
		return 0;
	}
	
	@Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 4), remap = false)
	private void a(float x, float y, float z) {
		GL11.glTranslatef(x, 0, z);
	}
	
	@Inject(method = "setupViewBobbing", at = @At("HEAD"), cancellable = true)
	private void disableBobbingWhileFastSwim(float par1, CallbackInfo ci) {
		if (((ICustomMovementEntity) this.mc.thePlayer).llm_$isAnimation(AnimRegistry.SWIMMING.getID())) {
			ci.cancel();
		}
	}
}
