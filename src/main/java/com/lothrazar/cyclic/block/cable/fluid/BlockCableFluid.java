package com.lothrazar.cyclic.block.cable.fluid;

import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.registry.CyclicScreens;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import com.lothrazar.cyclic.block.cable.CableBase;
import com.lothrazar.cyclic.block.cable.EnumConnectType;
import com.lothrazar.cyclic.block.cable.ShapeCache;
import com.lothrazar.cyclic.lookups.Lookup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

import java.util.List;

public class BlockCableFluid extends CableBase implements Lookup {
    public BlockCableFluid(Properties properties) {
        super(properties.strength(0.5F));
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("block.cyclic.fluid_pipe.tooltip0").withStyle(ChatFormatting.GRAY));
        }
        else {
            tooltip.add(Component.translatable("item.cyclic.shift").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return ShapeCache.getOrCreate(state, CableBase::createShape);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileCableFluid(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CyclicBlocks.FLUID_PIPE.blockEntity(), world.isClientSide ? TileCableFluid::clientTick : TileCableFluid::serverTick);
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
            Storage<FluidVariant> cap = facingTile == null ? null : FluidStorage.SIDED.find(worldIn, pos.relative(d), d.getOpposite());
            if (cap != null) {
                stateIn = stateIn.setValue(FACING_TO_PROPERTY_MAP.get(d), EnumConnectType.INVENTORY);
                worldIn.setBlockAndUpdate(pos, stateIn);
            }
        }
        super.setPlacedBy(worldIn, pos, stateIn, placer, stack);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileCableFluid tileentity = (TileCableFluid) worldIn.getBlockEntity(pos);
            if (tileentity != null && tileentity.filter != null) {
                Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tileentity.filter.getStackInSlot(0));
            }
            worldIn.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        EnumProperty<EnumConnectType> property = FACING_TO_PROPERTY_MAP.get(facing);
        EnumConnectType oldProp = stateIn.getValue(property);
        if (oldProp.isBlocked() || oldProp.isExtraction()) {
            //  updateConnection(world, currentPos, facing, oldProp);
            return stateIn;
        }
        if (isFluid(stateIn, facing, facingState, world, currentPos, facingPos)) {
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
    public void registerClient() {
        MenuScreens.register(CyclicScreens.FLUID_PIPE, ScreenCableFluid::new);
    }

    @Override
    public void registerLookups() {
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            if (!CableBase.isCableBlocked(blockEntity.getBlockState(), direction)) {
                return blockEntity.flow.get(direction);
            }
            return null;
        }, CyclicBlocks.FLUID_PIPE.blockEntity());
    }
}
