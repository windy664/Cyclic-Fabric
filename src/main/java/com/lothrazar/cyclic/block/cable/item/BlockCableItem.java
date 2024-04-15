package com.lothrazar.cyclic.block.cable.item;

import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.registry.CyclicScreens;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import com.lothrazar.cyclic.Cyclic;
import com.lothrazar.cyclic.block.cable.CableBase;
import com.lothrazar.cyclic.block.cable.EnumConnectType;
import com.lothrazar.cyclic.block.cable.ShapeCache;
import com.lothrazar.cyclic.lookups.CyclicLookup;
import com.lothrazar.cyclic.lookups.Lookup;
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
import org.jetbrains.annotations.NotNull;

public class BlockCableItem extends CableBase implements Lookup {
    public BlockCableItem(Properties properties) {
        super(properties.strength(0.5F));
    }

    @Override
    public void registerClient() {
        MenuScreens.register(CyclicScreens.ITEM_PIPE, ScreenCableItem::new);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context
    ) {
        return ShapeCache.getOrCreate(state, CableBase::createShape);
    }

    @SuppressWarnings("UnstableApiUsage")
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
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileCableItem(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(
                type,
                CyclicBlocks.ITEM_PIPE.blockEntity(),
                world.isClientSide ? TileCableItem::clientTick : TileCableItem::serverTick
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void setPlacedBy(
            @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState stateIn, LivingEntity placer, @NotNull ItemStack stack
    ) {
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

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(
            BlockState stateIn,
            @NotNull Direction facing,
            @NotNull BlockState facingState,
            @NotNull LevelAccessor world,
            @NotNull BlockPos currentPos,
            @NotNull BlockPos facingPos
    ) {
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

    @SuppressWarnings("UnstableApiUsage")
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
