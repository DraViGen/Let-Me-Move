package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.render.AnimationCustom;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.AnimationUtils;
import net.dravigen.letMeMove.utils.GeneralUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dravigen.letMeMove.render.AnimationRegistry.*;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity implements ICustomMovementEntity {

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }

    @Unique
    private ResourceLocation currentAnimation = STANDING_ID;
    @Unique
    private float leaningPitch;
    @Unique
    private float lastLeaningPitch;

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
        return this.currentAnimation;
    }

    @Override
    public boolean llm_$isAnimation(ResourceLocation animationID) {
        return this.currentAnimation.equals(animationID);
    }

    @Override
    public AnimationCustom llm_$getAnimation() {
        return AnimationUtils.getAnimationFromID(this.currentAnimation);
    }

    @Override
    public void llm_$setAnimation(ResourceLocation ID) {
        this.currentAnimation = ID;
    }


    @Inject(method = "getSpeedModifier",at = @At("RETURN"), cancellable = true)
    private void applyAnimationSpeedModifier(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValueF() * AnimationUtils.getAnimationFromID(this.currentAnimation).speedModifier);
    }

    @Redirect(method = "onEntityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;isInsideOfMaterial(Lnet/minecraft/src/Material;)Z"))
    private boolean customBreathCheck(EntityLivingBase instance, Material material) {
        return GeneralUtils.isHeadInsideWater(instance);
    }

    @ModifyArg(method = "moveEntityWithHeading",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;moveFlying(FFF)V", ordinal = 2), index = 2)
    private float flySpeedModifier(float par1) {
        if (this.llm_$isAnimation(SKYDIVING_ID)) {
            this.motionY *= 0.98;
            return this.llm_$getAnimation().speedModifier;
        }
        else if (this.llm_$isAnimation(HIGH_FALLING_ID)) {
            return 0.01f;
        }
        else {
            return par1;
        }
    }

/*
    @ModifyConstant(method = "moveEntityWithHeading", constant = @Constant(doubleValue = 0.08))
    private double slowFallWhileSkyDiving(double constant) {
        if (this.llm_$isAnimation(SKYDIVING_ID)) {
            return 0.04;
        }
        else {
            return constant;
        }
    }*/
}
