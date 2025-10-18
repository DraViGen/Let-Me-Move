package net.dravigen.letMeMove.interfaces.functional;

import net.minecraft.src.EntityLivingBase;

@FunctionalInterface
public interface IAnimationLeaning {
	void updateLeaningPitch(EntityLivingBase entity);
}
