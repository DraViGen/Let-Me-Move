package net.dravigen.letMeMove.mixin.server;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.packet.PacketUtils;
import net.dravigen.letMeMove.animation.AnimationRegistry;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetServerHandler.class)
public abstract class NetServerHandlerMixin extends NetHandler {
	
	@Shadow
	public EntityPlayerMP playerEntity;
	
	@Inject(method = "handleCustomPayload", at = @At("HEAD"))
	private void tu_onCustomPayloadC2S(Packet250CustomPayload packet, CallbackInfo ci) {
		if (packet.channel.equals(PacketUtils.ANIMATION_SYNC_CHANNEL)) {
			PacketUtils.handleAnimationSync(packet, this.playerEntity);
		}
		else if (packet.channel.equals(PacketUtils.HUNGER_EXHAUSTION_CHANNEL)) {
			PacketUtils.handleExhaustionFromClient(packet, this.playerEntity);
		}
	}
	
	@Redirect(method = "handleFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;isPlayerSleeping()Z", ordinal = 1))
	private boolean preventIllegalStance(EntityPlayerMP instance) {
		return true;
	}
	
	@Redirect(method = "handleFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;addExhaustionForJump()V"))
	private void a(EntityPlayerMP instance) {
		if (!((ICustomMovementEntity) instance).llm_$isAnimation(AnimationRegistry.SWIMMING_ID)) {
			instance.addExhaustionForJump();
		}
	}
}
