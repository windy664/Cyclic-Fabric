package net.knsh.cyclic.block.cable.energy;

import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.block.cable.CableBase;
import net.knsh.cyclic.block.cable.EnumConnectType;
import net.knsh.cyclic.library.cap.CustomEnergyStorageUtil;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.util.UtilDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TileCableEnergy extends BlockEntityCyclic {
    public static IntValue BUFFERSIZE;
    public static IntValue TRANSFER_RATE;
    SimpleEnergyStorage energy;
    private final Map<Direction, Integer> mapIncomingEnergy = new ConcurrentHashMap<>();
    private int energyLastSynced = -1;

    public TileCableEnergy(BlockPos pos, BlockState state) {
        super(CyclicBlocks.ENERGY_PIPE.blockEntity(), pos, state);
        for (Direction f : Direction.values()) {
            mapIncomingEnergy.put(f, 0);
        }
        energy = new SimpleEnergyStorage(BUFFERSIZE.get(), BUFFERSIZE.get(), BUFFERSIZE.get()) {
            @Override
            protected void onFinalCommit() {
                TileCableEnergy.this.setChanged();
            }
        };
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileCableEnergy e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileCableEnergy e) {
        e.tick();
    }

    public void tick() {
        this.tickDownIncomingPowerFaces();
        this.tickCableFlow();
        for (final Direction extractSide : Direction.values()) {
            final EnumProperty<EnumConnectType> property = CableBase.FACING_TO_PROPERTY_MAP.get(extractSide);
            final EnumConnectType connection = getBlockState().getValue(property);
            if (connection.isExtraction()) {
                tryExtract(extractSide);
            }
        }
    }

    private void tryExtract(Direction extractSide) {
        if (extractSide == null) {
            return;
        }
        final BlockPos posTarget = this.worldPosition.relative(extractSide);
        final BlockEntity tile = level.getBlockEntity(posTarget);
        if (tile == null) {
            return;
        }
        final EnergyStorage itemHandlerFrom = EnergyStorage.SIDED.find(level, posTarget, extractSide.getOpposite());
        if (itemHandlerFrom == null) {
            return;
        }
        final long capacity = energy.getCapacity() - energy.getAmount();
        if (capacity <= 0) {
            return;
        }
        long amountTransfered = EnergyStorageUtil.move(
                itemHandlerFrom,
                energy,
                TRANSFER_RATE.get(),
                null
        );
    }

    private void tickCableFlow() {
        for (final Direction outgoingSide : UtilDirection.getAllInDifferentOrder()) {
            EnumConnectType connection = this.getBlockState().getValue(CableBase.FACING_TO_PROPERTY_MAP.get(outgoingSide));
            if (connection.isExtraction() || connection.isBlocked()) {
                continue;
            }
            if (!this.isEnergyIncomingFromFace(outgoingSide)) {
                moveEnergy(outgoingSide, TRANSFER_RATE.get(), this.getBlockState(), this);
            }
        }
    }

    public void tickDownIncomingPowerFaces() {
        for (final Direction incomingDirection : Direction.values()) {
            mapIncomingEnergy.computeIfPresent(incomingDirection, (direction, amount) -> {
                if (amount > 0) {
                    amount -= 1;
                }
                return amount;
            });
        }
    }

    @Override
    public void load(CompoundTag tag) {
        for (Direction f : Direction.values()) {
            mapIncomingEnergy.put(f, tag.getInt(f.getSerializedName() + "_incenergy"));
        }
        CustomEnergyStorageUtil.deserializeNBT(tag.getCompound(NBTENERGY), energy);
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        for (Direction f : Direction.values()) {
            tag.putInt(f.getSerializedName() + "_incenergy", mapIncomingEnergy.get(f));
        }
        tag.put(NBTENERGY, CustomEnergyStorageUtil.serializeNBT(energy));
        super.saveAdditional(tag);
    }

    private static final int TIMER_SIDE_INPUT = 15;

    private boolean isEnergyIncomingFromFace(Direction face) {
        return mapIncomingEnergy.get(face) > 0;
    }

    public void updateIncomingEnergyFace(Direction inputFrom) {
        mapIncomingEnergy.put(inputFrom, TIMER_SIDE_INPUT);
    }

    @Override
    public void setField(int field, int value) {}

    @Override
    public int getField(int field) {
        return 0;
    }
}
