package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.packet.PacketUtils;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.Packet250CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
