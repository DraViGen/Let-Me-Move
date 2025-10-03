package net.dravigen.letMeMove.mixin;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*
@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin extends EntityPlayer {
    public EntityPlayerMPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }


    @Inject(method = "getEyeHeight",at = @At("RETURN"), cancellable = true)
    private void customEyeHeightOnPose(CallbackInfoReturnable<Float> cir) {
        if (this.sleeping) return;

        if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            cir.setReturnValue(0.18f);
        }
    }
}*/
