package net.dravigen.letMeMove.utils;

import net.minecraft.src.*;

public class GeneralUtils {
	public static float lerp(float delta, float start, float end) {
		return start + delta * (end - start);
	}
	
	public static boolean isInsideWater(Entity entity) {
		World world = entity.worldObj;
		AxisAlignedBB bb = entity.boundingBox.copy();
		bb.offset(0, 0.2, 0);
		int minY = MathHelper.floor_double(bb.minY + 0.2);
		
		return world.getBlockMaterial(MathHelper.floor_double(entity.posX), minY,
				MathHelper.floor_double(entity.posZ)) == Material.water;
	}
	
	public static boolean isHeadInsideWater(Entity entity) {
		World world = entity.worldObj;
		AxisAlignedBB bb = entity.boundingBox.copy();
		int eye = MathHelper.floor_double(bb.maxY - 0.3);
		
		return world.getBlockMaterial(MathHelper.floor_double(entity.posX), eye,
				MathHelper.floor_double(entity.posZ)) == Material.water;
	}
	
	public static boolean isEntityFeetInsideBlock(Entity entity) {
		int x = MathHelper.floor_double(entity.posX);
		int y = MathHelper.floor_double(entity.boundingBox.minY);
		int z = MathHelper.floor_double(entity.posZ);
		
		return entity.worldObj.isBlockFullCube(x, y, z);
	}
	
	public static boolean isEntityHeadNormalHeightInsideBlock(Entity entity) {
		int x = MathHelper.floor_double(entity.posX);
		int y = MathHelper.floor_double(entity.boundingBox.minY + 1.8);
		int z = MathHelper.floor_double(entity.posZ);
		
		return entity.worldObj.isBlockFullCube(x, y, z);
	}
	
	public static float lerpAngle(float angleOne, float angleTwo, float magnitude) {
		float f = (magnitude - angleTwo) % (float) (Math.PI * 2);
		
		if (f < (float) -Math.PI) {
			f += (float) (Math.PI * 2);
		}
		
		if (f >= (float) Math.PI) {
			f -= (float) (Math.PI * 2);
		}
		
		return angleTwo + angleOne * f;
	}
	
	public static float method_2807(float f) {
		return -65.0F * f + f * f;
	}
	
	public static float incrementUntilGoal(float currentValue, float goalValue, float easeFactor) {
		
		float difference = goalValue - currentValue;
		
		float stepSize = difference * easeFactor;
		
		return currentValue + stepSize;
	}
	
	public static float incrementAngleUntilGoal(float currentValue, float goalValue, float easeFactor) {
		
		float difference = goalValue - currentValue;
		
		difference = difference % 360.0F;
		
		if (difference > 180.0F) {
			difference -= 360.0F;
		}
		else if (difference < -180.0F) {
			difference += 360.0F;
		}
		
		float stepSize = difference * easeFactor;
		
		float newValue = currentValue + stepSize;
		
		newValue = newValue % 360.0F;
		
		if (newValue < 0) {
			newValue += 360.0F;
		}
		
		return newValue;
	}
	
	public static coords checkEntityAgainstWall(Entity entity) {
		double x = entity.posX;
		int y = MathHelper.floor_double(entity.boundingBox.minY);
		double z = entity.posZ;
		
		boolean wallEast = entity.worldObj.isBlockFullCube(MathHelper.floor_double(x + entity.width / 2 + 0.1), y, MathHelper.floor_double(z));
		boolean wallWest = entity.worldObj.isBlockFullCube(MathHelper.floor_double(x - entity.width / 2 - 0.1), y, MathHelper.floor_double(z));
		boolean wallSouth = entity.worldObj.isBlockFullCube(MathHelper.floor_double(x), y, MathHelper.floor_double(z + entity.width / 2 + 0.1));
		boolean wallNorth = entity.worldObj.isBlockFullCube(MathHelper.floor_double(x), y, MathHelper.floor_double(z - entity.width / 2 - 0.1));
		
		int facing = getFacing(entity);
		
		switch (facing) {
			case 0 : if (wallNorth) return coords.NORTH;
			case 1 : if (wallEast) return coords.EAST;
			case 2 : if (wallSouth) return coords.SOUTH;
			case 3 : if (wallWest) return coords.WEST;
		}
		
		return wallEast ?
				coords.EAST
				: wallWest ?
				coords.WEST
				: wallSouth ?
				coords.SOUTH
				: wallNorth ?
				coords.NORTH
				: null;
	}
	
	public static coords checkEntityAgainstWall(Entity entity, double yOff) {
		double x = entity.posX;
		int y = MathHelper.floor_double(entity.boundingBox.minY + yOff);
		double z = entity.posZ;
		
		boolean wallEast = entity.worldObj.isBlockFullCube(MathHelper.floor_double(x + entity.width / 2 + 0.1), y, MathHelper.floor_double(z));
		boolean wallWest = entity.worldObj.isBlockFullCube(MathHelper.floor_double(x - entity.width / 2 - 0.1), y, MathHelper.floor_double(z));
		boolean wallSouth = entity.worldObj.isBlockFullCube(MathHelper.floor_double(x), y, MathHelper.floor_double(z + entity.width / 2 + 0.1));
		boolean wallNorth = entity.worldObj.isBlockFullCube(MathHelper.floor_double(x), y, MathHelper.floor_double(z - entity.width / 2 - 0.1));
		
		int facing = getFacing(entity);
		
		switch (facing) {
			case 0 : if (wallNorth) return coords.NORTH;
			case 1 : if (wallEast) return coords.EAST;
			case 2 : if (wallSouth) return coords.SOUTH;
			case 3 : if (wallWest) return coords.WEST;
		}
		
		return wallEast ?
				coords.EAST
				: wallWest ?
				coords.WEST
				: wallSouth ?
				coords.SOUTH
				: wallNorth ?
				coords.NORTH
				: null;
	}
	
	public static int checkIfOpenSpaceAboveWall(Entity entity) {
		coords side = checkEntityAgainstWall(entity);
		
		if (side == null) return -1;
		
		int x = MathHelper.floor_double(entity.posX + (side == coords.EAST ? 1 : side == coords.WEST ? -1 : 0));
		int y = MathHelper.floor_double(entity.boundingBox.minY + 2);
		int z = MathHelper.floor_double(entity.posZ + (side == coords.SOUTH ? 1 : side == coords.NORTH ? -1 : 0));
		
		World world = entity.worldObj;
		
		if(!world.getBlockMaterial(x, y, z).blocksMovement()
				&& world.isBlockFullCube(x, y - 1, z)) {
			return y;
		}
		else return -1;
	}
	
	public static boolean checkIfEntityFacingWall(Entity entity) {
		coords wall = checkEntityAgainstWall(entity);
		int facing = getFacing(entity);
		
		if (wall == null) return false;
		
		return wall == coords.NORTH && facing == 0
				|| wall == coords.EAST && facing == 1
				|| wall == coords.SOUTH && facing == 2
				|| wall == coords.WEST && facing == 3;
	}
	
	private static int getFacing(Entity entity) {
		float yaw = (entity.getRotationYawHead() + 180) % 360 ;
		
		yaw = yaw < 0 ? 360 + yaw : yaw;
		
		int roundedYaw = Math.round(yaw / 90) % 4;
		return roundedYaw;
	}
	
	public enum coords {
		NORTH, EAST, SOUTH, WEST
	}
}
