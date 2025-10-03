package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.EnumPose;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityPlayerSP.class,remap = false)
public abstract class EntityPlayerSPMixin extends AbstractClientPlayer {
    @Shadow protected abstract boolean isBlockTranslucent(int par1, int par2, int par3);

    public EntityPlayerSPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }


    @Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z",ordinal = 1))
    private boolean handleCollisionBetter(EntityPlayerSP instance, int par1, int par2, int par3) {
        double height = instance.boundingBox.maxY - instance.boundingBox.minY;
        double var1 = this.boundingBox.minY - MathHelper.floor_double(this.boundingBox.minY);
        return height <= 1.5 ? height <= 1 ? false : this.isBlockTranslucent(par1, var1 > 0 ? MathHelper.floor_double(par2-var1+0.05) : par2, par3) : this.isBlockTranslucent(par1,par2,par3);
    }

    @Redirect(method = "onLivingUpdate",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isSneaking()Z"))
    private boolean disableSprintOnCrawl(EntityPlayerSP instance) {
        if (instance.isSneaking() || ((ICustomMovementEntity)instance).isPose(EnumPose.CRAWLING)) {
            instance.setSprinting(false);
            return true;
        }
        return false;
    }

    @Redirect(method = "onLivingUpdate",at = @At(value = "FIELD", target = "Lnet/minecraft/src/MovementInput;sneak:Z", ordinal = 0, opcode = Opcodes.GETFIELD))
    private boolean disableVanillaSneakLowerCamera(MovementInput instance) {
        return ((ICustomMovementEntity)this).isPose(EnumPose.SNEAKING);
    }

    @ModifyVariable(method = "pushOutOfBlocks", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double customCollision(double value) {
        double height = this.boundingBox.maxY - this.boundingBox.minY;
        return value - (height < 1.5 ? 0.5 : 0);
    }
}
