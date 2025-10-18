package net.dravigen.letMeMove.mixin.client;

import net.dravigen.letMeMove.animation.AnimationRegistry;
import net.dravigen.letMeMove.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = EntityPlayerSP.class, remap = false)
public abstract class EntityPlayerSPMixin extends AbstractClientPlayer {
	
	public EntityPlayerSPMixin(World par1World, String par2Str) {
		super(par1World, par2Str);
	}
	
	@Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isSneaking()Z"))
	private boolean disableSprintOnCrawl(EntityPlayerSP instance) {
		if (instance.isSneaking() || ((ICustomMovementEntity) instance).llm_$isAnimation(
				AnimationRegistry.SWIMMING_ID)) {
			instance.setSprinting(false);
			
			return true;
		}
		
		return false;
	}
	
	@Redirect(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/src/MovementInput;sneak:Z", ordinal = 0, opcode = Opcodes.GETFIELD))
	private boolean disableVanillaSneakLowerCamera(MovementInput instance) {
		return ((ICustomMovementEntity) this).llm_$isAnimation(AnimationRegistry.CROUCHING_ID);
	}
	
	@ModifyArg(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z", ordinal = 1), index = 1)
	private int customCollision(int par1) {
		return this.height > 1 ? par1 : par1 - 1;
	}
}
