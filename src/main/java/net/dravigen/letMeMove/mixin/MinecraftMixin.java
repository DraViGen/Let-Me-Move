package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.dravigen.letMeMove.utils.GeneralUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow public WorldClient theWorld;
    @Shadow public EntityClientPlayerMP thePlayer;
    @Unique long prevTime;
    @Shadow private boolean isGamePaused;

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void updateRender(CallbackInfo ci) {
        EntityPlayer player = this.thePlayer;
        ICustomMovementEntity customPlayer = (ICustomMovementEntity) player;

        if (!this.isGamePaused && this.theWorld != null && player != null) {
            float delta = (System.currentTimeMillis() - prevTime) / 25f;

            customPlayer.llm_$getAnimation().updateLeaning(player);

            player.yOffset = GeneralUtils.incrementUntilGoal(player.yOffset, player.height - 0.18f, 0.4f * delta);
        }

        prevTime = System.currentTimeMillis();
    }
}
