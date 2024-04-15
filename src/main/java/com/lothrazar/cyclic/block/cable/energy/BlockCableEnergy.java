package com.lothrazar.cyclic.block.cable.energy;

import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.block.cable.CableBase;
import com.lothrazar.cyclic.block.cable.EnumConnectType;
import com.lothrazar.cyclic.block.cable.ShapeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import team.reborn.energy.api.EnergyStorage;

public class BlockCableEnergy extends CableBase {
    public BlockCableEnergy(Properties properties) {
        super(properties.strength(0.5F));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return ShapeCache.getOrCreate(state, CableBase::createShape);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileCableEnergy(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CyclicBlocks.ENERGY_PIPE.blockEntity(), world.isClientSide ? TileCableEnergy::clientTick : TileCableEnergy::serverTick);
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
            EnergyStorage energy = facingTile == null ? null : EnergyStorage.SIDED.find(worldIn, pos.relative(d), d.getOpposite());
            if (energy != null) {
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
            //      updateConnection(world, currentPos, facing, oldProp);
            return stateIn;
        }
        if (isEnergy(stateIn, facing, facingState, world, currentPos, facingPos)) {
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
        EnergyStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> {
            if (!CableBase.isCableBlocked(blockEntity.getBlockState(), direction)) {
                return blockEntity.energy;
            }
            return null;
        }), CyclicBlocks.ENERGY_PIPE.blockEntity());
    }
}
