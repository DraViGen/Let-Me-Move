package net.dravigen.letMeMove.mixin.render;

import net.dravigen.letMeMove.EnumPose;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.ModernUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public abstract class ModelBipedMixin extends ModelBase {

    @Shadow public ModelRenderer bipedRightArm;

    @Shadow public ModelRenderer bipedLeftArm;

    @Shadow public ModelRenderer bipedRightLeg;

    @Shadow public ModelRenderer bipedLeftLeg;

    @Shadow public ModelRenderer bipedBody;

    @Shadow public ModelRenderer bipedHead;

    @Shadow public ModelRenderer bipedCloak;
    @Shadow public ModelRenderer bipedHeadwear;
    @Shadow public boolean aimedBow;

    @Shadow public int heldItemRight;

    @Shadow public int heldItemLeft;

    @Inject(method = "render",at = @At("HEAD"))
    private void a(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7, CallbackInfo ci) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) entity;
        float leaningPitch = customMoveEntity.letMeMove_$getLeaningPitch();
        if (((ICustomMovementEntity) entity).isPose(EnumPose.CRAWLING)) {
            float var1 = 1.62f - (1.62f - 1.25f) * leaningPitch;
            GL11.glTranslatef(0, var1, 0);
            GL11.glRotatef(90 * leaningPitch, 1, 0, 0);
        }
    }

    @Inject(method = "setRotationAngles",at = @At("HEAD"),cancellable = true)
    public void setAngles(float f, float g, float h, float i, float j, float u, Entity livingEntity, CallbackInfo ci) {
        ICustomMovementEntity customMoveEntity = (ICustomMovementEntity) livingEntity;

        ci.cancel();

        boolean bl2 = customMoveEntity.isPose(EnumPose.CRAWLING);
        float leaningPitch =  Math.min(1.0F,customMoveEntity.letMeMove_$getLeaningPitch());

        leaningPitch = livingEntity.inWater && bl2 ? 1 : leaningPitch;

        this.bipedHead.rotateAngleY = i * (float) (Math.PI / 180.0);
        if (leaningPitch > 0.0F) {
            if (bl2) {
                this.bipedHead.rotateAngleX = this.lerpAngle(leaningPitch, this.bipedHead.rotateAngleX, (float) (-Math.PI / 4));
            } else {
                this.bipedHead.rotateAngleX = this.lerpAngle(leaningPitch, this.bipedHead.rotateAngleX, j * (float) (Math.PI / 180.0));
            }
        } else {
            this.bipedHead.rotateAngleX = j * (float) (Math.PI / 180.0);
        }
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;

        this.bipedBody.rotateAngleY = 0.0F;

        this.bipedRightArm.rotationPointZ = 0.0F;
        this.bipedRightArm.rotationPointX = -5.0F;

        this.bipedLeftArm.rotationPointZ = 0.0F;
        this.bipedLeftArm.rotationPointX = 5.0F;

        float k = 1.0F;

        this.bipedRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / k;
        this.bipedLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;

        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;

        if (!customMoveEntity.isPose(EnumPose.CRAWLING) || customMoveEntity.isPose(EnumPose.CRAWLING) && livingEntity.inWater) {
            this.bipedRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * g / k;
            this.bipedLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / k;
        } else {
            this.bipedRightLeg.rotateAngleX = 0;
            this.bipedLeftLeg.rotateAngleX = 0;
        }

        this.bipedRightLeg.rotateAngleY = 0.0F;
        this.bipedLeftLeg.rotateAngleY = 0.0F;

        this.bipedRightLeg.rotateAngleZ = 0.0F;
        this.bipedLeftLeg.rotateAngleZ = 0.0F;

        if (this.isRiding) {
            this.bipedRightArm.rotateAngleX += (float) (-Math.PI / 5);
            this.bipedLeftArm.rotateAngleX += (float) (-Math.PI / 5);

            this.bipedRightLeg.rotateAngleX = -1.4137167F;
            this.bipedRightLeg.rotateAngleY = (float) (Math.PI / 10);

            this.bipedRightLeg.rotateAngleZ = 0.07853982F;
            this.bipedLeftLeg.rotateAngleX = -1.4137167F;

            this.bipedLeftLeg.rotateAngleY = (float) (-Math.PI / 10);
            this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
        }
        if (this.heldItemLeft != 0) {
            this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5f - 0.31415927f * (float)this.heldItemLeft;
        }
        if (this.heldItemRight != 0) {
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5f - 0.31415927f * (float)this.heldItemRight;
        }

        this.bipedRightArm.rotateAngleY = 0.0F;
        this.bipedLeftArm.rotateAngleY = 0.0F;

        if(this.aimedBow){
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
            this.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = (float) (-Math.PI / 2) + this.bipedHead.rotateAngleX;
        }

        this.handSwinging((EntityLivingBase) livingEntity, h);

        if (customMoveEntity.isPose(EnumPose.SNEAKING)) {
            this.bipedBody.rotateAngleX = 0.5F;

            this.bipedRightArm.rotateAngleX += 0.4F;
            this.bipedLeftArm.rotateAngleX += 0.4F;

            this.bipedRightLeg.rotationPointZ = 4.0F;
            this.bipedLeftLeg.rotationPointZ = 4.0F;

            this.bipedRightLeg.rotationPointY = 12.2F;
            this.bipedLeftLeg.rotationPointY = 12.2F;

            this.bipedHead.rotationPointY = 4.2F;
            this.bipedHeadwear.rotationPointY = 4.2f;

            this.bipedBody.rotationPointY = 3.2F;

            this.bipedLeftArm.rotationPointY = 5.2F;
            this.bipedRightArm.rotationPointY = 5.2F;
        } else {
            this.bipedBody.rotateAngleX = 0.0F;

            this.bipedRightLeg.rotationPointZ = 0.1F;
            this.bipedLeftLeg.rotationPointZ = 0.1F;

            this.bipedRightLeg.rotationPointY = 12.0F;
            this.bipedLeftLeg.rotationPointY = 12.0F;

            this.bipedHead.rotationPointY = 0.0F;
            this.bipedHeadwear.rotationPointY = 0.0f;

            this.bipedBody.rotationPointY = 0.0F;

            this.bipedLeftArm.rotationPointY = 2.0F;
            this.bipedRightArm.rotationPointY = 2.0F;
        }

        if (leaningPitch > 0.0F) {
            float l = f % 26.0F;
            float m = this.onGround > 0.0F ? 0.0F : leaningPitch;
            float n = leaningPitch;
            if (l < 14.0F) {
                this.bipedLeftArm.rotateAngleX = this.lerpAngle(n, this.bipedLeftArm.rotateAngleX, 0.0F);
                this.bipedRightArm.rotateAngleX = ModernUtils.lerp(m, this.bipedRightArm.rotateAngleX, 0.0F);

                this.bipedLeftArm.rotateAngleY = this.lerpAngle(n, this.bipedLeftArm.rotateAngleY, (float) Math.PI);
                this.bipedRightArm.rotateAngleY = ModernUtils.lerp(m, this.bipedRightArm.rotateAngleY, (float) Math.PI);

                this.bipedLeftArm.rotateAngleZ = this.lerpAngle(n, this.bipedLeftArm.rotateAngleZ, (float) Math.PI + 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
                this.bipedRightArm.rotateAngleZ = ModernUtils.lerp(m, this.bipedRightArm.rotateAngleZ, (float) Math.PI - 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
            } else if (l >= 14.0F && l < 22.0F) {
                float o = (l - 14.0F) / 8.0F;
                this.bipedLeftArm.rotateAngleX = this.lerpAngle(n, this.bipedLeftArm.rotateAngleX, (float) (Math.PI / 2) * o);
                this.bipedRightArm.rotateAngleX = ModernUtils.lerp(m, this.bipedRightArm.rotateAngleX, (float) (Math.PI / 2) * o);

                this.bipedLeftArm.rotateAngleY = this.lerpAngle(n, this.bipedLeftArm.rotateAngleY, (float) Math.PI);
                this.bipedRightArm.rotateAngleY = ModernUtils.lerp(m, this.bipedRightArm.rotateAngleY, (float) Math.PI);

                this.bipedLeftArm.rotateAngleZ = this.lerpAngle(n, this.bipedLeftArm.rotateAngleZ, 5.012389F - 1.8707964F * o);
                this.bipedRightArm.rotateAngleZ = ModernUtils.lerp(m, this.bipedRightArm.rotateAngleZ, 1.2707963F + 1.8707964F * o);
            } else if (l >= 22.0F && l < 26.0F) {
                float o = (l - 22.0F) / 4.0F;
                this.bipedLeftArm.rotateAngleX = this.lerpAngle(n, this.bipedLeftArm.rotateAngleX, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
                this.bipedRightArm.rotateAngleX = ModernUtils.lerp(m, this.bipedRightArm.rotateAngleX, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);

                this.bipedLeftArm.rotateAngleY = this.lerpAngle(n, this.bipedLeftArm.rotateAngleY, (float) Math.PI);
                this.bipedRightArm.rotateAngleY = ModernUtils.lerp(m, this.bipedRightArm.rotateAngleY, (float) Math.PI);

                this.bipedLeftArm.rotateAngleZ = this.lerpAngle(n, this.bipedLeftArm.rotateAngleZ, (float) Math.PI);
                this.bipedRightArm.rotateAngleZ = ModernUtils.lerp(m, this.bipedRightArm.rotateAngleZ, (float) Math.PI);
            }

            this.bipedLeftLeg.rotateAngleX = ModernUtils.lerp(leaningPitch, this.bipedLeftLeg.rotateAngleX, 0.3F * MathHelper.cos(f * 0.33333334F + (float) Math.PI));
            this.bipedRightLeg.rotateAngleX = ModernUtils.lerp(leaningPitch, this.bipedRightLeg.rotateAngleX, 0.3F * MathHelper.cos(f * 0.33333334F));

        }

        if (livingEntity instanceof EntityPlayer) {
            if (((EntityPlayer) livingEntity).getCurrentItemOrArmor(2) == null) {
                if (customMoveEntity.isPose(EnumPose.SNEAKING)) {
                    this.bipedCloak.rotationPointZ = 1.4F;
                    this.bipedCloak.rotationPointY = 1.85F;
                } else {
                    this.bipedCloak.rotationPointZ = 0.0F;
                    this.bipedCloak.rotationPointY = 0.0F;
                }
            } else if (customMoveEntity.isPose(EnumPose.SNEAKING)) {
                this.bipedCloak.rotationPointZ = 0.3F;
                this.bipedCloak.rotationPointY = 0.8F;
            } else {
                this.bipedCloak.rotationPointZ = -1.1F;
                this.bipedCloak.rotationPointY = -0.85F;
            }
        }
    }

    @Unique
    protected float lerpAngle(float angleOne, float angleTwo, float magnitude) {
        float f = (magnitude - angleTwo) % (float) (Math.PI * 2);
        if (f < (float) -Math.PI) {
            f += (float) (Math.PI * 2);
        }

        if (f >= (float) Math.PI) {
            f -= (float) (Math.PI * 2);
        }

        return angleTwo + angleOne * f;
    }

    @Unique
    private float method_2807(float f) {
        return -65.0F * f + f * f;
    }

    @Unique
    protected void handSwinging(EntityLivingBase livingEntity, float f) {
        if (!(this.onGround <= 0.0F)) {
            float g = this.onGround;
            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(g) * (float) (Math.PI * 2)) * 0.2F;

            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;

            this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;

            this.bipedRightArm.rotateAngleY = this.bipedRightArm.rotateAngleY + this.bipedBody.rotateAngleY;

            this.bipedLeftArm.rotateAngleY = this.bipedLeftArm.rotateAngleY + this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX + this.bipedBody.rotateAngleY;
            g = 1.0F - this.onGround;
            g *= g;
            g *= g;
            g = 1.0F - g;
            float h = MathHelper.sin(g * (float) Math.PI);
            float i = MathHelper.sin(this.onGround * (float) Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;

            this.bipedRightArm.rotateAngleX = (float)(this.bipedRightArm.rotateAngleX - (h * 1.2 + i));
            this.bipedRightArm.rotateAngleY = this.bipedRightArm.rotateAngleY + this.bipedBody.rotateAngleY * 2.0F;
            this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + MathHelper.sin(this.onGround * (float) Math.PI) * -0.4F;
        }
    }
}
