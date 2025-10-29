package net.dravigen.let_me_move.animation;

import net.dravigen.let_me_move.animation.actions.*;
import net.dravigen.let_me_move.animation.poses.*;

import static net.dravigen.let_me_move.utils.AnimationUtils.*;

public class AnimRegistry {
	public final static BaseAnimation STANDING = new AnimStanding();
	public final static BaseAnimation JUMPING = new AnimJumping();
	public final static BaseAnimation FLYING = new AnimFlying();
	public final static BaseAnimation CLIMBING = new AnimClimbing();
	public final static BaseAnimation SWIMMING = new AnimSwimming();
	public final static BaseAnimation DIVING = new AnimDiving();
	public final static BaseAnimation HIGH_FALLING = new AnimHighFalling();
	public final static BaseAnimation LOW_FALLING = new AnimLowFalling();
	public final static BaseAnimation SKY_DIVING = new AnimSkyDiving();
	public final static BaseAnimation DASHING = new AnimDashing();
	public final static BaseAnimation ROLLING = new AnimRolling();
	public final static BaseAnimation WALL_SLIDING = new AnimWallSliding();
	public final static BaseAnimation PULLING_UP = new AnimPullingUp();
	public final static BaseAnimation CROUCHING = new AnimCrouching();
	
	
	/**
	 * Register the animations here, the higher the animation the higher the priority (will be checked first)
	 */
	public static void registerAllAnimation() {
		registerAnimation(STANDING);
		
		registerAnimation(FLYING);
		
		registerAnimation(CLIMBING);
		
		registerAnimation(SWIMMING);
		
		registerAnimation(DASHING);
		
		registerAnimation(ROLLING);
		
		registerAnimation(PULLING_UP);
		
		registerAnimation(WALL_SLIDING);
		
		registerAnimation(CROUCHING);
		
		registerAnimation(DIVING);
		
		registerAnimation(JUMPING);
		
		registerAnimation(LOW_FALLING);
		
		registerAnimation(SKY_DIVING);
		
		registerAnimation(HIGH_FALLING);
	}
}
