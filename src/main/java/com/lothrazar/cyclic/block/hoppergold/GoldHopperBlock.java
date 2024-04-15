package com.lothrazar.cyclic.block.hoppergold;

import com.lothrazar.cyclic.block.hopper.SimpleHopperBlock;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.library.block.EntityBlockFlib;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoldHopperBlock extends SimpleHopperBlock {

    public GoldHopperBlock(BlockBehaviour.Properties settings) {
        super(settings.strength(1.3F));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new GoldHopperBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return EntityBlockFlib.createTickerHelper(blockEntityType, CyclicBlocks.HOPPERGOLD.blockEntity(), level.isClientSide ? GoldHopperBlockEntity::clientTick : GoldHopperBlockEntity::serverTick);
    }
}
