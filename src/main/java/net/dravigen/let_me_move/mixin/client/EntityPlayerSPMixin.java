package net.dravigen.let_me_move.mixin.client;

import net.dravigen.let_me_move.animation.AnimRegistry;
import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin extends AbstractClientPlayer {
	
	public EntityPlayerSPMixin(World par1World, String par2Str) {
		super(par1World, par2Str);
	}
	
	@Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isSneaking()Z"))
	private boolean disableSprintOnCrawl(EntityPlayerSP instance) {
		if (instance.isSneaking() ||
				((ICustomMovementEntity) instance).llm_$isAnimation(AnimRegistry.SWIMMING.getID())) {
			instance.setSprinting(false);
			
			return true;
		}
		
		return false;
	}
	
	@Redirect(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/src/MovementInput;sneak:Z", ordinal = 0, opcode = Opcodes.GETFIELD))
	private boolean disableVanillaSneakLowerCamera(MovementInput instance) {
		return ((ICustomMovementEntity) this).llm_$isAnimation(AnimRegistry.CROUCHING.getID());
	}
	
	@Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z", ordinal = 0))
	private boolean customCollisionOne(EntityPlayerSP instance, int par1, int par2, int par3) {
		return !instance.worldObj.getCollidingBoundingBoxes(instance, instance.boundingBox).isEmpty();
	}
	
	@Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z", ordinal = 1))
	private boolean disableUselessCheck(EntityPlayerSP instance, int par1, int par2, int par3) {
		return false;
	}
}
