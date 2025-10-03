package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.EnumPose;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase {
    @Shadow protected boolean sleeping;

    @Shadow public abstract float getEyeHeight();

    @Shadow public abstract void addStat(StatBase par1StatBase, int par2);

    @Shadow public abstract void addExhaustion(float par1);

    @Shadow public abstract void addMovementStat(double par1, double par3, double par5);

    @Shadow public abstract float getMovementSpeedModifierFromEffects();

    @Shadow public abstract boolean canSwim();

    @Shadow public PlayerCapabilities capabilities;

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void updatePose(CallbackInfo ci) {
        if (this.sleeping) return;

        EnumPose newPoseState;
        ICustomMovementEntity playerMove = (ICustomMovementEntity) this;
        int currentPose = playerMove.letMeMove_$getCustomMovementState();

        if (Keyboard.isKeyDown(Keyboard.KEY_C) || (isInsideWater() && this.isUsingSpecialKey() && !this.capabilities.isFlying)) {
            newPoseState = EnumPose.CRAWLING;
        } else if (this.isSneaking()) {
            newPoseState = EnumPose.SNEAKING;
        } else {
            newPoseState = EnumPose.STANDING;
        }

        List moveRangeCollisionList = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox);

        if (currentPose != newPoseState.ordinal()) {
            float dHeight = newPoseState.height - EnumPose.getPose(currentPose).height;

            if (dHeight > 0) {
                moveRangeCollisionList = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(0, dHeight, 0));
            }

            if (moveRangeCollisionList.isEmpty()) {
                playerMove.letMeMove_$setCustomMovementState(newPoseState);
            }
            else {
                for (EnumPose pose : EnumPose.values()) {
                    AxisAlignedBB bounds = this.boundingBox.copy();
                    float dNewHeight = pose.height - EnumPose.getPose(currentPose).height;
                    if (dNewHeight < dHeight) {
                        dHeight = dNewHeight;


                        moveRangeCollisionList = this.worldObj.getCollidingBoundingBoxes(this, bounds.addCoord(0, dHeight, 0));

                        if (moveRangeCollisionList.isEmpty()) {
                            playerMove.letMeMove_$setCustomMovementState(pose);
                            break;
                        }
                    }
                }
            }
        } else if (this.isEntityInsideOpaqueBlock() && !this.isEntityFeetInsideOpaqueBlock()) {
            playerMove.letMeMove_$setCustomMovementState(EnumPose.CRAWLING);
        }

        EnumPose actualPose =  playerMove.getPose();

        this.setSize(0.6f,actualPose.height);
        if (this.worldObj.isRemote) {
            this.yOffset =  actualPose.height - 0.18f;
        }
    }

    @Inject(method = "onLivingUpdate",at = @At("HEAD"))
    private void handleFastSwim(CallbackInfo ci) {
        if (isInsideWater() && ((ICustomMovementEntity)this).isPose(EnumPose.CRAWLING)) {
            if (this.canSwim()) {
                if (this.moveForward > 0) {
                    Vec3 direction = this.getLookVec();
                    float speed = 0.2f * this.getMovementSpeedModifierFromEffects();

                    super.moveEntity(direction.xCoord * speed, direction.yCoord * speed + this.motionY, direction.zCoord * speed);
                    this.addMovementStat(direction.xCoord * speed, direction.yCoord * speed + this.motionY, direction.zCoord * speed);

                    this.motionY *= 0.5;
                    this.motionY = this.motionY < 0 ? 0 : this.motionY - 0.02;

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
            } else {
                ((ICustomMovementEntity) this).letMeMove_$setCustomMovementState(EnumPose.STANDING);
            }
        }
    }

    @Unique
    private boolean isInsideWater() {
        World world = this.worldObj;
        return ((ICustomMovementEntity)this).isPose(EnumPose.CRAWLING) ? world.isMaterialInBB(this.boundingBox.expand(0,-0.4,0),Material.water) : this.isInsideOfMaterial(Material.water);
    }

    @Redirect(method = "addMovementStat",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;isInsideOfMaterial(Lnet/minecraft/src/Material;)Z"))
    private boolean addNewSwimExhaustion(EntityPlayer player, Material material, double par1, double par3, double par5) {
        if (player.isInsideOfMaterial(material)) {
            if (((ICustomMovementEntity)player).isPose(EnumPose.CRAWLING)) {
                int var7 = Math.round(MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5) * 100.0f);
                if (var7 > 0) {
                    this.addStat(StatList.distanceDoveStat, var7);
                    this.addExhaustion(0.15f * (float)var7 * 0.01f);
                }
                return false;
            }
            return true;
        }
        return false;
    }

    @Inject(method = "moveEntityWithHeading",at = @At("HEAD"),cancellable = true)
    private void disableMoveIfFastSwimming(float par1, float par2, CallbackInfo ci) {
        if (isInsideWater() && ((ICustomMovementEntity)this).isPose(EnumPose.CRAWLING) && this.moveForward > 0 && this.canSwim()){
            ci.cancel();
        }
    }

    @Unique
    public boolean isEntityFeetInsideOpaqueBlock() {
        for (int count = 0; count < 8; ++count) {
            float i = ((float)((count >> 0) % 2) - 0.5f) * this.width * 0.8f;
            float j = ((float)((count >> 1) % 2) - 0.5f) * 0.1f;
            float k = ((float)((count >> 2) % 2) - 0.5f) * this.width * 0.8f;
            int x = MathHelper.floor_double(this.posX + (double)i);
            int feetY = MathHelper.floor_double(this.posY + 0.5 + (double)j);
            int z = MathHelper.floor_double(this.posZ + (double)k);
            if (!this.worldObj.canBlockSuffocateEntity(x, feetY, z)) continue;
            return true;
        }
        return false;
    }
}
