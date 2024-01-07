package net.knsh.cyclic.block.conveyor;

import net.knsh.cyclic.registry.CyclicBlockEntities;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ConveyorBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<DyeColor> COLOUR = EnumProperty.create("color", DyeColor.class);
    public static final EnumProperty<ConveyorType> TYPE = EnumProperty.create("type", ConveyorType.class);
    public static final EnumProperty<ConveyorSpeed> SPEED = EnumProperty.create("speed", ConveyorSpeed.class);
    private static final int MAX_CONNECTED_UPDATE = 16;

    public ConveyorBlock(Properties settings) {
        super(settings.strength(0.6F).noOcclusion());
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    protected static final VoxelShape AG00 = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 0.8D, 16.0D);
    protected static final VoxelShape AG01 = Block.box(1.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    protected static final VoxelShape AG02 = Block.box(2.0D, 1.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape AG03 = Block.box(3.0D, 2.0D, 0.0D, 16.0D, 3.0D, 16.0D);
    protected static final VoxelShape AG04 = Block.box(4.0D, 3.0D, 0.0D, 16.0D, 4.0D, 16.0D);
    protected static final VoxelShape AG05 = Block.box(5.0D, 4.0D, 0.0D, 16.0D, 5.0D, 16.0D);
    protected static final VoxelShape AG06 = Block.box(6.0D, 5.0D, 0.0D, 16.0D, 6.0D, 16.0D);
    protected static final VoxelShape AG07 = Block.box(7.0D, 6.0D, 0.0D, 16.0D, 7.0D, 16.0D);
    protected static final VoxelShape AG08 = Block.box(8.0D, 7.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape AG09 = Block.box(9.0D, 8.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    protected static final VoxelShape AG10 = Block.box(10.0D, 9.0D, 0.0D, 16.0D, 10.0D, 16.0D);
    protected static final VoxelShape AG11 = Block.box(11.0D, 10.0D, 0.0D, 16.0D, 11.0D, 16.0D);
    protected static final VoxelShape AG12 = Block.box(12.0D, 11.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    protected static final VoxelShape AG13 = Block.box(13.0D, 12.0D, 0.0D, 16.0D, 13.0D, 16.0D);
    protected static final VoxelShape AG14 = Block.box(14.0D, 13.0D, 0.0D, 16.0D, 14.0D, 16.0D);
    protected static final VoxelShape AG15 = Block.box(15.0D, 14.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    protected static final VoxelShape AG16 = Block.box(15.5D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    //four angled shapes
    protected static final VoxelShape ANGLEEAST = Shapes.or(AG00, AG01, AG02, AG03, AG04, AG05, AG06, AG07, AG08, AG09, AG10, AG11, AG12, AG13, AG14, AG15, AG16);
    protected static final VoxelShape ANGLESOUTH = Shapes.or(rot(AG00), rot(AG01), rot(AG02), rot(AG03), rot(AG04), rot(AG05),
            rot(AG06), rot(AG07), rot(AG08), rot(AG09), rot(AG10), rot(AG11), rot(AG12), rot(AG13), rot(AG14), rot(AG15), rot(AG16));
    protected static final VoxelShape ANGLENORTH = Shapes.or(flipx(AG00), flipx(AG01), flipx(AG02), flipx(AG03), flipx(AG04), flipx(AG05),
            flipx(AG06), flipx(AG07), flipx(AG08), flipx(AG09), flipx(AG10), flipx(AG11), flipx(AG12), flipx(AG13), flipx(AG14), flipx(AG15), flipx(AG16));
    protected static final VoxelShape ANGLEWEST = Shapes.or(flipz(AG00), flipz(AG01), flipz(AG02), flipz(AG03), flipz(AG04), flipz(AG05),
            flipz(AG06), flipz(AG07), flipz(AG08), flipz(AG09), flipz(AG10), flipz(AG11), flipz(AG12), flipz(AG13), flipz(AG14), flipz(AG15), flipz(AG16));

    /**
     * Utility methods for block shapes.
     *
     * @author SciWhiz12
     */
    /**
     * Rotates the given {@link VoxelShape} along the horizontal plane according to the given rotation direction.
     * <p>
     * Assumes the given shape is within the bounds of 1 unit on each axis.
     * <p>
     * https://gist.github.com/sciwhiz12/0852b629e7a3d0200ffc03ec7edab187
     *
     * @param shape
     *          The shape to rotate
     * @return The rotated shape
     */
    public static VoxelShape rot(final VoxelShape shape) {
        double x1 = shape.min(Direction.Axis.X), x2 = shape.max(Direction.Axis.X);
        double y1 = shape.min(Direction.Axis.Y), y2 = shape.max(Direction.Axis.Y);
        double z1 = shape.min(Direction.Axis.Z), z2 = shape.max(Direction.Axis.Z);
        double temp = z1; // ]
        z1 = x1; // ] x1 <-> z1
        x1 = temp; // ]
        temp = z2; // ]
        z2 = x2; // ] x2 <-> z2
        x2 = temp; // ]
        x1 = 1 - x1; // clockwise
        x2 = 1 - x2;
        return safeShapeBox(x1, x2, y1, y2, z1, z2);
    }

    private static VoxelShape safeShapeBox(double x1, double x2, double y1, double y2, double z1, double z2) {
        double temp;
        if (x1 > x2) {
            temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (y1 > y2) {
            temp = y1;
            y1 = y2;
            y2 = temp;
        }
        if (z1 > z2) {
            temp = z1;
            z1 = z2;
            z2 = temp;
        }
        return Shapes.box(x1, y1, z1, x2, y2, z2);
    }

    public static VoxelShape flipx(final VoxelShape shape) {
        double x1 = shape.min(Direction.Axis.X);
        double x2 = shape.max(Direction.Axis.X);
        final double y1 = shape.min(Direction.Axis.Y);
        final double y2 = shape.max(Direction.Axis.Y);
        double z1 = shape.min(Direction.Axis.Z);
        double z2 = shape.max(Direction.Axis.Z);
        double temp = z1; // ]
        z1 = x1; // ] x1 <-> z1
        x1 = temp; // ]
        temp = z2; // ]
        z2 = x2; // ] x2 <-> z2
        x2 = temp; // ]
        z1 = 1 - z1; // counterclockwise
        z2 = 1 - z2;
        return safeShapeBox(x1, y1, z1, x2, y2, z2);
    }

    public static VoxelShape flipz(final VoxelShape shape) {
        double x1 = shape.min(Direction.Axis.X);
        double x2 = shape.max(Direction.Axis.X);
        final double y1 = shape.min(Direction.Axis.Y);
        final double y2 = shape.max(Direction.Axis.Y);
        double z1 = shape.min(Direction.Axis.Z);
        double z2 = shape.max(Direction.Axis.Z);
        //flip
        x1 = 1 - x1; //
        x2 = 1 - x2;
        z1 = 1 - z1; //
        z2 = 1 - z2;
        return safeShapeBox(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public VoxelShape getShape(net.minecraft.world.level.block.state.BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

        if (state.getValue(TYPE) == ConveyorType.UP) {
            //      Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
            switch (facing) {
                case EAST:
                    return ANGLEEAST;
                case NORTH:
                    return ANGLENORTH;
                case SOUTH:
                    return ANGLESOUTH;
                case WEST:
                    return ANGLEWEST;
                case DOWN:
                case UP:
                    break;
            }
            if (state.getValue(TYPE) == ConveyorType.DOWN) {
                switch (facing) {
                    case EAST:
                        return ANGLEWEST;
                    case NORTH:
                        return ANGLESOUTH;
                    case SOUTH:
                        return ANGLENORTH;
                    case WEST:
                        return ANGLEEAST;
                    case DOWN:
                    case UP:
                        break;
                }
            }
        }
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    }

    @Override
    public boolean useShapeForLightOcclusion(net.minecraft.world.level.block.state.BlockState state) {
        return state.getValue(TYPE).isVertical();
    }

    @Override
    public boolean propagatesSkylightDown(net.minecraft.world.level.block.state.BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(TYPE).isVertical();
    }

    @Override
    public InteractionResult use(net.minecraft.world.level.block.state.BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getItemInHand(hand);
        Item heldItem = heldStack.getItem();

        if (heldItem instanceof DyeItem dye) {
            DyeColor newc = dye.getDyeColor();
            world.setBlockAndUpdate(pos, state.setValue(COLOUR, newc));
            this.setConnectedColor(world, pos, newc, 0);
            return InteractionResult.SUCCESS;
        } else if (heldItem == Items.REDSTONE_TORCH) {
            ConveyorSpeed speed = state.getValue(SPEED);
            if (world.setBlockAndUpdate(pos, state.setValue(SPEED, speed.getNext()))) {
                player.playNotifySound(SoundEvents.LEVER_CLICK, SoundSource.BLOCKS,
                        0.7f,
                        (float) (speed.getNext().getSpeed() * 5.0f));

                player.displayClientMessage(Component.translatable("block.cyclic.conveyor.speed").append(speed.getNext().getSerializedName()), true);
                this.setConnectedSpeed(world, pos, speed.getNext(), 0);
                return InteractionResult.SUCCESS;
            }
        } //TODO wrench stuff

        return super.use(state, world, pos, player, hand, hit);
    }

    private void setConnectedColor(Level world, BlockPos pos, DyeColor speedIn, int maxRecursive) {
        if (maxRecursive > MAX_CONNECTED_UPDATE) {
            return;
        }
        for (Direction d : Direction.values()) {
            //
            BlockPos offset = pos.relative(d);
            net.minecraft.world.level.block.state.BlockState here = world.getBlockState(offset);
            if (here.getBlock() == this) {
                world.setBlockAndUpdate(offset, here.setValue(COLOUR, speedIn));
                maxRecursive++;
                this.setConnectedColor(world, offset, speedIn, maxRecursive);
                //}
            }
        }
    }

    private void setConnectedSpeed(Level world, BlockPos pos, ConveyorSpeed speedIn, int maxRecursive) {
        if (maxRecursive > MAX_CONNECTED_UPDATE) {
            return;
        }
        for (Direction d : Direction.values()) {
            //
            BlockPos offset = pos.relative(d);
            net.minecraft.world.level.block.state.BlockState here = world.getBlockState(offset);
            if (here.getBlock() == this) {
                if (world.setBlockAndUpdate(offset, here.setValue(SPEED, speedIn))) {
                    maxRecursive++;
                    this.setConnectedSpeed(world, offset, speedIn, maxRecursive);
                }
            }
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, net.minecraft.world.level.block.state.BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        ConveyorSpeed speed = ConveyorSpeed.MEDIUM;
        ConveyorType type = ConveyorType.STRAIGHT;
        DyeColor col = DyeColor.GRAY;
        //
        //overwrite defaults with what is nearby
        net.minecraft.world.level.block.state.BlockState nearby = getClosestConnected(world, pos);
        if (nearby != null) {
            speed = nearby.getValue(SPEED);
            col = nearby.getValue(COLOUR);
        }
        //now set
        Direction facing = placer != null ? placer.getDirection() : Direction.NORTH;
        world.setBlock(pos, state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing).setValue(SPEED, speed).setValue(TYPE, type).setValue(COLOUR, col), 2);
        super.setPlacedBy(world, pos, state, placer, itemStack);
    }

    private net.minecraft.world.level.block.state.BlockState getClosestConnected(Level world, BlockPos pos) {
        for (Direction d : Direction.values()) {
            //
            BlockPos offset = pos.relative(d);
            net.minecraft.world.level.block.state.BlockState here = world.getBlockState(offset);
            if (here.getBlock() == this) {
                return here;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        return new ConveyorBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, net.minecraft.world.level.block.state.BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING).add(SPEED).add(TYPE).add(COLOUR).add(WATERLOGGED);
    }

    @Override
    public net.minecraft.world.level.block.state.BlockState updateShape(net.minecraft.world.level.block.state.BlockState state, Direction direction, net.minecraft.world.level.block.state.BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    @Override
    public net.minecraft.world.level.block.state.BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx)
                .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(net.minecraft.world.level.block.state.BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public RenderShape getRenderShape(net.minecraft.world.level.block.state.BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, net.minecraft.world.level.block.state.BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CyclicBlocks.CONVEYOR.blockEntity(), world.isClientSide ? ConveyorBlockEntity::clientTick : ConveyorBlockEntity::serverTick);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Component.translatable("block.cyclic.conveyor.tooltip").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, world, tooltip, options);
    }

    //TODO wrench mechanics lol
}
