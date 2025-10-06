package net.dravigen.letMeMove.mixin.render;

import net.minecraft.src.ModelRenderer;
import net.minecraft.src.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderPlayer.class)
public class RenderPlayerMixin {
    @Redirect(method = "renderFirstPersonArm",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ModelRenderer;render(F)V"))
    private void renderArm(ModelRenderer arm, float var2) {
        arm.rotateAngleX = 0;
        arm.rotateAngleY = 0;
        arm.rotateAngleZ = 0;
        arm.rotationPointX = -5f;
        arm.rotationPointY = 2;
        arm.rotationPointZ = 0;

        arm.render(var2);
    }
}
