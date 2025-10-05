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

    public static float incrementUntilGoal(float currentValue, float goalValue, float easeFactor) {

        float difference = goalValue - currentValue;

        float stepSize = difference * easeFactor;

        return currentValue + stepSize;
    }

    public static float incrementAngleUntilGoal(float currentValue, float goalValue, float easeFactor) {

        float difference = goalValue - currentValue;

        difference = difference % 360.0F;

        if (difference > 180.0F) {
            difference -= 360.0F;
        }
        else if (difference < -180.0F) {
            difference += 360.0F;
        }

        float stepSize = difference * easeFactor;

        float newValue = currentValue + stepSize;

        newValue = newValue % 360.0F;
        if (newValue < 0) {
            newValue += 360.0F;
        }

        return newValue;
    }
}
