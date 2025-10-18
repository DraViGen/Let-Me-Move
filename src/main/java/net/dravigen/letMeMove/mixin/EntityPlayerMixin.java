package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.render.AnimationCustom;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.AnimationUtils;
import net.dravigen.letMeMove.utils.GeneralUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.letMeMove.render.AnimationRegistry.*;
import static net.dravigen.letMeMove.utils.GeneralUtils.*;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase {

    @Shadow public PlayerCapabilities capabilities;
    @Shadow protected boolean sleeping;

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    @Shadow public abstract float getEyeHeight();
    @Shadow public abstract void addStat(StatBase par1StatBase, int par2);
    @Shadow public abstract void addExhaustion(float par1);
    @Shadow public abstract void addMovementStat(double par1, double par3, double par5);
    @Shadow public abstract float getMovementSpeedModifierFromEffects();
    @Shadow public abstract boolean canSwim();

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void updateAnimation(CallbackInfo ci) {
        if (this.sleeping) return;

        ICustomMovementEntity customPlayer = (ICustomMovementEntity) this;
        EntityPlayer player = (EntityPlayer) (Object) this;

        if (this.worldObj.isRemote) {
            for (AnimationCustom animation : AnimationUtils.getAnimationsMap().values()) {
                animation.updateAnimationTime(customPlayer.llm_$getAnimationID());
            }

            if (Minecraft.getMinecraft().gameSettings.keyBindLeft.pressed) {
                customPlayer.llm_$setSide(ICustomMovementEntity.side.LEFT);
            }
            else if (Minecraft.getMinecraft().gameSettings.keyBindRight.pressed) {
                customPlayer.llm_$setSide(ICustomMovementEntity.side.RIGHT);
            }

            ResourceLocation newID = new ResourceLocation("");

            if (customPlayer.llm_$getAnimation().timeRendered == customPlayer.llm_$getAnimation().duration) {
                for (AnimationCustom animation : AnimationUtils.getAnimationsMap().values()) {
                    if (!animation.isGeneralConditonsMet(player, this.boundingBox)) continue;

                    if (animation.isActivationConditonsMet(player, this.boundingBox)) {
                        newID = animation.getID();

                        break;
                    }
                }

                newID = newID.equals(new ResourceLocation("")) ? STANDING_ID : newID;

                AxisAlignedBB bounds = this.boundingBox.copy();

                boolean noCollisionWithBlock = this.worldObj.getCollidingBoundingBoxes(this, bounds).isEmpty();

                if (!newID.equals(customPlayer.llm_$getAnimationID())) {
                    AnimationCustom newAnimation = AnimationUtils.getAnimationFromID(newID);
                    float dHeight = newAnimation.height - customPlayer.llm_$getAnimation().height;

                    if (dHeight > 0) {
                        noCollisionWithBlock = this.worldObj.getCollidingBoundingBoxes(this, bounds.addCoord(0, dHeight, 0)).isEmpty();
                    }

                    if (noCollisionWithBlock) {
                        customPlayer.llm_$setAnimation(newID);
                    }
                    else {
                        dHeight = 0;

                        for (AnimationCustom testAnimation : AnimationUtils.getAnimationsMap().values()) {
                            bounds = this.boundingBox.copy();

                            float dNewHeight = testAnimation.height - customPlayer.llm_$getAnimation().height;

                            if (testAnimation.isGeneralConditonsMet(player, bounds) && dNewHeight > dHeight) {

                                noCollisionWithBlock = this.worldObj.getCollidingBoundingBoxes(this, bounds.addCoord(0, dNewHeight, 0)).isEmpty();

                                if (noCollisionWithBlock) {
                                    dHeight = dNewHeight;
                                    newID = testAnimation.getID();
                                }
                            }
                        }

                        if (!newID.equals(STANDING_ID) && !newID.equals(customPlayer.llm_$getAnimationID())) {
                            customPlayer.llm_$setAnimation(newID);
                        }
                    }
                }
                else if (!this.worldObj.getCollidingBlockBounds(this.boundingBox).isEmpty() && !GeneralUtils.isEntityFeetInsideBlock(this)) {
                    customPlayer.llm_$setAnimation(SWIMMING_ID);
                }
            }
        }

        AnimationCustom currentAnimation = customPlayer.llm_$getAnimation();

        this.setSize(0.6f, currentAnimation.height);
    }

    @Unique boolean movedOnce = false;

    @Inject(method = "moveEntityWithHeading", at = @At("HEAD"), cancellable = true)
    private void handleCustomMove(CallbackInfo ci) {
        ICustomMovementEntity customPlayer = (ICustomMovementEntity) this;
        double prevX = this.posX;
        double prevY = this.posY;
        double prevZ = this.posZ;

        AnimationCustom animation = customPlayer.llm_$getAnimation();

        if (animation == null) return;

        ResourceLocation id = animation.getID();

        if (id.equals(SWIMMING_ID)) {
            if (!this.canSwim()) {
                customPlayer.llm_$setAnimation(STANDING_ID);
            }

            if (canFastSwimInWater()) {
                boolean b1 = !isHeadInsideWater(this) && isInsideWater(this);

                this.motionY = b1 && this.motionY > 0 ? 0 : this.motionY;

                if (this.moveForward > 0) {
                    ci.cancel();

                    Vec3 look = this.getLookVec();
                    Vec3 direction = look;

                    if ((isInsideWater(this) && look.yCoord < 0 && look.yCoord > -0.2) || b1 && look.yCoord > 0 && look.yCoord < 0.45) {
                        direction = Vec3.createVectorHelper(look.xCoord, 0, look.zCoord);
                    }

                    float speed = 0.2f * this.getMovementSpeedModifierFromEffects();

                    this.motionX = direction.xCoord * speed;
                    this.motionZ = direction.zCoord * speed;

                    super.moveEntity(this.motionX, direction.yCoord * speed + this.motionY, this.motionZ);
                    this.addMovementStat(this.posX - prevX, this.posY - prevY, this.posZ - prevZ);

                    this.motionY *= 0.5f;

                    this.prevLimbSwingAmount = this.limbSwingAmount;
                    double var9 = this.posX - this.prevPosX;
                    double var10 = this.posZ - this.prevPosZ;
                    float var12 = MathHelper.sqrt_double(var9 * var9 + var10 * var10) * 4.0f;

                    if (var12 > 1.0f) {
                        var12 = 1.0f;
                    }

                    this.limbSwingAmount += (var12 - this.limbSwingAmount) * 0.4f;
                    this.limbSwing += this.limbSwingAmount;
                }
                else {
                    /*
                    super.moveEntity(this.motionX, this.motionY, this.motionZ);
                    this.addMovementStat(this.posX - prevX, this.posY - prevY, this.posZ - prevZ);

                    this.motionX *= 0.8f;
                    this.motionY *= 0.8f;
                    this.motionZ *= 0.8f;
                    this.motionY -= 0.02;*/

                    this.limbSwingAmount = 0;
                }
            }
        }
        else if (id.equals(DASHING_ID)) {
            if (!movedOnce) {
                ci.cancel();

                float var1 = this.moveStrafing * 8;
                float var4 = var1 * var1;

                if (var4 >= 1.0E-4f) {
                    if ((var4 = MathHelper.sqrt_float(var4)) < 1.0f) {
                        var4 = 1.0f;
                    }

                    var4 = 1 / var4;
                    float var5 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0f);
                    float var6 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0f);
                    this.motionX = (var1 *= var4) * var6;
                    this.motionZ = var1 * var5;
                }

                super.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.addMovementStat(this.posX - prevX, this.posY - prevY, this.posZ - prevZ);

                movedOnce = true;
            }
        }
        else if (id.equals(ROLLING_ID)) {
            ci.cancel();

            super.moveEntityWithHeading(this.moveStrafing, 1.5f);
            this.addMovementStat(this.posX - prevX, this.posY - prevY, this.posZ - prevZ);
        }
        else {
            movedOnce = false;
        }
    }

    @Unique
    private boolean canFastSwimInWater() {
        return !this.isEating() && !this.capabilities.isFlying && isInsideWater(this) && ((ICustomMovementEntity) this).llm_$isAnimation(SWIMMING_ID);
    }

    @Inject(method = "addMovementStat", at = @At(value = "HEAD"), cancellable = true)
    private void customExhaustion(double par1, double par3, double par5, CallbackInfo ci) {
        AnimationCustom animation = ((ICustomMovementEntity) this).llm_$getAnimation();

        if (animation == null) return;

        ResourceLocation id = animation.getID();

        if (canFastSwimInWater() || id.equals(SWIMMING_ID) && this.onGround) {
            int var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5) * 100.0f);

            if (var7 > 0) {
                this.addStat(StatList.distanceDoveStat, var7);
                float modifier = isInsideWater(this) ? 1 : 0.75f;
                float par2 = 1.75f * modifier * var7 * 0.001f * this.worldObj.getDifficulty().getHungerIntensiveActionCostMultiplier();
                this.addExhaustion(par2);
            }

            ci.cancel();
        }
        else if (id.equals(DASHING_ID)) {
            int var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0f);

            if (var7 > 0) {
                this.addStat(StatList.distanceWalkedStat, var7);
                float par2 = 2f * (float) var7 * 0.001f * this.worldObj.getDifficulty().getHungerIntensiveActionCostMultiplier();
                this.addExhaustion(par2);

            }

            ci.cancel();
        }
        else if (id.equals(ROLLING_ID)) {
            int var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0f);

            if (var7 > 0) {
                this.addStat(StatList.distanceWalkedStat, var7);
                float par2 = 1.25f * (float) var7 * 0.001f * this.worldObj.getDifficulty().getHungerIntensiveActionCostMultiplier();
                this.addExhaustion(par2);
            }

            ci.cancel();
        }
    }
}
