package net.knsh.cyclic.lookups;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.battery.BatteryImplementation;
import net.knsh.cyclic.lookups.types.FluidLookup;
import net.knsh.cyclic.lookups.types.ItemHandlerLookup;
import net.knsh.cyclic.registry.CyclicItems;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class CyclicItemLookup {
    public static final ItemApiLookup<BatteryImplementation, Void> BATTERY_ITEM = ItemApiLookup.get(new ResourceLocation(Cyclic.MOD_ID, "battery"), BatteryImplementation.class, void.class);
    public static final ItemApiLookup<SlottedStackStorage, Void> ITEM_HANDLER = ItemApiLookup.get(new ResourceLocation(Cyclic.MOD_ID, "item_handler"), SlottedStackStorage.class, void.class);
    public static final ItemApiLookup<FluidLookup, Void> FLUID_HANDLER = ItemApiLookup.get(new ResourceLocation(Cyclic.MOD_ID, "fluid_handler"), FluidLookup.class, void.class);

    public static void init() {
        BATTERY_ITEM.registerSelf(CyclicItems.BATTERY);
    }
}
