package com.lothrazar.cyclic.registry;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public class CyclicBlockEntities {
    public static void register() {
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getStorage(), CyclicBlocks.HOPPERGOLD.blockEntity());
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getStorage(), CyclicBlocks.HOPPER.blockEntity());

        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getStorage(), CyclicBlocks.FLUIDHOPPER.blockEntity());
        FluidStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.flow.get(direction)), CyclicBlocks.FLUID_PIPE.blockEntity());
    }
}
