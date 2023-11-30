package net.knsh.cyclic.block.cable;

import com.google.common.collect.Maps;
import net.knsh.cyclic.block.BlockCyclic;
import net.knsh.cyclic.library.util.SoundUtil;
import net.knsh.cyclic.porting.neoforge.network.NetworkHooks;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicItems;
import net.knsh.cyclic.registry.CyclicSounds;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CableBase extends BlockCyclic implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<EnumConnectType> DOWN = EnumProperty.create("down", EnumConnectType.class);
    public static final EnumProperty<EnumConnectType> UP = EnumProperty.create("up", EnumConnectType.class);
    public static final EnumProperty<EnumConnectType> NORTH = EnumProperty.create("north", EnumConnectType.class);
    public static final EnumProperty<EnumConnectType> SOUTH = EnumProperty.create("south", EnumConnectType.class);
    public static final EnumProperty<EnumConnectType> WEST = EnumProperty.create("west", EnumConnectType.class);
    public static final EnumProperty<EnumConnectType> EAST = EnumProperty.create("east", EnumConnectType.class);
    public static final Map<Direction, EnumProperty<EnumConnectType>> FACING_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), (p) -> {
        p.put(Direction.NORTH, NORTH);
        p.put(Direction.EAST, EAST);
        p.put(Direction.SOUTH, SOUTH);
        p.put(Direction.WEST, WEST);
        p.put(Direction.UP, UP);
        p.put(Direction.DOWN, DOWN);
    });
    private static final double top = 16;
    private static final double bot = 0;
    private static final double C = 8;
    private static final double w = 2;
    private static final double sm = C - w;
    private static final double lg = C + w;
    protected static final VoxelShape AABB = Block.box(sm, sm, sm, lg, lg, lg);
    //Y for updown
    protected static final VoxelShape AABB_UP = Block.box(sm, sm, sm, lg, top, lg);
    protected static final VoxelShape AABB_DOWN = Block.box(sm, bot, sm, lg, lg, lg);
    //Z for n-s
    protected static final VoxelShape AABB_NORTH = Block.box(sm, sm, bot, lg, lg, lg);
    protected static final VoxelShape AABB_SOUTH = Block.box(sm, sm, sm, lg, lg, top);
    //X for e-w
    protected static final VoxelShape AABB_WEST = Block.box(bot, sm, sm, lg, lg, lg);
    protected static final VoxelShape AABB_EAST = Block.box(sm, sm, sm, top, lg, lg);

    static boolean shapeConnects(BlockState state, EnumProperty<EnumConnectType> dirctionProperty) {
        return state.getValue(dirctionProperty).isConnected();
    }

    public static VoxelShape createShape(BlockState state) {
        VoxelShape shape = AABB;
        if (shapeConnects(state, UP)) {
            shape = Shapes.joinUnoptimized(shape, AABB_UP, BooleanOp.OR);
        }
        if (shapeConnects(state, DOWN)) {
            shape = Shapes.joinUnoptimized(shape, AABB_DOWN, BooleanOp.OR);
        }
        if (shapeConnects(state, WEST)) {
            shape = Shapes.joinUnoptimized(shape, AABB_WEST, BooleanOp.OR);
        }
        if (shapeConnects(state, EAST)) {
            shape = Shapes.joinUnoptimized(shape, AABB_EAST, BooleanOp.OR);
        }
        if (shapeConnects(state, NORTH)) {
            shape = Shapes.joinUnoptimized(shape, AABB_NORTH, BooleanOp.OR);
        }
        if (shapeConnects(state, SOUTH)) {
            shape = Shapes.joinUnoptimized(shape, AABB_SOUTH, BooleanOp.OR);
        }
        return shape;
    }

    public CableBase(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hit.getDirection() == null) {
            return super.use(state, world, pos, player, hand, hit);
        }
        if (hand != InteractionHand.MAIN_HAND) {
            return super.use(state, world, pos, player, hand, hit);
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(CyclicItems.CABLE_WRENCH)) {
            //ex
            boolean hasExtractor = false;
            for (Direction side : Direction.values()) {
                EnumConnectType connection = state.getValue(CableBase.FACING_TO_PROPERTY_MAP.get(side));
                if (connection.isExtraction()) {
                    hasExtractor = true;
                    break;
                }
            }
            //if (hasExtractor && (this == CyclicBlocks.ITEM_PIPE.get() || this == CyclicBlocks.FLUID_PIPE.get())) {
            if (hasExtractor && (this == CyclicBlocks.ITEM_PIPE.block())) {
                //if has extractor
                if (!world.isClientSide) {
                    BlockEntity tileEntity = world.getBlockEntity(pos);
                    if (tileEntity instanceof MenuProvider) {
                        MenuProvider screenHandlerFactory = state.getMenuProvider(world, pos);

                        if (screenHandlerFactory != null) {
                            player.openMenu(screenHandlerFactory);
                        }
                    }
                    else {
                        throw new IllegalStateException("Our named container provider is missing!");
                    }
                }
                return InteractionResult.SUCCESS;
            }
            //ex
            return super.use(state, world, pos, player, hand, hit);
        }
        rotateFromWrench(state, world, pos, player, hit);
        player.swing(hand);
        return super.use(state, world, pos, player, hand, hit);
    }

    public static void crouchClick(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        rotateFromWrench(state, level, pos, player, hit);
    }

    private static void rotateFromWrench(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        //now must be wrench
        final float hitLimit = 0.28F;
        Direction sideToToggle = hit.getDirection();
        //hitX y and Z from old onBlockActivated
        double hitX = hit.getLocation().x - pos.getX();
        double hitY = hit.getLocation().y - pos.getY();
        double hitZ = hit.getLocation().z - pos.getZ();
        if (hitX < hitLimit) {
            sideToToggle = Direction.WEST;
        }
        else if (hitX > 1 - hitLimit) {
            sideToToggle = Direction.EAST;
        }
        else if (hitY < hitLimit) {
            sideToToggle = Direction.DOWN;
        }
        else if (hitY > 1 - hitLimit) {
            sideToToggle = Direction.UP;
        }
        else if (hitZ < hitLimit) {
            sideToToggle = Direction.NORTH;
        }
        else if (hitZ > 1 - hitLimit) {
            sideToToggle = Direction.SOUTH;
        }
        EnumProperty<EnumConnectType> prop = CableBase.FACING_TO_PROPERTY_MAP.get(sideToToggle);
        if (state.hasProperty(prop)) {
            EnumConnectType status = state.getValue(prop);
            //inventory is decided not by wrench but by normal mode
            //so it rotates:
            BlockState newState = state;
            // INVENTORY// NONE -> CABLE(extract) -> BLOCKED -> and back to none again
            boolean updatePost = false;
            if (player.isCrouching()) {
                switch (status) {
                    case BLOCKED:
                        newState = state.setValue(prop, EnumConnectType.NONE);
                        updatePost = true;
                        break;
                    default: //anything to blocked
                        newState = state.setValue(prop, EnumConnectType.BLOCKED);
                        break;
                }
            }
            else {
                switch (status) {
                    case BLOCKED:
                        newState = state.setValue(prop, EnumConnectType.NONE);
                        updatePost = true;
                        break;
                    case INVENTORY: // inventory normal
                        newState = state.setValue(prop, EnumConnectType.CABLE);//to extract
                        updatePost = true;
                        break;
                    case NONE: // no connection
                        //if its none stay teh same
                        break;
                    case CABLE: // extract
                        newState = state.setValue(prop, EnumConnectType.INVENTORY);
                        break;
                }
            }
            if (world.getBlockState(pos).getBlock() instanceof CableBase && world.setBlockAndUpdate(pos, newState)) {
                if (updatePost) {
                    newState.updateShape(sideToToggle, world.getBlockState(pos.relative(sideToToggle)), world, pos, pos.relative(sideToToggle));
                }
                if (world.isClientSide) {
                    SoundUtil.playSound(player, CyclicSounds.THUNK, 0.2F, 1F);
                }
            }
        }
    }

    public static boolean isCableBlocked(BlockState blockState, Direction side) {
        if (side == null) {
            return false;
        }
        EnumProperty<EnumConnectType> property = CableBase.FACING_TO_PROPERTY_MAP.get(side);
        return blockState.getBlock() instanceof CableBase
                && blockState.hasProperty(property)
                && blockState.getValue(property).isUnBlocked() == false;
    }
}
