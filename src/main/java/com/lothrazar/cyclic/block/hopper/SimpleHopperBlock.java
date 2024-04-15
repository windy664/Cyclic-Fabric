package com.lothrazar.cyclic.block.hopper;

import com.lothrazar.cyclic.Cyclic;
import com.lothrazar.cyclic.block.BlockCyclic;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleHopperBlock extends BlockCyclic {
    public static final DirectionProperty FACING = BlockStateProperties.FACING_HOPPER;

    public SimpleHopperBlock(Properties settings) {
        super(settings.strength(1.3F));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace().getOpposite();
        if (direction == Direction.UP) {
            direction = Direction.DOWN;
        }
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimpleHopperBlock.getShapeHopper(state, level, pos, context);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return SimpleHopperBlock.getRaytraceShapeHopper(state, level, pos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SimpleHopperBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, CyclicBlocks.HOPPER.blockEntity(), level.isClientSide ? SimpleHopperBlockEntity::clientTick : SimpleHopperBlockEntity::serverTick);
    }

    public static VoxelShape getShapeHopper(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        try {
            return Blocks.HOPPER.getShape(state, worldIn, pos, context);
        }
        catch (Exception e) {
            Cyclic.LOGGER.error("An unknown has broken the vanilla hopper, causing compatibility issues", e);
            return Shapes.block();
        }
    }

    public static VoxelShape getRaytraceShapeHopper(BlockState state, BlockGetter worldIn, BlockPos pos) {
        try {
            return Blocks.HOPPER.getInteractionShape(state, worldIn, pos);
        }
        catch (Exception e) {
            Cyclic.LOGGER.error("An unknown has broken the vanilla hopper, causing compatibility issues", e);
            return Shapes.block();
        }
    }
}
