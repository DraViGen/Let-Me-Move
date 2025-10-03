package net.dravigen.letMeMove.interfaces;

import net.dravigen.letMeMove.EnumPose;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Unique;

public interface ICustomMovementEntity {

    @Unique
    @Environment(EnvType.CLIENT)
    float letMeMove_$getLeaningPitch();
    void letMeMove_$setLeaningPitch(float pitch);

    int letMeMove_$getCustomMovementState();
    void letMeMove_$setCustomMovementState(EnumPose state);

    default EnumPose getPose() {
        return EnumPose.getPose(this.letMeMove_$getCustomMovementState());
    }

    default boolean isPose(EnumPose pose) {
        return getPose() == pose;
    }

}
