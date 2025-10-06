package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.GeneralUtils;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.Minecraft;
import net.minecraft.src.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow private boolean isGamePaused;
    @Shadow public WorldClient theWorld;
    @Shadow public EntityLivingBase renderViewEntity;

    @Inject(method = "runGameLoop",at = @At("HEAD"))
    private void updateRender(CallbackInfo ci) {
        EntityLivingBase player = this.renderViewEntity;

        if (!this.isGamePaused && this.theWorld != null && player != null) {
            player.yOffset = GeneralUtils.incrementUntilGoal(player.yOffset, player.height - 0.18f, 0.025f);
            ((ICustomMovementEntity) player).llm_$getAnimation().updateLeaning(player);
        }
    }
}
