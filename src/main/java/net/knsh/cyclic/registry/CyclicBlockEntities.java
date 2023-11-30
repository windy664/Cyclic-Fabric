package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import team.reborn.energy.api.EnergyStorage;

public class CyclicBlockEntities {
    public static void register() {
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getStorage(), CyclicBlocks.HOPPERGOLD.blockEntity());
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getStorage(), CyclicBlocks.HOPPER.blockEntity());
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getStorage(), CyclicBlocks.FLUIDHOPPER.blockEntity());
        EnergyStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.getEnergy()), CyclicBlocks.CRAFTER.blockEntity());
        ItemStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.getFlow().get(direction)), CyclicBlocks.ITEM_PIPE.blockEntity());
    }
}
