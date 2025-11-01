package net.dravigen.let_me_move.mixin;

import net.dravigen.let_me_move.LetMeMoveAddon;
import net.dravigen.let_me_move.animation.BaseAnimation;
import net.dravigen.let_me_move.packet.PacketUtils;
import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.dravigen.let_me_move.utils.AnimationUtils;
import net.dravigen.let_me_move.utils.GeneralUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dravigen.let_me_move.animation.AnimRegistry.*;

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
	@Unique
	private float deltaRender;
	
	public EntityLivingBaseMixin(World par1World) {
		super(par1World);
	}
	
	@Shadow
	protected abstract float func_110146_f(float par1, float par2);
	
	@Shadow
	public abstract boolean attackEntityFrom(DamageSource par1DamageSource, float par2);
	
	@Override
	public float llm_$getLeaningPitch() {
		return GeneralUtils.lerp(Minecraft.getMinecraft().getTimer().renderPartialTicks,
								 this.lastLeaningPitch,
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
	public BaseAnimation llm_$getAnimation() {
		if ((EntityLivingBase) (Object) this instanceof EntityPlayer) {
			if (this.currentAnimation == null) this.currentAnimation = STANDING.getID();
			
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
	public boolean llm_$isAnimation(ResourceLocation id) {
		if ((EntityLivingBase) (Object) this instanceof EntityPlayer) {
			if (this.currentAnimation == null) return false;
			
			return this.currentAnimation.equals(id);
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
	
	@Override
	public void llm_$setDelta(float deltaRender) {
		this.deltaRender = deltaRender;
	}
	
	@Override
	public float llm_$getDelta() {
		return this.deltaRender;
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
			if (this.llm_$isAnimation(SKY_DIVING.getID())) {
				if (this.moveForward == 0) {
					this.motionY *= 0.96;
				}
				else {
					this.motionY *= 0.98;
				}
				
				return this.llm_$getAnimation().speedModifier;
			}
			else if (this.llm_$isAnimation(HIGH_FALLING.getID())) {
				this.motionY *= 0.98;
				
				return this.llm_$getAnimation().speedModifier;
			}
			else if (this.llm_$isAnimation(DIVING.getID())) {
				if (this.motionY < 0) {
					this.motionY *= 1.02;
				}
				
				return this.llm_$getAnimation().speedModifier;
			}
			else if (this.llm_$isAnimation(WALL_SLIDING.getID())) {
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
		if (((ICustomMovementEntity) instance).llm_$isAnimation(WALL_SLIDING.getID())) {
			return true;
		}
		
		return instance.onGround;
	}
	
	@ModifyArg(method = "entityLivingBaseFall", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"), index = 1)
	private float lessFallDamageIfRolling(float damage) {
		if ((EntityLivingBase) (Object) this instanceof EntityPlayer) {
			int t = this.llm_$getAnimation().timeRendered;
			
			if (this.llm_$isAnimation(ROLLING.getID()) && t >= 10 && t <= 20) {
				return damage * 0.85f;
			}
		}
		
		return damage;
	}
	
	@Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;func_110146_f(FF)F"))
	private float disableHeadTurn(EntityLivingBase instance, float par1, float par2) {
		ICustomMovementEntity customEntity = (ICustomMovementEntity) instance;
		ResourceLocation animation = customEntity.llm_$getAnimationID();
		if (animation != null && instance instanceof EntityPlayer && customEntity.llm_$getAnimation().customBodyHeadRotation(instance)) {
			return par2;
		}
		
		return this.func_110146_f(par1, par2);
	}
}