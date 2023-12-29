package net.knsh.cyclic.library.cap;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class CustomEnergyStorageUtil {
    public static final String NBTENERGY = "energy";

    public static void setEnergy(long energy, EnergyStorage storage) {
        if (energy < 0) {
            energy = 0;
        }
        if (energy > storage.getCapacity()) {
            energy = storage.getAmount();
        }
        try (Transaction transaction = Transaction.openOuter()) {
            storage.insert(energy - storage.getAmount(), transaction);
            transaction.commit();
        }
    }

    public static CompoundTag serializeNBT(SimpleEnergyStorage storage) {
        CompoundTag tag = new CompoundTag();
        tag.putLong(NBTENERGY, storage.getAmount());
        return tag;
    }

    public static void deserializeNBT(net.minecraft.nbt.Tag nbt, SimpleEnergyStorage storage) {
        CompoundTag real = (CompoundTag) nbt;
        storage.amount = real.getLong(NBTENERGY);
    }
}
