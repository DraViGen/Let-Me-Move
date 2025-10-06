package net.dravigen.letMeMove.mixin;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
/*
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow @Final public AxisAlignedBB boundingBox;

    @Redirect(method = "isEntityInsideOpaqueBlock(Z)Z",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;canBlockSuffocateEntity(III)Z",ordinal = 0))
    private boolean customCheckInsideBlock(World world, int x, int y, int z) {
        return world.canBlockSuffocateEntity(x, MathHelper.floor_double(this.boundingBox.maxY - 0.1), z);
    }
}*/
