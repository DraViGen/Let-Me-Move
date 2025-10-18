package net.dravigen.letMeMove.interfaces;

import net.dravigen.letMeMove.animation.AnimationCustom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.ResourceLocation;

public interface ICustomMovementEntity {
	@Environment(EnvType.CLIENT)
	float llm_$getLeaningPitch();
	
	void llm_$setLeaningPitch(float pitch);
	
	ResourceLocation llm_$getAnimationID();
	
	AnimationCustom llm_$getAnimation();
	
	void llm_$setAnimation(ResourceLocation animation);
	
	boolean llm_$isAnimation(ResourceLocation animationID);
	
	side llm_$getSide();
	
	float llm_$getSideValue();
	
	void llm_$setSide(side side);
	
	enum side {
		LEFT, RIGHT
	}
}
