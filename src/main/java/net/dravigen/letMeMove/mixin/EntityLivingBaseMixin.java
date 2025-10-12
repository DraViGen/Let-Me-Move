package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.LetMeMoveAddon;
import net.dravigen.letMeMove.packet.PacketUtils;
import net.dravigen.letMeMove.render.AnimationCustom;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.AnimationUtils;
import net.dravigen.letMeMove.utils.GeneralUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dravigen.letMeMove.render.AnimationRegistry.*;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity implements ICustomMovementEntity {

    @Shadow public float moveForward;
    @Unique private float leaningPitch;
    @Unique private float lastLeaningPitch;
    @Unique private ResourceLocation currentAnimation;

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }

    @Override
    public float llm_$getLeaningPitch() {
        return GeneralUtils.lerp(Minecraft.getMinecraft().getTimer().renderPartialTicks, this.lastLeaningPitch, this.leaningPitch);
    }

    @Override
    public void llm_$setLeaningPitch(float pitch) {
        this.lastLeaningPitch = this.leaningPitch;
        this.leaningPitch = pitch;
    }

    @Override
    public ResourceLocation llm_$getAnimationID() {
        if ((EntityLivingBase) (Object) this instanceof EntityPlayer player) {
            return this.currentAnimation;
        }
        else return null;
    }

    @Override
    public boolean llm_$isAnimation(ResourceLocation animationID) {
        if ((EntityLivingBase) (Object) this instanceof EntityPlayer player) {
            if (this.currentAnimation == null) return false;

            return this.currentAnimation.equals(animationID);
        }
        else return false;
    }

    @Override
    public AnimationCustom llm_$getAnimation() {
        if ((EntityLivingBase) (Object) this instanceof EntityPlayer player) {
            if (this.currentAnimation == null) this.currentAnimation = STANDING_ID;

            return AnimationUtils.getAnimationFromID(this.currentAnimation);
        }
        else {
            return null;
        }
    }

    @Override
    public void llm_$setAnimation(ResourceLocation ID) {
        if ((EntityLivingBase) (Object) this instanceof EntityPlayer player) {

            if (this.worldObj.isRemote) {
                PacketUtils.animationCtoSSync(ID);
            }

            this.currentAnimation = ID;

            this.llm_$getAnimation().startCooldown();

            player.setData(LetMeMoveAddon.CURRENT_ANIMATION, String.valueOf(ID));
        }
    }

    @Inject(method = "getSpeedModifier", at = @At("RETURN"), cancellable = true)
    private void applyAnimationSpeedModifier(CallbackInfoReturnable<Float> cir) {
        if (this.llm_$getAnimation() == null) return;

        cir.setReturnValue(cir.getReturnValueF() * this.llm_$getAnimation().speedModifier);
    }

    @ModifyArg(method = "moveEntityWithHeading", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;moveFlying(FFF)V", ordinal = 2), index = 2)
    private float flySpeedModifier(float par1) {
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
        }

        return par1;
    }
}