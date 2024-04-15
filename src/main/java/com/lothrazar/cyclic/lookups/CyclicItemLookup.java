package com.lothrazar.cyclic.lookups;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.block.battery.BatteryImplementation;
import com.lothrazar.cyclic.lookups.types.FluidLookup;
import com.lothrazar.cyclic.registry.CyclicItems;
import net.minecraft.resources.ResourceLocation;

public class CyclicItemLookup {
    public static final ItemApiLookup<BatteryImplementation, Void> BATTERY_ITEM = ItemApiLookup.get(new ResourceLocation(ModCyclic.MODID, "battery"), BatteryImplementation.class, void.class);
    public static final ItemApiLookup<SlottedStackStorage, Void> ITEM_HANDLER = ItemApiLookup.get(new ResourceLocation(ModCyclic.MODID, "item_handler"), SlottedStackStorage.class, void.class);
    public static final ItemApiLookup<FluidLookup, Void> FLUID_HANDLER = ItemApiLookup.get(new ResourceLocation(ModCyclic.MODID, "fluid_handler"), FluidLookup.class, void.class);

    public static void init() {
        BATTERY_ITEM.registerSelf(CyclicItems.BATTERY);
    }
}
