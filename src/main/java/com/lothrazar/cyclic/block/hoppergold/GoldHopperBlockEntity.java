package com.lothrazar.cyclic.block.hoppergold;

import com.lothrazar.cyclic.block.hopper.SimpleHopperBlockEntity;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GoldHopperBlockEntity extends SimpleHopperBlockEntity {
    public GoldHopperBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.HOPPERGOLD.blockEntity(), pos, state);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, GoldHopperBlockEntity e) {
        e.tick(level, blockPos, blockState);
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, GoldHopperBlockEntity e) {}

    @Override
    public int getFlow() {
        return 64;
    }
}
