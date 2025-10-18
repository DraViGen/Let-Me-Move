package net.dravigen.letMeMove.mixin.server;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin extends EntityPlayer {
	
	public EntityPlayerMPMixin(World par1World, String par2Str) {
		super(par1World, par2Str);
	}
	
	@Inject(method = "getEyeHeight", at = @At("RETURN"), cancellable = true)
	private void customEyeHeight(CallbackInfoReturnable<Float> cir) {
		if (this.sleeping) {
			cir.setReturnValue(0.18f);
		}
		
		cir.setReturnValue(1.62f - (1.62f - (this.height - 0.18f)));
	}
}
