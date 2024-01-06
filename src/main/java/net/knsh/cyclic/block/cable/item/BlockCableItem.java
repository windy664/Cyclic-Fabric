package net.knsh.cyclic.block.cable.item;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.cable.CableBase;
import net.knsh.cyclic.block.cable.EnumConnectType;
import net.knsh.cyclic.block.cable.ShapeCache;
import net.knsh.cyclic.lookups.CyclicLookup;
import net.knsh.cyclic.lookups.Lookup;
import net.knsh.cyclic.lookups.types.ItemHandlerLookup;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCableItem extends CableBase implements Lookup {
    public BlockCableItem(Properties properties) {
        super(properties.strength(0.5F));
    }

    @Override
    public void registerClient() {
        MenuScreens.register(CyclicScreens.ITEM_PIPE, ScreenCableItem::new);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return ShapeCache.getOrCreate(state, CableBase::createShape);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileCableItem tileentity = (TileCableItem) worldIn.getBlockEntity(pos);
            if (tileentity != null) {
                if (tileentity.filter != null) {
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tileentity.filter.getStackInSlot(0));
                }
                for (Direction dir : Direction.values()) {
                    SlottedStackStorage items = CyclicLookup.ITEM_HANDLER_SIDED.find(worldIn, pos, dir);
                    if (items != null) {
                        for (int i = 0; i < items.getSlotCount(); ++i) {
                            Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), items.getStackInSlot(i));
                        }
                    }
                }
            }
            worldIn.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileCableItem(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CyclicBlocks.ITEM_PIPE.blockEntity(), world.isClientSide ? TileCableItem::clientTick : TileCableItem::serverTick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState stateIn, LivingEntity placer, ItemStack stack) {
        for (Direction d : Direction.values()) {
            BlockEntity facingTile = worldIn.getBlockEntity(pos.relative(d));

            Storage<ItemVariant> cap = facingTile == null ? null : ItemStorage.SIDED.find(worldIn, pos.relative(d), d.getOpposite());
            if (cap != null) {
                stateIn = stateIn.setValue(FACING_TO_PROPERTY_MAP.get(d), EnumConnectType.INVENTORY);
                worldIn.setBlockAndUpdate(pos, stateIn);
            }
        }
        super.setPlacedBy(worldIn, pos, stateIn, placer, stack);
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

    @Override
    public void registerLookups() {
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            if (!CableBase.isCableBlocked(blockEntity.getBlockState(), direction)) {
                return blockEntity.flow.get(direction);
            }
            Cyclic.LOGGER.info("GAB!");
            return null;
        }, CyclicBlocks.ITEM_PIPE.blockEntity());
    }
}
