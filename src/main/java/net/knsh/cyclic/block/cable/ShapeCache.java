package net.knsh.cyclic.block.cable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ShapeCache {
    private static final Map<BlockState, VoxelShape> CACHE = new HashMap<>();

    public static VoxelShape getOrCreate(BlockState state, Function<BlockState, VoxelShape> shapeFactory) {
        return CACHE.computeIfAbsent(state, shapeFactory);
    }
}
