package net.dravigen.letMeMove.mixin;

import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public double posY;

    @Redirect(method = "isEntityInsideOpaqueBlock(Z)Z",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;canBlockSuffocateEntity(III)Z",ordinal = 0))
    private boolean a(World world, int x, int y, int z) {
        return world.canBlockSuffocateEntity(x, MathHelper.floor_double(this.posY),z);
    }
}
