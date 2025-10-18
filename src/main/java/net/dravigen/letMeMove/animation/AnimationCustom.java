package net.dravigen.letMeMove.animation;

import net.dravigen.letMeMove.interfaces.functional.IAnimationCondition;
import net.dravigen.letMeMove.interfaces.functional.IAnimationLeaning;
import net.dravigen.letMeMove.interfaces.functional.IAnimationRender;
import net.minecraft.src.*;

public class AnimationCustom {
	
	public final float speedModifier;
	public final boolean needYOffsetUpdate;
	public final int maxCooldown;
	private final ResourceLocation animationIdentifier;
	public float height;
	public int cooldown = 0;
	public float yOffset;
	public int duration;
	public int timeRendered;
	private IAnimationRender animationRender;
	private IAnimationCondition activationConditions;
	private IAnimationCondition generalConditions;
	private IAnimationLeaning leaningUpdate;
	
	public AnimationCustom(ResourceLocation animationIdentifier, float height, float speedModifier,
			boolean needYOffsetUpdate, int maxCooldown, int duration, float yOffset) {
		this.animationIdentifier = animationIdentifier;
		this.height = height;
		this.speedModifier = speedModifier;
		this.needYOffsetUpdate = needYOffsetUpdate;
		this.maxCooldown = maxCooldown;
		this.duration = duration;
		this.yOffset = yOffset;
	}
	
	public ResourceLocation getID() {
		return this.animationIdentifier;
	}
	
	public void setActivationConditions(IAnimationCondition animationCondition) {
		this.activationConditions = animationCondition;
	}
	
	public void setGeneralConditions(IAnimationCondition animationCondition) {
		this.generalConditions = animationCondition;
	}
	
	public void setAnimationRender(IAnimationRender render) {
		this.animationRender = render;
	}
	
	public void setLeaningUpdate(IAnimationLeaning leaningUpdate) {
		this.leaningUpdate = leaningUpdate;
	}
	
	public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		if (!this.hasCooldown() || this.cooldown == 0) {
			return this.activationConditions.isConditionsMet(player, axisAlignedBB);
		}
		else return false;
	}
	
	public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
		return this.generalConditions.isConditionsMet(player, axisAlignedBB);
	}
	
	public void renderAnimation(ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j,
			float u, float delta) {
		this.animationRender.animate(model, entity, f, g, h, i, j, u, delta);
	}
	
	public void updateLeaning(EntityLivingBase entity) {
		this.leaningUpdate.updateLeaningPitch(entity);
	}
	
	public boolean hasCooldown() {
		return this.maxCooldown != 0;
	}
	
	public void startCooldown() {
		this.cooldown = this.maxCooldown;
	}
	
	public void updateAnimationTime(ResourceLocation currentAnimaiton) {
		if (currentAnimaiton.equals(this.animationIdentifier)) {
			if (this.cooldown == 0) {
				if (this.timeRendered < this.duration) {
					this.timeRendered++;
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
	
	public void registerAnimation(IAnimationCondition generalCondition, IAnimationCondition activationCondition,
			IAnimationRender render, IAnimationLeaning leaningUpdate) {
		this.setGeneralConditions(generalCondition);
		this.setActivationConditions(activationCondition);
		this.setAnimationRender(render);
		this.setLeaningUpdate(leaningUpdate);
	}
}
