package net.dravigen.let_me_move.animation.poses;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ResourceLocation;

public class AnimWalking extends AnimCommon {
	public static final ResourceLocation id = new ResourceLocation("LMM", "walking");
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return (player.moveForward != 0 || player.moveStrafing != 0) && !player.isSprinting() && !player.isSneaking() && player.onGround;
	}
	
	public AnimWalking() {
		super(id);
	}
}
