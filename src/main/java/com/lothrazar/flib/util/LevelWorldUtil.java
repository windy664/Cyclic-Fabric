package com.lothrazar.flib.util;

import com.lothrazar.flib.core.BlockPosDim;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class LevelWorldUtil {
    public static Direction getRandomDirection(RandomSource rand) {
        int index = Mth.nextInt(rand, 0, Direction.values().length - 1);
        return Direction.values()[index];
    }

    public static ResourceKey<Level> stringToDimension(String key) {
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(key));
    }

    public static String dimensionToString(Level world) {
        return world.dimension().location().toString();
    }

    public static boolean dimensionIsEqual(BlockPosDim targetPos, Level world) {
        if (targetPos == null || targetPos.getDimension() == null) {
            return false;
        }
        return targetPos.getDimension().equalsIgnoreCase(LevelWorldUtil.dimensionToString(world));
    }
}
