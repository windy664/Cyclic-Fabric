package net.knsh.cyclic.block.melter;

import net.knsh.cyclic.block.BlockCyclic;
import net.knsh.cyclic.lookups.CyclicLookup;
import net.knsh.cyclic.lookups.Lookup;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class BlockMelter extends BlockCyclic implements Lookup {
    public BlockMelter(Properties properties) {
        super(properties.strength(1.2F).noOcclusion());
        this.setHasGui();
    }

    @Override
    @Deprecated
    public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return false;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileMelter(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CyclicBlocks.MELTER.blockEntity(), world.isClientSide ? TileMelter::clientTick : TileMelter::serverTick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public void registerLookups() {
        CyclicLookup.ITEM_HANDLER.registerSelf(CyclicBlocks.MELTER.blockEntity());
        CyclicLookup.FLUID_HANDLER.registerSelf(CyclicBlocks.MELTER.blockEntity());
    }
}
