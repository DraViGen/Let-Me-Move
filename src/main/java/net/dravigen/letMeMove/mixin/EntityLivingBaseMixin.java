package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.LetMeMoveAddon;
import net.dravigen.letMeMove.packet.PacketUtils;
import net.dravigen.letMeMove.animation.AnimationCustom;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.AnimationUtils;
import net.dravigen.letMeMove.utils.GeneralUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dravigen.letMeMove.animation.AnimationRegistry.*;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity implements ICustomMovementEntity {
	
	@Shadow
	public float moveForward;
	@Unique
	private float leaningPitch;
	@Unique
	private float lastLeaningPitch;
	@Unique
	private ResourceLocation currentAnimation;
	@Unique
	private side side;
	public EntityLivingBaseMixin(World par1World) {
		super(par1World);
	}

	@Shadow
	protected abstract float func_110146_f(float par1, float par2);
	
	@Shadow
	public abstract boolean attackEntityFrom(DamageSource par1DamageSource, float par2);
	
	@Override
	public float llm_$getLeaningPitch() {
		return GeneralUtils.lerp(Minecraft.getMinecraft().getTimer().renderPartialTicks, this.lastLeaningPitch,
				this.leaningPitch);
	}
	
	@Override
	public void llm_$setLeaningPitch(float pitch) {
		this.lastLeaningPitch = this.leaningPitch;
		this.leaningPitch = pitch;
	}
	
	@Override
	public ResourceLocation llm_$getAnimationID() {
		if ((EntityLivingBase) (Object) this instanceof EntityPlayer) {
			return this.currentAnimation;
		}
		else return null;
	}
	
	@Override
	public AnimationCustom llm_$getAnimation() {
		if ((EntityLivingBase) (Object) this instanceof EntityPlayer) {
			if (this.currentAnimation == null) this.currentAnimation = STANDING_ID;
			
			return AnimationUtils.getAnimationFromID(this.currentAnimation);
		}
		else {
			return null;
		}
	}
	
	@Override
	public void llm_$setAnimation(ResourceLocation ID) {
		if ((EntityLivingBase) (Object) this instanceof EntityPlayer player && this.currentAnimation != null) {
			
			if (this.worldObj.isRemote) {
				PacketUtils.animationCtoSSync(ID);
			}
			
			if (!ID.equals(this.currentAnimation)) {
				this.llm_$getAnimation().startCooldown();
			}
			
			this.currentAnimation = ID;
			
			player.setData(LetMeMoveAddon.CURRENT_ANIMATION, String.valueOf(ID));
		}
	}
	
	@Override
	public boolean llm_$isAnimation(ResourceLocation animationID) {
		if ((EntityLivingBase) (Object) this instanceof EntityPlayer) {
			if (this.currentAnimation == null) return false;
			
			return this.currentAnimation.equals(animationID);
		}
		else return false;
	}
	
	@Override
	public side llm_$getSide() {
		return this.side;
	}
	
	@Override
	public float llm_$getSideValue() {
		return side.equals(ICustomMovementEntity.side.LEFT) ? -1 : 1;
	}
	
	@Override
	public void llm_$setSide(side side) {
		this.side = side;
	}
	
	
	@Inject(method = "getSpeedModifier", at = @At("RETURN"), cancellable = true)
	private void applyAnimationSpeedModifier(CallbackInfoReturnable<Float> cir) {
		if (this.llm_$getAnimation() == null) return;
		
		cir.setReturnValue(cir.getReturnValueF() * this.llm_$getAnimation().speedModifier);
	}
	
	@ModifyArg(method = "moveEntityWithHeading", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;moveFlying(FFF)V", ordinal = 2), index = 2)
	private float customFallSpeed(float par1) {
		if (this.llm_$getAnimation() == null) return par1;
		
		if ((EntityLivingBase) (Object) this instanceof EntityPlayer player && !player.capabilities.isFlying) {
			if (this.llm_$isAnimation(SKYDIVING_ID)) {
				if (this.moveForward == 0) {
					this.motionY *= 0.96;
				}
				else {
					this.motionY *= 0.98;
				}
				
				return this.llm_$getAnimation().speedModifier;
			}
			else if (this.llm_$isAnimation(HIGH_FALLING_ID)) {
				this.motionY *= 0.98;
				
				return this.llm_$getAnimation().speedModifier;
			}
			else if (this.llm_$isAnimation(DIVING_ID)) {
				if (this.motionY < 0) {
					this.motionY *= 1.02;
				}
				
				return this.llm_$getAnimation().speedModifier;
			}
			else if (this.llm_$isAnimation(WALL_SLIDING_ID)) {
				this.motionY *= 0.85;
				
				if (fallDistance > 4) {
					this.fallDistance *= 0.85f;
				}
				
				if (fallDistance > 6) {
					if (this.attackEntityFrom(DamageSource.generic, (float) (2 * -this.motionY))) {
						this.hurtResistantTime -= 8;
					}
				}
			}
		}
		
		return par1;
	}
	
	@Redirect(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityLivingBase;onGround:Z"))
	private boolean allowJumpWhileWallSliding(EntityLivingBase instance) {
		if (((ICustomMovementEntity) instance).llm_$isAnimation(WALL_SLIDING_ID)) {
			return true;
		}
		
		return instance.onGround;
	}
	
	@ModifyArg(method = "entityLivingBaseFall", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"), index = 1)
	private float lessFallDamageIfRolling(float damage) {
		if ((EntityLivingBase) (Object) this instanceof EntityPlayer) {
			int t = this.llm_$getAnimation().timeRendered;
			
			if (this.llm_$isAnimation(ROLLING_ID) && t >= 10 && t <= 20) {
				return damage * 0.85f;
			}
		}
		
		return damage;
	}
	
	@Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;func_110146_f(FF)F"))
	private float disableHeadTurn(EntityLivingBase instance, float par1, float par2) {
		ResourceLocation animation = ((ICustomMovementEntity) instance).llm_$getAnimationID();
		
		if (animation != null && (animation.equals(WALL_SLIDING_ID) || animation.equals(WALL_PULLING_ID))) {
			return par2;
		}
		
		return this.func_110146_f(par1, par2);
	}
}