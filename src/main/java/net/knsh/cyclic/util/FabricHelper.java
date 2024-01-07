package net.knsh.cyclic.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class FabricHelper {
    public static long toMiliBuckets(long droplets) {
        return Math.round((float) droplets / 81);
    }

    public static long toDroplets(long miliBuckets) {
        return miliBuckets * 81;
    }

    public static <V> int getBurnTime(V object) {
        var burnTime = AbstractFurnaceBlockEntity.getFuel().get(object);
        return burnTime != null ? burnTime : 0;
    }

    //public static void readTank(CompoundTag tag, SingleVariantStorage<FluidVariant> )
}
