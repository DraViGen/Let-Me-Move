package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.EnumPose;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.LMMUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity implements ICustomMovementEntity {

    @Shadow public float limbSwingAmount;

    @Shadow public float limbSwing;

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }

    @Unique
    private int customMovementState = 0;
    @Unique
    private float leaningPitch;

    @Environment(EnvType.CLIENT)
    @Override
    public float letMeMove_$getLeaningPitch() {
        return LMMUtils.lerp(Minecraft.getMinecraft().getTimer().renderPartialTicks, this.lastLeaningPitch, this.leaningPitch);
    }
    @Override
    public int letMeMove_$getCustomMovementState() {
        return this.customMovementState;
    }

    @Override
    public void letMeMove_$setLeaningPitch(float pitch) {
        this.leaningPitch = pitch;
    }

    @Override
    public void letMeMove_$setCustomMovementState(EnumPose state) {
        this.customMovementState = state.ordinal();
    }


    @Unique
    private float lastLeaningPitch;

    @Inject(method = "onLivingUpdate",at = @At("HEAD"))
    private void updateLeaningPitch(CallbackInfo ci) {
        this.lastLeaningPitch = this.leaningPitch;

        if (this.customMovementState == EnumPose.DIVING.ordinal()) {

            this.limbSwingAmount = 0;
            this.limbSwing = 0;

            float pitch = (this.fallDistance - 2) / 10;
            pitch = pitch > 1 ? 1 : pitch;

            this.leaningPitch = pitch + 1;
        }
        else if (this.customMovementState == EnumPose.SWIMMING.ordinal()) {
            if (this.inWater) {
                float pitch = (this.rotationPitch + 90) / 90f;

                float difference = pitch - this.leaningPitch;

                if (Math.abs(difference) <= 0.09) {
                    this.leaningPitch = pitch;
                }else {
                    if (difference > 0) {
                        this.leaningPitch = this.leaningPitch + 0.09f;
                    } else {
                        this.leaningPitch = this.leaningPitch - 0.09f;
                    }
                }
            } else if (!this.onGround && this.fallDistance > 2) {
                float pitch = (this.fallDistance-2) / 10;
                pitch = pitch > 1 ? 1 : pitch;

                this.limbSwingAmount = (float) Math.abs(this.motionY);

                this.leaningPitch = pitch + 1;
            }
            else this.leaningPitch = Math.min(1.0F, this.leaningPitch + 0.09F);
        }
        else {
            this.leaningPitch = Math.max(0.0F, this.leaningPitch - 0.09F);
        }
    }

    @Inject(method = "getSpeedModifier",at = @At("RETURN"), cancellable = true)
    private void applyCurrentPoseSpeedMultiplier(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValueF() * EnumPose.getPose(this.customMovementState).movementMultiplier);
    }
}
