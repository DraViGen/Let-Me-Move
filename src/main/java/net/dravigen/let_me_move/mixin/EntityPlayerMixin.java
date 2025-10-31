package net.dravigen.let_me_move.mixin;

import net.dravigen.let_me_move.animation.BaseAnimation;
import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.dravigen.let_me_move.packet.PacketUtils;
import net.dravigen.let_me_move.utils.AnimationUtils;
import net.dravigen.let_me_move.utils.GeneralUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static net.dravigen.let_me_move.animation.AnimRegistry.*;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase {
	
	@Unique
	private final List<BaseAnimation> animToCheckIfFail = Arrays.asList(AnimationUtils.getAnimationFromID(CRAWLING.getID()),
																		AnimationUtils.getAnimationFromID(CROUCHING.getID()));
	@Shadow
	protected boolean sleeping;
	
	public EntityPlayerMixin(World par1World) {
		super(par1World);
	}
	
	@Shadow
	public abstract float getEyeHeight();
	
	@Shadow
	public abstract boolean canSwim();
	
	@Shadow
	public abstract void addMovementStat(double par1, double par3, double par5);
	
	@Shadow public abstract boolean canJump();
	
	@Inject(method = "onUpdate", at = @At("HEAD"))
	private void updateAnimation(CallbackInfo ci) {
		if (this.sleeping) return;
		
		ICustomMovementEntity customPlayer = (ICustomMovementEntity) this;
		EntityPlayer player = (EntityPlayer) (Object) this;
		
		if (this.worldObj.isRemote) {
			for (BaseAnimation animation : AnimationUtils.getAnimationsMap().values()) {
				animation.updateAnimationTime(customPlayer.llm_$getAnimationID());
			}
			
			if (Minecraft.getMinecraft().gameSettings.keyBindLeft.pressed) {
				customPlayer.llm_$setSide(ICustomMovementEntity.side.LEFT);
			}
			else if (Minecraft.getMinecraft().gameSettings.keyBindRight.pressed) {
				customPlayer.llm_$setSide(ICustomMovementEntity.side.RIGHT);
			}
			
			ResourceLocation newID = new ResourceLocation("");
			
			if (!customPlayer.llm_$getAnimation().hasCooldown() || customPlayer.llm_$getAnimation().hasCooldown() && customPlayer.llm_$getAnimation().timeRendered == customPlayer.llm_$getAnimation().totalDuration) {
				for (BaseAnimation animation : AnimationUtils.getAnimationsMap().values()) {
					if (!animation.shouldActivateAnimation(player, this.boundingBox)) continue;
					
					if (animation.isGeneralConditonsMet(player, this.boundingBox)) {
						newID = animation.getID();
						
						break;
					}
				}
				
				newID = newID.equals(new ResourceLocation("")) ? STANDING.getID() : newID;
				
				AxisAlignedBB bounds = this.boundingBox.copy();
				
				boolean noCollisionWithBlock = this.worldObj.getCollidingBoundingBoxes(this, bounds).isEmpty();
				
				if (!newID.equals(customPlayer.llm_$getAnimationID())) {
					BaseAnimation newAnimation = AnimationUtils.getAnimationFromID(newID);
					float dHeight = newAnimation.height - customPlayer.llm_$getAnimation().height;
					
					if (dHeight > 0) {
						noCollisionWithBlock = this.worldObj.getCollidingBoundingBoxes(this,
																					   bounds.addCoord(0, dHeight, 0))
								.isEmpty();
					}
					
					if (noCollisionWithBlock) {
						customPlayer.llm_$setAnimation(newID);
					}
					else {
						dHeight = 0;
						
						for (BaseAnimation testAnimation : animToCheckIfFail) {
							bounds = this.boundingBox.copy();
							
							float dNewHeight = testAnimation.height - customPlayer.llm_$getAnimation().height;
							
							if (testAnimation.isGeneralConditonsMet(player, bounds) && dNewHeight > dHeight) {
								
								noCollisionWithBlock = this.worldObj.getCollidingBoundingBoxes(this,
																							   bounds.addCoord(0,
																											   dNewHeight,
																											   0))
										.isEmpty();
								
								if (noCollisionWithBlock) {
									dHeight = dNewHeight;
									newID = testAnimation.getID();
								}
							}
						}
						
						if (!newID.equals(STANDING.getID()) && !newID.equals(customPlayer.llm_$getAnimationID())) {
							customPlayer.llm_$setAnimation(newID);
						}
					}
				}
				else if (!this.worldObj.getCollidingBlockBounds(this.boundingBox).isEmpty() &&
						!GeneralUtils.isEntityFeetInsideBlock(this)) {
					customPlayer.llm_$setAnimation(CRAWLING.getID());
				}
			}
		}
		
		BaseAnimation currentAnimation = customPlayer.llm_$getAnimation();
		
		this.setSize(0.6f, currentAnimation.height);
	}
	
	@Inject(method = "moveEntityWithHeading", at = @At("HEAD"), cancellable = true)
	private void handleCustomMove(CallbackInfo ci) {
		ICustomMovementEntity customPlayer = (ICustomMovementEntity) this;
		double prevX = this.posX;
		double prevY = this.posY;
		double prevZ = this.posZ;
		
		BaseAnimation animation = customPlayer.llm_$getAnimation();
		
		if (animation == null) return;
		
		boolean shouldDisableBaseMove = animation.getCustomMove((EntityPlayer) (Object)this);
		
		if (shouldDisableBaseMove) {
			ci.cancel();
		}
		
		this.addMovementStat(this.posX - prevX, this.posY - prevY, this.posZ - prevZ);
	}
	
	@Inject(method = "addMovementStat", at = @At(value = "HEAD"), cancellable = true)
	private void customExhaustion(double distX, double distY, double distZ, CallbackInfo ci) {
		BaseAnimation animation = ((ICustomMovementEntity) this).llm_$getAnimation();
		
		if (animation == null) return;
		
		boolean disableBaseHunger = animation.getHungerCost((EntityPlayer) (Object)this, distX, distY, distZ);
		
		if (disableBaseHunger) {
			ci.cancel();
		}
	}
	
	@Inject(method = "jump", at = @At("TAIL"))
	private void addHorizontalMotionFromJumpingOnWall(CallbackInfo ci) {
		if (((ICustomMovementEntity) this).llm_$isAnimation(WALL_SLIDING.getID()) && this.canJump()) {
			GeneralUtils.coords side = GeneralUtils.getWallSide(this, 0, this.height);
			this.motionY += 0.15;
			this.motionX += side == GeneralUtils.coords.EAST ? -0.3f : side == GeneralUtils.coords.WEST ? 0.3f : 0;
			this.motionZ += side == GeneralUtils.coords.SOUTH ? -0.3f : side == GeneralUtils.coords.NORTH ? 0.3f : 0;
			PacketUtils.sendExhaustionToServer(1.33f * 0.25f);
		}
	}
}
