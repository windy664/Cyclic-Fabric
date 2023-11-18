package net.knsh.cyclic.block.anvilvoid;

import net.knsh.cyclic.block.BlockCyclic;
import net.knsh.cyclic.registry.CyclicBlockEntities;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AnvilVoidBlock extends BlockCyclic {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape PART_BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    public static final VoxelShape PART_LOWER_X = Block.box(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
    public static final VoxelShape PART_MID_X = Block.box(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    public static final VoxelShape PART_UPPER_X = Block.box(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
    public static final VoxelShape PART_LOWER_Z = Block.box(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
    public static final VoxelShape PART_MID_Z = Block.box(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
    public static final VoxelShape PART_UPPER_Z = Block.box(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
    public static final VoxelShape X_AXIS_AABB = Shapes.or(PART_BASE, PART_LOWER_X, PART_MID_X, PART_UPPER_X);
    public static final VoxelShape Z_AXIS_AABB = Shapes.or(PART_BASE, PART_LOWER_Z, PART_MID_Z, PART_UPPER_Z);

    public AnvilVoidBlock(Properties settings) {
        super(settings.strength(1.8F).sound(SoundType.ANVIL));
        this.setHasGui();
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnvilVoidBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            MenuProvider screenHandlerFactory = state.getMenuProvider(world, pos);

            if (screenHandlerFactory != null) {
                player.openMenu(screenHandlerFactory);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AnvilVoidBlockEntity) {
                Containers.dropContents(world, pos, (AnvilVoidBlockEntity)blockEntity);
                // update comparators
                world.updateNeighbourForOutputSignal(pos,this);
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CyclicBlocks.ANVILVOID.blockEntity(), AnvilVoidBlockEntity::tick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
