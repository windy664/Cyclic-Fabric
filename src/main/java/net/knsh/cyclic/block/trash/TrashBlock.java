package net.knsh.cyclic.block.trash;

import net.knsh.cyclic.block.BlockCyclic;
import net.knsh.cyclic.registry.CyclicBlockEntities;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrashBlock extends BlockCyclic {
    private static final double BOUNDS = 1;
    public static final VoxelShape AABB = Block.box(BOUNDS, BOUNDS, BOUNDS, 16 - BOUNDS, 16 - BOUNDS, 16 - BOUNDS);

    public TrashBlock(Properties properties) {
        super(properties.strength(1.8F).sound(SoundType.METAL).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrashBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CyclicBlocks.TRASH.blockEntity(), world.isClientSide ? TrashBlockEntity::clientTick : TrashBlockEntity::serverTick);
    }
}
