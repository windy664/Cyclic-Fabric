package net.knsh.cyclic.lookups;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.lookups.types.FluidLookup;
import net.knsh.cyclic.lookups.types.ItemHandlerLookup;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class CyclicLookup {
    public static final BlockApiLookup<FluidLookup, Void> FLUID_HANDLER = BlockApiLookup.get(new ResourceLocation(Cyclic.MOD_ID, "fluid_handler"), FluidLookup.class, void.class);
    public static final BlockApiLookup<ItemHandlerLookup, Void> ITEM_HANDLER = BlockApiLookup.get(new ResourceLocation(Cyclic.MOD_ID, "item_handler"), ItemHandlerLookup.class, void.class);
    public static final BlockApiLookup<ItemHandlerLookup, Direction> ITEM_HANDLER_SIDED = BlockApiLookup.get(new ResourceLocation(Cyclic.MOD_ID, "item_handler_sided"), ItemHandlerLookup.class, Direction.class);

    public static void init() {
        CyclicBlocks.BLOCK_INSTANCE.forEach(block -> {
            if (block instanceof Lookup lookup) lookup.registerLookups();
        });
    }
}
