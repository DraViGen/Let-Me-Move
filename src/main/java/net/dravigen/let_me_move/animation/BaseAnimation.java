package net.dravigen.let_me_move.animation;

import btw.world.util.difficulty.DifficultyParam;
import net.minecraft.src.*;

public abstract class BaseAnimation {
	
	public final float speedModifier;
	public final boolean needYOffsetUpdate;
	public final int maxCooldown;
	public final int totalDuration;
	public final boolean shouldAutoUpdate;
	private final ResourceLocation id;
	public float height;
	public int cooldown = 0;
	public float yOffset;
	public int timeRendered;
	public int priority;
	protected boolean movedOnce;
	
	public BaseAnimation(ResourceLocation id, float height, float speedModifier, boolean needYOffsetUpdate,
			int maxCooldown, int totalDuration, boolean shouldAutoUpdate, float yOffset) {
		this.id = id;
		this.height = height;
		this.speedModifier = speedModifier;
		this.needYOffsetUpdate = needYOffsetUpdate;
		this.maxCooldown = maxCooldown;
		this.totalDuration = totalDuration;
		this.shouldAutoUpdate = shouldAutoUpdate;
		this.yOffset = yOffset;
		this.priority = 0;
	}
	/*
	public BaseAnimation(ResourceLocation id, float height, float speedModifier, boolean needYOffsetUpdate,
			int maxCooldown, int duration, boolean shouldAutoUpdate) {
		this.id = id;
		this.height = height;
		this.speedModifier = speedModifier;
		this.needYOffsetUpdate = needYOffsetUpdate;
		this.maxCooldown = maxCooldown;
		this.duration = duration;
		this.shouldAutoUpdate = shouldAutoUpdate;
		this.yOffset = 0;
		this.priority = 0;
	}
	
	public BaseAnimation(ResourceLocation id, float height, float speedModifier, boolean needYOffsetUpdate,
			int maxCooldown, int duration) {
		this.id = id;
		this.height = height;
		this.speedModifier = speedModifier;
		this.needYOffsetUpdate = needYOffsetUpdate;
		this.maxCooldown = maxCooldown;
		this.duration = duration;
		this.shouldAutoUpdate = true;
		this.yOffset = 0;
		this.priority = 0;
	}
	
	public BaseAnimation(ResourceLocation id, float height, float speedModifier, boolean needYOffsetUpdate,
			int maxCooldown) {
		this.id = id;
		this.height = height;
		this.speedModifier = speedModifier;
		this.needYOffsetUpdate = needYOffsetUpdate;
		this.maxCooldown = maxCooldown;
		this.duration = 0;
		this.shouldAutoUpdate = true;
		this.yOffset = 0;
		this.priority = 0;
	}
	
	public BaseAnimation(ResourceLocation id, float height, float speedModifier, boolean needYOffsetUpdate) {
		this.id = id;
		this.height = height;
		this.speedModifier = speedModifier;
		this.needYOffsetUpdate = needYOffsetUpdate;
		this.maxCooldown = 0;
		this.duration = 0;
		this.shouldAutoUpdate = true;
		this.yOffset = 0;
		this.priority = 0;
	}
	
	public BaseAnimation(ResourceLocation id, float height, float speedModifier) {
		this.id = id;
		this.height = height;
		this.speedModifier = speedModifier;
		this.needYOffsetUpdate = false;
		this.maxCooldown = 0;
		this.duration = 0;
		this.shouldAutoUpdate = true;
		this.yOffset = 0;
		this.priority = 0;
	}*/
	
	public ResourceLocation getID() {
		return this.id;
	}
	
	public boolean hasCooldown() {
		return this.maxCooldown != 0;
	}
	
	public void startCooldown() {
		this.cooldown = this.maxCooldown;
	}
	
	public void updateAnimationTime(ResourceLocation currentAnimaiton) {
		if (currentAnimaiton.equals(this.id)) {
			if (this.cooldown == 0) {
				if (this.timeRendered < this.totalDuration) {
					if (this.shouldAutoUpdate) {
						this.timeRendered++;
					}
				}
				else {
					this.startCooldown();
				}
			}
		}
		else {
			timeRendered = 0;
		}
		
		if (this.cooldown > 0) {
			this.cooldown--;
		}
	}
	
	public boolean shouldActivateAnimation(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		if (!this.hasCooldown() || this.cooldown == 0) {
			return this.isActivationConditonsMet(player, axisAlignedBB);
		}
		else return false;
	}

	public abstract boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB);
	
	public abstract boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB);
	
	protected float getHungerDifficultyMultiplier(EntityPlayer player) {
		return ((Float)player.worldObj.getDifficultyParameter(DifficultyParam.HungerIntensiveActionCostMultiplier.class)).floatValue();
	}
	
	public abstract void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i,
			float j, float u, float delta);
	
	public abstract void updateLeaning(EntityLivingBase entity);
	
	public abstract boolean getHungerCost(EntityPlayer player, double distX, double distY, double distZ);

	public abstract boolean getCustomMove(EntityPlayer player);
}
