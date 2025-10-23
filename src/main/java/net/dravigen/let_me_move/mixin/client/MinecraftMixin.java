package net.dravigen.let_me_move.mixin.client;

import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.dravigen.let_me_move.utils.GeneralUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.let_me_move.animation.AnimRegistry.ROLLING;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	
	@Shadow
	public WorldClient theWorld;
	@Shadow
	public EntityClientPlayerMP thePlayer;
	@Unique
	long prevTime;
	@Shadow
	private boolean isGamePaused;
	@Unique
	private static float prevPitch;
	
	@Shadow public GameSettings gameSettings;
	
	@Inject(method = "runGameLoop", at = @At("HEAD"))
	private void updateRender(CallbackInfo ci) {
		EntityPlayer player = this.thePlayer;
		ICustomMovementEntity customPlayer = (ICustomMovementEntity) player;
		
		if (!this.isGamePaused && this.theWorld != null && player != null) {
			float delta = (System.currentTimeMillis() - prevTime) / 25f;
			
			customPlayer.llm_$getAnimation().updateLeaning(player);
			
			player.yOffset = GeneralUtils.incrementUntilGoal(player.yOffset, player.height - 0.18f, 0.4f * delta);
			
			if (customPlayer.llm_$isAnimation(ROLLING.getID()) && this.gameSettings.thirdPersonView == 0) {
				float leaning = customPlayer.llm_$getLeaningPitch();
				
				if (leaning == 0) {
					prevPitch = player.cameraPitch;
				}
				else if (leaning < 4) {
					player.cameraPitch = (prevPitch + leaning * 90) % 360;
				}
				else {
					player.cameraPitch = 0;
					player.prevCameraPitch = player.cameraPitch;
				}
			}
			
			float yaw = (player.renderYawOffset) % (360);
			
			yaw = yaw < 0 ? 360 + yaw : yaw;
			
			player.renderYawOffset = yaw;
		}
		
		prevTime = System.currentTimeMillis();
	}
}
