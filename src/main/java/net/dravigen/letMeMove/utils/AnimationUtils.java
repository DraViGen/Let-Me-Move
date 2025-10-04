package net.dravigen.letMeMove.utils;

import net.dravigen.letMeMove.render.AnimationCustom;
import net.minecraft.src.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class AnimationUtils {
    private static final Map<ResourceLocation, AnimationCustom> animationsMap = new HashMap<>();

    public static Map<ResourceLocation, AnimationCustom> getAnimationsMap() {
        return animationsMap;
    }

    public static AnimationCustom getAnimationFromID(ResourceLocation ID) {
        return animationsMap.get(ID);
    }

    public static AnimationCustom registerAnimation(ResourceLocation identifier, float height, float moveModifier) {
        AnimationCustom animation = new AnimationCustom(identifier, height, moveModifier);
        animationsMap.put(identifier, animation);
        return animation;
    }
}
