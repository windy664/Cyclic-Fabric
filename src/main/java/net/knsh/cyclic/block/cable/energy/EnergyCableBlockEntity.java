package net.knsh.cyclic.block.cable.energy;

import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.block.cable.CableBase;
import net.knsh.cyclic.block.cable.EnumConnectType;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.util.UtilDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraftforge.common.ForgeConfigSpec;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class EnergyCableBlockEntity extends BlockEntityCyclic {
    public static ForgeConfigSpec.IntValue BUFFERSIZE;
    public static ForgeConfigSpec.IntValue TRANSFER_RATE;
    public SimpleEnergyStorage energy = new SimpleEnergyStorage(BUFFERSIZE.get(), BUFFERSIZE.get(), BUFFERSIZE.get()) {
        @Override
        protected void onFinalCommit() {
            EnergyCableBlockEntity.this.setChanged();
        }
    };

    public EnergyCableBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.ENERGY_PIPE.blockEntity(), pos, state);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, EnergyCableBlockEntity e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, EnergyCableBlockEntity e) {
        e.tick();
    }

    //  @Override
    private void tick() {
        tickCableFlow();
        for (final Direction extractSide : Direction.values()) {
            final EnumProperty<EnumConnectType> property = CableBase.FACING_TO_PROPERTY_MAP.get(extractSide);
            final EnumConnectType connection = getBlockState().getValue(property);
            if (connection.isExtraction()) {
                final BlockPos posTarget = this.worldPosition.relative(extractSide);
                final BlockEntity tile = level.getBlockEntity(posTarget);
                if (tile == null) {
                    return;
                }
                final EnergyStorage energyFrom = EnergyStorage.SIDED.find(level, posTarget, extractSide.getOpposite());
                if (energyFrom != null) {
                    EnergyStorageUtil.move(energyFrom, energy, TRANSFER_RATE.get(), null);
                }
            }
        }
    }

    private void tickCableFlow() {
        for (final Direction outgoingSide : UtilDirection.getAllInDifferentOrder()) {
            EnumConnectType connection = this.getBlockState().getValue(CableBase.FACING_TO_PROPERTY_MAP.get(outgoingSide));
            if (connection.isExtraction() || connection.isBlocked()) {
                continue;
            }
            final BlockPos posTarget = this.worldPosition.relative(outgoingSide);
            final EnergyStorage energyTo = EnergyStorage.SIDED.find(level, posTarget, outgoingSide.getOpposite());
            if (energyTo != null) {
                EnergyStorageUtil.move(energy, energyTo, TRANSFER_RATE.get(), null);
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        energy.amount = tag.getLong("amount");
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putLong("amount", energy.amount);
        super.saveAdditional(tag);
    }

    @Override
    public void setField(int field, int value) {}

    @Override
    public int getField(int field) {
        return 0;
    }
}
