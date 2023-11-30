package net.knsh.cyclic.block.cable.energy;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.knsh.cyclic.block.cable.CableBase;
import net.knsh.cyclic.block.cable.EnumConnectType;
import net.knsh.cyclic.block.cable.ShapeCache;
import net.knsh.cyclic.registry.CyclicBlocks;
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
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class EnergyCableBlock extends CableBase {
    public EnergyCableBlock(Properties settings) {
        super(settings.strength(0.5F));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return ShapeCache.getOrCreate(state, CableBase::createShape);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyCableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, CyclicBlocks.ENERGY_PIPE.blockEntity(), level.isClientSide ? EnergyCableBlockEntity::clientTick : EnergyCableBlockEntity::serverTick);
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
            EnergyStorage energy = facingTile == null ? null : EnergyStorage.SIDED.find(level, facingTile.getBlockPos(), d.getOpposite());
            if (energy != null) {
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

    private boolean isEnergy(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (facing == null) {
            return false;
        }
        BlockEntity neighbor = world.getBlockEntity(facingPos);
        if (world.isClientSide() || neighbor == null) return false;
        EnergyStorage cap = EnergyStorage.SIDED.find(world.getServer().getLevel(Level.OVERWORLD), neighbor.getBlockPos(), facing.getOpposite());
        if (cap != null) {
            return true;
        }
        return false;
    }
}
