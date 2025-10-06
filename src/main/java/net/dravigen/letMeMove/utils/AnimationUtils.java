package net.dravigen.letMeMove.utils;

import net.dravigen.letMeMove.render.AnimationCustom;
import net.minecraft.src.*;

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

    public static AnimationCustom registerAnimation(ResourceLocation identifier, float height, float moveModifier, boolean needLeaningUpdate) {
        AnimationCustom animation = new AnimationCustom(identifier, height, moveModifier, needLeaningUpdate);
        animationsMap.put(identifier, animation);

        return animation;
    }

    public static void resetAnimationRotationPoints(ModelBiped model) {
        model.bipedBody.rotationPointX = 0f;
        model.bipedBody.rotationPointY = 0.0F;
        model.bipedBody.rotationPointZ = 0.0F;

        model.bipedHead.rotationPointX = 0.0F;
        model.bipedHead.rotationPointY = 0.0F;
        model.bipedHead.rotationPointZ = 0.0F;

        model.bipedHeadwear.rotationPointX = 0.0F;
        model.bipedHeadwear.rotationPointY = 0.0F;
        model.bipedHeadwear.rotationPointZ = 0.0F;

        model.bipedRightArm.rotationPointX = -5f;
        model.bipedRightArm.rotationPointY = 2;
        model.bipedRightArm.rotationPointZ = 0;

        model.bipedLeftArm.rotationPointX = 5f;
        model.bipedLeftArm.rotationPointY = 2;
        model.bipedLeftArm.rotationPointZ = 0;

        model.bipedRightLeg.rotationPointX = -1.9f;
        model.bipedRightLeg.rotationPointY = 12;
        model.bipedRightLeg.rotationPointZ = 0.1f;

        model.bipedLeftLeg.rotationPointX = 1.9f;
        model.bipedLeftLeg.rotationPointY = 12;
        model.bipedLeftLeg.rotationPointZ = 0.1f;
    }

    public enum type {
        X,Y,Z
    }

    public static void setSmoothAllRotation(ModelRenderer part, float rotX, float rotY, float rotZ) {
        setSmoothRotation(part, type.X, rotX, 0.01f);
        setSmoothRotation(part, type.Y, rotY, 0.01f);
        setSmoothRotation(part, type.Z, rotZ, 0.01f);
    }

    public static void setSmoothAllRotation(ModelRenderer part, float rotX, float rotY, float rotZ, float factor) {
        setSmoothRotation(part, type.X, rotX, factor);
        setSmoothRotation(part, type.Y, rotY, factor);
        setSmoothRotation(part, type.Z, rotZ, factor);
    }

    public static void setSmoothAllRotation(ModelRenderer part, float rotX, float rotY, float rotZ, float factorX, float factorY, float factorZ) {
        setSmoothRotation(part, type.X, rotX, factorX);
        setSmoothRotation(part, type.Y, rotY, factorY);
        setSmoothRotation(part, type.Z, rotZ, factorZ);
    }

    public static void setSmoothRotation(ModelRenderer part, type type, float rot, float factor) {
        if (type == AnimationUtils.type.X) {
            part.rotateAngleX = GeneralUtils.incrementUntilGoal(part.rotateAngleX, rot, factor);
        }
        else if (type == AnimationUtils.type.Y) {
            part.rotateAngleY = GeneralUtils.incrementUntilGoal(part.rotateAngleY, rot, factor);
        }
        else if (type == AnimationUtils.type.Z) {
            part.rotateAngleZ = GeneralUtils.incrementUntilGoal(part.rotateAngleZ, rot, factor);
        }
    }
}
