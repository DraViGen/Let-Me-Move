package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.LetMeMoveAddon;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.packet.PacketUtils;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.ServerConfigurationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationManager.class)
public abstract class ServerConfigurationManagerMixin {

    @Inject(method = "playerLoggedIn", at = @At("HEAD"))
    private void loadPlayerAnimation(EntityPlayerMP player, CallbackInfo ci) {
        ResourceLocation id = LetMeMoveAddon.getDataID(player, LetMeMoveAddon.CURRENT_ANIMATION);
        ((ICustomMovementEntity) player).llm_$setAnimation(id);
        PacketUtils.animationStoCSync(id, player.playerNetServerHandler);
    }

    @Inject(method = "playerLoggedOut", at = @At("TAIL"))
    private void savePlayerAnimation(EntityPlayerMP player, CallbackInfo ci) {
        player.setData(LetMeMoveAddon.CURRENT_ANIMATION, String.valueOf(((ICustomMovementEntity) player).llm_$getAnimationID()));
    }
}
