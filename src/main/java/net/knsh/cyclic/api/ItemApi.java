package net.knsh.cyclic.api;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.battery.BatteryImplementation;
import net.knsh.cyclic.registry.CyclicItems;
import net.minecraft.resources.ResourceLocation;

public class ItemApi {
    public static final ItemApiLookup<BatteryImplementation, Void> BATTERY_ITEM = ItemApiLookup.get(new ResourceLocation(Cyclic.MOD_ID, "battery"), BatteryImplementation.class, void.class);
    public static final ItemApiLookup<ItemHandlerCap, Void> ITEM_HANDLER = ItemApiLookup.get(new ResourceLocation(Cyclic.MOD_ID, "item_handler"), ItemHandlerCap.class, void.class);

    public static void init() {
        BATTERY_ITEM.registerSelf(CyclicItems.BATTERY);
        ITEM_HANDLER.registerSelf(CyclicItems.CRAFTING_BAG);
    }
}
