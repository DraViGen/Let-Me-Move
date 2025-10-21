package net.dravigen.letMeMove.animation;

import net.dravigen.letMeMove.animation.customs.*;

import static net.dravigen.letMeMove.utils.AnimationUtils.*;

public class AnimRegistry {
	public final static BaseAnimation STANDING = new AnimStanding();
	public final static BaseAnimation CROUCHING = new AnimCrouching();
	public final static BaseAnimation SWIMMING = new AnimSwimming();
	public final static BaseAnimation DIVING = new AnimDiving();
	public final static BaseAnimation HIGH_FALLING = new AnimHighFalling();
	public final static BaseAnimation LOW_FALLING = new AnimLowFalling();
	public final static BaseAnimation SKY_DIVING = new AnimSkyDiving();
	public final static BaseAnimation DASHING = new AnimDashing();
	public final static BaseAnimation ROLLING = new AnimRolling();
	public final static BaseAnimation WALL_SLIDING = new AnimWallSliding();
	public final static BaseAnimation PULLING_UP = new AnimPullingUp();
	
	
	/**
	 * Register the animations here, the higher the animation the higher the priority (will be checked first)
	 */
	public static void registerAllAnimation() {
		registerAnimation(STANDING);
		
		registerAnimation(SWIMMING);
		
		registerAnimation(DASHING);
		
		registerAnimation(ROLLING);
		
		registerAnimation(PULLING_UP);
		
		registerAnimation(WALL_SLIDING);
		
		registerAnimation(CROUCHING);
		
		registerAnimation(DIVING);
		
		registerAnimation(SKY_DIVING);
		
		registerAnimation(LOW_FALLING);
		
		registerAnimation(HIGH_FALLING);
	}
}
