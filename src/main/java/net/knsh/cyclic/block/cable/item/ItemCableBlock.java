package net.knsh.cyclic.block.cable.item;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.knsh.cyclic.block.cable.CableBase;
import net.knsh.cyclic.block.cable.EnumConnectType;
import net.knsh.cyclic.block.cable.ShapeCache;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ItemCableBlock extends CableBase {
    private final BlockApiLookup<Storage<ItemVariant>, @Nullable Direction> blockApiLookup = ItemStorage.SIDED;
    public ItemCableBlock(Properties settings) {
        super(settings.strength(0.5F));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ShapeCache.getOrCreate(state, CableBase::createShape);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            ItemCableBlockEntity tileentity = (ItemCableBlockEntity) worldIn.getBlockEntity(pos);
            if (tileentity != null) {
                //if (tileentity.filter != null) {
                //    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tileentity.filter.getStackInSlot(0));
                //}
                for (Direction dir : Direction.values()) {
                    Storage<ItemVariant> items = blockApiLookup.find(worldIn, pos.relative(dir), dir.getOpposite());
                    if (items != null) {
                        items.forEach((itemVariantStorageView -> {
                            Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemVariantStorageView.getResource().toStack());
                        }));
                    }
                }
            }
            worldIn.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ItemCableBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CyclicBlocks.ITEM_PIPE.blockEntity(), world.isClientSide ? ItemCableBlockEntity::clientTick : ItemCableBlockEntity::serverTick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        for (Direction d : Direction.values()) {
            BlockEntity facingTile = level.getBlockEntity(pos.relative(d));
            Storage<ItemVariant> cap = facingTile == null ? null : blockApiLookup.find(level, facingTile.getBlockPos(), d.getOpposite());
            if (cap != null) {
                state = state.setValue(FACING_TO_PROPERTY_MAP.get(d), EnumConnectType.INVENTORY);
                level.setBlockAndUpdate(pos, state);
            }
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        EnumProperty<EnumConnectType> property = FACING_TO_PROPERTY_MAP.get(facing);
        EnumConnectType oldProp = stateIn.getValue(property);
        if (oldProp.isBlocked() || oldProp.isExtraction()) {
            //  updateConnection(world, currentPos, facing, oldProp);
            return stateIn;
        }

        if (isItem(stateIn, facing, facingState, world, currentPos, facingPos)) {
            BlockState with = stateIn.setValue(property, EnumConnectType.INVENTORY);
            if (world instanceof Level && world.getBlockState(currentPos).getBlock() == this) {
                //hack to force {any} -> inventory IF its here
                ((Level) world).setBlockAndUpdate(currentPos, with);
            }
            return with;
        }
        else {
            return stateIn.setValue(property, EnumConnectType.NONE);
        }
    }

    private boolean isItem(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (facing == null) {
            return false;
        }
        BlockEntity neighbor = world.getBlockEntity(facingPos);
        if (world.isClientSide() || neighbor == null) return false;
        Storage<ItemVariant> cap = blockApiLookup.find(world.getServer().getLevel(Level.OVERWORLD), neighbor.getBlockPos(), facing.getOpposite());
        if (cap != null) {
            return true;
        }
        return false;
    }
}
