package net.dravigen.letMeMove.mixin;

import net.minecraft.src.MovementInput;
import net.minecraft.src.MovementInputFromOptions;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MovementInputFromOptions.class)
public abstract class MovementInputFromOptionsMixin extends MovementInput {
    @Redirect(method = "updatePlayerMoveState",at = @At(value = "FIELD", target = "Lnet/minecraft/src/MovementInputFromOptions;sneak:Z",opcode = Opcodes.GETFIELD))

    private boolean disableVanillaSneakSlow(MovementInputFromOptions instance) {
        return false;
    }
}
