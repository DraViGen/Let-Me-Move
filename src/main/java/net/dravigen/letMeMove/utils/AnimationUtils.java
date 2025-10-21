package net.dravigen.letMeMove.utils;

import net.dravigen.letMeMove.animation.BaseAnimation;
import net.minecraft.src.*;

import java.util.*;
import java.util.stream.Collectors;

public class AnimationUtils {
	private static int priority = 0;
	
	private static final Map<ResourceLocation, BaseAnimation> animationsMap = new HashMap<>();
	
	public static Map<ResourceLocation, BaseAnimation> getAnimationsMap() {
		return animationsMap.entrySet().stream().sorted(
				Comparator.comparingInt(animation -> animation.getValue().priority)).collect(
				Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));
	}
	
	public static BaseAnimation getAnimationFromID(ResourceLocation ID) {
		return animationsMap.get(ID);
	}
	
	public static void registerAnimation(BaseAnimation animation) {
		animation.priority = priority++;
		animationsMap.put(animation.getID(), animation);
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
	
	public static void offsetAllRotationPoints(ModelBiped model, float x, float y, float z) {
		model.bipedBody.rotationPointX += x;
		model.bipedBody.rotationPointY += y;
		model.bipedBody.rotationPointZ += z;
		
		model.bipedHead.rotationPointX += x;
		model.bipedHead.rotationPointY += y;
		model.bipedHead.rotationPointZ += z;
		
		model.bipedHeadwear.rotationPointX += x;
		model.bipedHeadwear.rotationPointY += y;
		model.bipedHeadwear.rotationPointZ += z;
		
		model.bipedRightArm.rotationPointX += x;
		model.bipedRightArm.rotationPointY += y;
		model.bipedRightArm.rotationPointZ += z;
		
		model.bipedLeftArm.rotationPointX += x;
		model.bipedLeftArm.rotationPointY += y;
		model.bipedLeftArm.rotationPointZ += z;
		
		model.bipedRightLeg.rotationPointX += x;
		model.bipedRightLeg.rotationPointY += y;
		model.bipedRightLeg.rotationPointZ += z;
		
		model.bipedLeftLeg.rotationPointX += x;
		model.bipedLeftLeg.rotationPointY += y;
		model.bipedLeftLeg.rotationPointZ += z;
	}
	
	public static void centerAllRotationPoints(ModelBiped model) {
		model.bipedBody.rotationPointX = 0.0F;
		model.bipedBody.rotationPointY = 0.0F;
		model.bipedBody.rotationPointZ = 0.0F;
		
		model.bipedHead.rotationPointX = 0.0F;
		model.bipedHead.rotationPointY = 0.0F;
		model.bipedHead.rotationPointZ = 0.0F;
		
		model.bipedHeadwear.rotationPointX = 0.0F;
		model.bipedHeadwear.rotationPointY = 0.0F;
		model.bipedHeadwear.rotationPointZ = 0.0F;
		
		model.bipedRightArm.rotationPointX = 0.0F;
		model.bipedRightArm.rotationPointY = 0.0F;
		model.bipedRightArm.rotationPointZ = 0.0F;
		
		model.bipedLeftArm.rotationPointX = 0.0F;
		model.bipedLeftArm.rotationPointY = 0.0F;
		model.bipedLeftArm.rotationPointZ = 0.0F;
		
		model.bipedRightLeg.rotationPointX = 0.0F;
		model.bipedRightLeg.rotationPointY = 0.0F;
		model.bipedRightLeg.rotationPointZ = 0.0F;
		
		model.bipedLeftLeg.rotationPointX = 0.0F;
		model.bipedLeftLeg.rotationPointY = 0.0F;
		model.bipedLeftLeg.rotationPointZ = 0.0F;
	}
	
	public static void setAllRotationPoint(ModelRenderer part, float rotX, float rotY, float rotZ) {
		setRotationPoint(part, type.X, rotX);
		setRotationPoint(part, type.Y, rotY);
		setRotationPoint(part, type.Z, rotZ);
	}
	
	public static void setRotationPoint(ModelRenderer part, type type, float rot) {
		if (type == AnimationUtils.type.X) {
			part.rotationPointX = rot;
		}
		else if (type == AnimationUtils.type.Y) {
			part.rotationPointY = rot;
		}
		else if (type == AnimationUtils.type.Z) {
			part.rotationPointZ = rot;
		}
	}
	
	public static void addAllRotationPoint(ModelRenderer part, float rotX, float rotY, float rotZ) {
		addRotationPoint(part, type.X, rotX);
		addRotationPoint(part, type.Y, rotY);
		addRotationPoint(part, type.Z, rotZ);
	}
	
	
	public static void addRotationPoint(ModelRenderer part, type type, float rot) {
		if (type == AnimationUtils.type.X) {
			part.rotationPointX += rot;
		}
		else if (type == AnimationUtils.type.Y) {
			part.rotationPointY += rot;
		}
		else if (type == AnimationUtils.type.Z) {
			part.rotationPointZ += rot;
		}
	}
	
	
	public static void smoothRotateAll(ModelRenderer part, float rotX, float rotY, float rotZ) {
		smoothRotate(part, type.X, rotX, 0.1f);
		smoothRotate(part, type.Y, rotY, 0.1f);
		smoothRotate(part, type.Z, rotZ, 0.1f);
	}
	
	public static void smoothRotateAll(ModelRenderer part, float rotX, float rotY, float rotZ, float factor) {
		smoothRotate(part, type.X, rotX, factor);
		smoothRotate(part, type.Y, rotY, factor);
		smoothRotate(part, type.Z, rotZ, factor);
	}
	
	public static void smoothRotateAll(ModelRenderer part, float[] rots, float factor) {
		smoothRotate(part, type.X, rots[0], factor);
		smoothRotate(part, type.Y, rots[1], factor);
		smoothRotate(part, type.Z, rots[2], factor);
	}
	
	public static void smoothRotateAll(ModelRenderer part, float rotX, float rotY, float rotZ, float factorX,
			float factorY, float factorZ) {
		smoothRotate(part, type.X, rotX, factorX);
		smoothRotate(part, type.Y, rotY, factorY);
		smoothRotate(part, type.Z, rotZ, factorZ);
	}
	
	public static void smoothRotate(ModelRenderer part, type type, float rot, float factor) {
		factor = factor == 0 ? 1 : factor;
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
	
	public enum type {
		X, Y, Z
	}
}
