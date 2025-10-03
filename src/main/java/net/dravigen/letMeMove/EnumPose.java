package net.dravigen.letMeMove;

import org.jetbrains.annotations.NotNull;

public enum EnumPose {
    STANDING(1.8F, 1),
    SNEAKING(1.5F, 0.3f),
    CRAWLING(0.7F, 0.15f);

    public final float height;
    public final float movementMultiplier;

    EnumPose(float height, float movementMultiplier) {
        this.height = height;
        this.movementMultiplier = movementMultiplier;
    }

    @NotNull
    public static EnumPose getPose(int i){
        for (EnumPose enumMode : EnumPose.values()) {
            if (enumMode.ordinal()==i){
                return enumMode;
            }
        }
        return EnumPose.STANDING;
    }

}
