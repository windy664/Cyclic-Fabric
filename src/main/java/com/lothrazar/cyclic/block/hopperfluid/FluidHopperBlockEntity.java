package com.lothrazar.cyclic.block.hopperfluid;

import com.lothrazar.cyclic.block.BlockEntityCyclic;
import com.lothrazar.cyclic.block.hopper.SimpleHopperBlock;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FluidHopperBlockEntity extends BlockEntityCyclic {
    public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidConstants.BUCKET;
        }

        @Override
        protected void onFinalCommit() {
            FluidHopperBlockEntity.this.setChanged();
        }
    };
    private final BlockApiLookup<Storage<FluidVariant>, @Nullable Direction> blockApiLookup = FluidStorage.SIDED;

    public FluidHopperBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.FLUIDHOPPER.blockEntity(), pos, state);
    }

    public Storage<FluidVariant> getStorage() {
        return fluidStorage;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("fluidVariant", fluidStorage.variant.toNbt());
        tag.putLong("amount", fluidStorage.amount);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidStorage.variant = FluidVariant.fromNbt(tag.getCompound("fluidVariant"));
        fluidStorage.amount = tag.getLong("amount");
    }

    public Direction getTopDirection(BlockState state) {
        return Direction.UP;
    }

    public Direction getBottomDirection(BlockState state) {
        return state.getValue(SimpleHopperBlock.FACING);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, FluidHopperBlockEntity e) {
        e.tick(level, blockPos, blockState);
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, FluidHopperBlockEntity e) {}

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.isPowered()) {
            return;
        }

        boolean bl;
        bl = extract(level, blockPos, blockState);
        bl |= insert(level, blockPos, blockState);

        if (bl) {
            setChanged(level, blockPos, blockState);
        }
    }

    protected boolean insert(Level level, BlockPos blockPos, BlockState blockState) {
        Direction direction = getBottomDirection(blockState);
        BlockPos targetPos = blockPos.relative(direction);
        Storage<FluidVariant> target = blockApiLookup.find(level, targetPos, direction.getOpposite());

        if (target != null) {
            BlockEntity blockEntityTarget = level.getBlockEntity(targetPos);
            boolean targetIsEmpty = StorageUtil.findStoredResource(target) == null;
            return StorageUtil.move(
                    getStorage(),
                    target,
                    itemVariant -> true,
                    FluidConstants.BUCKET,
                    null
            ) == 1;
        }
        return false;
    }

    protected boolean extract(Level level, BlockPos blockPos, BlockState blockState) {
        Direction direction = getTopDirection(blockState);
        BlockPos sourcePos = blockPos.relative(direction);
        Storage<FluidVariant> source = blockApiLookup.find(level, sourcePos, direction.getOpposite());

        if (source != null) {
            long moved = StorageUtil.move(
                    source,
                    getStorage(),
                    itemVariant -> true,
                    FluidConstants.BUCKET,
                    null
            );
            return moved >= 1;
        } else {
            return false;
        }
    }

    @Override
    public void setField(int field, int value) {}

    @Override
    public int getField(int field) {
        return 0;
    }
}
