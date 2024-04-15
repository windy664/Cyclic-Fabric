package com.lothrazar.cyclic.lookups;

import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.lookups.types.FluidLookup;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.registry.CyclicItems;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class CyclicLookup {
    public static final BlockApiLookup<FluidLookup, Void> FLUID_HANDLER = BlockApiLookup.get(new ResourceLocation(ModCyclic.MODID, "fluid_handler"), FluidLookup.class, void.class);
    public static final BlockApiLookup<SlottedStackStorage, Void> ITEM_HANDLER = BlockApiLookup.get(new ResourceLocation(ModCyclic.MODID, "item_handler"), SlottedStackStorage.class, void.class);
    public static final BlockApiLookup<SlottedStackStorage, Direction> ITEM_HANDLER_SIDED = BlockApiLookup.get(new ResourceLocation(ModCyclic.MODID, "item_handler_sided"), SlottedStackStorage.class, Direction.class);

    public static void init() {
        CyclicBlocks.BLOCK_INSTANCE.forEach(block -> {
            if (block instanceof Lookup lookup) lookup.registerLookups();
        });

        CyclicItems.INSTANCE.forEach(itemLike -> {
            if (itemLike instanceof Lookup lookup) lookup.registerLookups();
        });
    }
}
