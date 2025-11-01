package net.dravigen.let_me_move.interfaces;

import net.dravigen.let_me_move.animation.BaseAnimation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.ResourceLocation;

public interface ICustomMovementEntity {
	@Environment(EnvType.CLIENT)
	float llm_$getLeaningPitch();
	
	void llm_$setLeaningPitch(float pitch);
	
	ResourceLocation llm_$getAnimationID();
	
	BaseAnimation llm_$getAnimation();
	
	void llm_$setAnimation(ResourceLocation animation);
	
	boolean llm_$isAnimation(ResourceLocation id);
	
	side llm_$getSide();
	
	float llm_$getSideValue();
	
	void llm_$setSide(side side);
	
	void llm_$setDelta(float deltaRender);
	
	float llm_$getDelta();
	
	enum side {
		LEFT,
		RIGHT
	}
}
