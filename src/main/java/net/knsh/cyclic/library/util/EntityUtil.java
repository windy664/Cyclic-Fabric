package net.knsh.cyclic.library.util;

import net.minecraft.core.Direction;

public class EntityUtil {
    public static float getYawFromFacing(Direction currentFacing) {
        switch (currentFacing) {
            case DOWN:
            case UP:
            case SOUTH:
            default:
                return 0;
            case EAST:
                return 270F;
            case NORTH:
                return 180F;
            case WEST:
                return 90F;
        }
    }
}
