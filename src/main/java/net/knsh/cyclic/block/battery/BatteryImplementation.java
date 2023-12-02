package net.knsh.cyclic.block.battery;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import team.reborn.energy.api.EnergyStorage;

public interface BatteryImplementation extends EnergyStorage {
    String NBTENERGY = "energy";

    BatteryImplementation getEnergy();

    default void setEnergy(long energyIn) {
        if (energyIn < 0) {
            energyIn = 0;
        }
        if (energyIn > getCapacity()) {
            energyIn = getAmount();
        }
        try (Transaction transaction = Transaction.openOuter()) {
            this.getEnergy().insert(energyIn, transaction);
            transaction.commit();
        }
    }

    default CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong(NBTENERGY, getAmount());
        return tag;
    }

    default void deserializeNBT(net.minecraft.nbt.Tag nbt) {
        CompoundTag real = (CompoundTag) nbt;
        setEnergy(real.getLong(NBTENERGY));
    }
}
