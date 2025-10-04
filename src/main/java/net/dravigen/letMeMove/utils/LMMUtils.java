package net.dravigen.letMeMove.utils;

import net.minecraft.src.*;

public class LMMUtils {
    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static boolean isInsideWater(Entity entity) {
        World world = entity.worldObj;
        AxisAlignedBB bb = entity.boundingBox.copy();
        bb.offset(0,0.2,0);
        int minY = MathHelper.floor_double(bb.minY + 0.2);
        return world.getBlockMaterial(MathHelper.floor_double(entity.posX), minY, MathHelper.floor_double(entity.posZ)) == Material.water;
    }

    public static boolean isHeadInsideWater(Entity entity) {
        World world = entity.worldObj;
        AxisAlignedBB bb = entity.boundingBox.copy();
        int eye = MathHelper.floor_double(bb.maxY - 0.3);
        return world.getBlockMaterial(MathHelper.floor_double(entity.posX), eye, MathHelper.floor_double(entity.posZ)) == Material.water;
    }
}
