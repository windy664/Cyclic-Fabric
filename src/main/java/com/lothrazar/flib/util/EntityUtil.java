package com.lothrazar.flib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;

public class EntityUtil {
    public static AABB makeBoundingBox(BlockPos center, int hRadius, int vRadius) {
        return makeBoundingBox(center.getX(), center.getY(), center.getZ(), hRadius, vRadius);
    }

    public static AABB makeBoundingBox(double x, double y, double z, int hRadius, int vRadius) {
        return new AABB(
                x - hRadius, y - vRadius, z - hRadius,
                x + hRadius, y + vRadius, z + hRadius);
    }

    public static void setCooldownItem(Player player, Item item, int cooldown) {
        player.getCooldowns().addCooldown(item, cooldown);
    }

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
