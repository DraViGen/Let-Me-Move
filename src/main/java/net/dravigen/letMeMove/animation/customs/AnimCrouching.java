package net.dravigen.letMeMove.animation.customs;

import net.minecraft.src.*;


public class AnimCrouching extends AnimCommon {
	public static final ResourceLocation id = new ResourceLocation("LMM", "crouching");
	
	public AnimCrouching() {
		super(id, 1.4f, 0.3f);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return player.onGround || player.fallDistance < 2;
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return player.isSneaking();
	}
}
