package net.dravigen.letMeMove.utils;

import net.minecraft.src.*;

public class GeneralUtils {
    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static boolean isInsideWater(Entity entity) {
        World world = entity.worldObj;
        AxisAlignedBB bb = entity.boundingBox.copy();
        bb.offset(0, 0.2, 0);
        int minY = MathHelper.floor_double(bb.minY + 0.2);
        return world.getBlockMaterial(MathHelper.floor_double(entity.posX), minY, MathHelper.floor_double(entity.posZ)) == Material.water;
    }

    public static boolean isHeadInsideWater(Entity entity) {
        World world = entity.worldObj;
        AxisAlignedBB bb = entity.boundingBox.copy();
        int eye = MathHelper.floor_double(bb.maxY - 0.3);
        return world.getBlockMaterial(MathHelper.floor_double(entity.posX), eye, MathHelper.floor_double(entity.posZ)) == Material.water;
    }

    public static boolean isEntityFeetInsideOpaqueBlock(Entity entity) {
        for (int count = 0; count < 8; ++count) {
            float i = ((float) ((count >> 0) % 2) - 0.5f) * entity.width * 0.8f;
            float j = ((float) ((count >> 1) % 2) - 0.5f) * 0.1f;
            float k = ((float) ((count >> 2) % 2) - 0.5f) * entity.width * 0.8f;
            int x = MathHelper.floor_double(entity.posX + (double) i);
            int feetY = MathHelper.floor_double(entity.posY + 0.5 + (double) j);
            int z = MathHelper.floor_double(entity.posZ + (double) k);
            if (!entity.worldObj.canBlockSuffocateEntity(x, feetY, z)) continue;
            return true;
        }
        return false;
    }

    public static float lerpAngle(float angleOne, float angleTwo, float magnitude) {
        float f = (magnitude - angleTwo) % (float) (Math.PI * 2);
        if (f < (float) -Math.PI) {
            f += (float) (Math.PI * 2);
        }

        if (f >= (float) Math.PI) {
            f -= (float) (Math.PI * 2);
        }

        return angleTwo + angleOne * f;
    }

    public static float method_2807(float f) {
        return -65.0F * f + f * f;
    }


    public static void handSwinging(EntityLivingBase livingEntity, ModelBiped model, float f) {
        if (!(model.onGround <= 0.0F)) {
            float g = model.onGround;
            model.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(g) * (float) (Math.PI * 2)) * 0.2F;

            model.bipedRightArm.rotationPointZ = MathHelper.sin(model.bipedBody.rotateAngleY) * 5.0F;
            model.bipedRightArm.rotationPointX = -MathHelper.cos(model.bipedBody.rotateAngleY) * 5.0F;

            model.bipedLeftArm.rotationPointZ = -MathHelper.sin(model.bipedBody.rotateAngleY) * 5.0F;
            model.bipedLeftArm.rotationPointX = MathHelper.cos(model.bipedBody.rotateAngleY) * 5.0F;

            model.bipedRightArm.rotateAngleY = model.bipedRightArm.rotateAngleY + model.bipedBody.rotateAngleY;

            model.bipedLeftArm.rotateAngleY = model.bipedLeftArm.rotateAngleY + model.bipedBody.rotateAngleY;
            model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX + model.bipedBody.rotateAngleY;
            g = 1.0F - model.onGround;
            g *= g;
            g *= g;
            g = 1.0F - g;
            float h = MathHelper.sin(g * (float) Math.PI);
            float i = MathHelper.sin(model.onGround * (float) Math.PI) * -(model.bipedHead.rotateAngleX - 0.7F) * 0.75F;

            model.bipedRightArm.rotateAngleX = (float) (model.bipedRightArm.rotateAngleX - (h * 1.2 + i));
            model.bipedRightArm.rotateAngleY = model.bipedRightArm.rotateAngleY + model.bipedBody.rotateAngleY * 2.0F;
            model.bipedRightArm.rotateAngleZ = model.bipedRightArm.rotateAngleZ + MathHelper.sin(model.onGround * (float) Math.PI) * -0.4F;
        }
    }
}
