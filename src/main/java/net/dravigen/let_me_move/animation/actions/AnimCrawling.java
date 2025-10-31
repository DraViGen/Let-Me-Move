package net.dravigen.let_me_move.animation.actions;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ResourceLocation;

import static net.dravigen.let_me_move.LetMeMoveAddon.crawl_key;
import static net.dravigen.let_me_move.utils.GeneralUtils.isInsideWater;

public class AnimCrawling extends AnimSwimming{
	public static final ResourceLocation id = new ResourceLocation("LMM", "crawling");
	
	public AnimCrawling() {
		super(id);
	}
	
	@Override
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return !player.capabilities.isFlying && player.onGround && !isInsideWater(player);
	}
	
	@Override
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return crawl_key.pressed;
	}
}
