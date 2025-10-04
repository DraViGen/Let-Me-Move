package net.dravigen.letMeMove.interfaces.functional;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityPlayer;

@FunctionalInterface
public interface IAnimationCondition {
    boolean isConditionsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB);
}
