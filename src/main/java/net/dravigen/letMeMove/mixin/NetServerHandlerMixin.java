package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.packet.PacketUtils;
import net.dravigen.letMeMove.render.AnimationRegistry;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetServerHandler.class)
public abstract class NetServerHandlerMixin extends NetHandler {

    @Shadow public EntityPlayerMP playerEntity;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    private void tu_onCustomPayloadC2S(Packet250CustomPayload packet, CallbackInfo ci) {
        if (packet.channel.equals(PacketUtils.ANIMATION_SYNC_CHANNEL)) {
            PacketUtils.handleAnimationSync(packet, this.playerEntity);
        }
    }

    @ModifyConstant(method = "handleFlying", constant = @Constant(doubleValue = 1.65))
    private double preventIllegalStance(double constant) {
        return 2;
    }

    @Redirect(method = "handleFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;addExhaustionForJump()V"))
    private void a(EntityPlayerMP instance) {
        if (!((ICustomMovementEntity)instance).llm_$isAnimation(AnimationRegistry.SWIMMING_ID)) {
            instance.addExhaustionForJump();
        }
    }
}
