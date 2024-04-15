package com.lothrazar.cyclic.registry;

import com.lothrazar.cyclic.block.battery.BatteryBlockItem;
import com.lothrazar.cyclic.block.tank.ItemBlockTank;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import com.lothrazar.cyclic.Cyclic;
import com.lothrazar.cyclic.block.cable.CableWrench;
import com.lothrazar.cyclic.block.expcollect.ExpItemGain;
import com.lothrazar.cyclic.item.GemstoneItem;
import com.lothrazar.cyclic.item.ItemCyclic;
import com.lothrazar.cyclic.item.crafting.CraftingBagItem;
import com.lothrazar.cyclic.item.crafting.simple.CraftingStickItem;
import com.lothrazar.cyclic.item.datacard.EntityDataCard;
import com.lothrazar.cyclic.item.datacard.LocationGpsCard;
import com.lothrazar.cyclic.item.datacard.filter.FilterCardItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Collection;

public class CyclicItems {
    public static Collection<ItemLike> INSTANCE = new ArrayList<>();

    public static Item
        GEM_AMBER = registerItem("gem_amber", new GemstoneItem(new FabricItemSettings())),
        CABLE_WRENCH = registerItem("cable_wrench", new CableWrench(new FabricItemSettings())),
        BATTERY = registerItem("battery", new BatteryBlockItem(CyclicBlocks.BATTERY.block(), new FabricItemSettings())),
        ENTITY_DATA = registerItem("entity_data", new EntityDataCard(new FabricItemSettings())),
        LOCATION_DATA = registerItem("location_data", new LocationGpsCard(new FabricItemSettings())),
        FILTER_DATA = registerItem("filter_data", new FilterCardItem(new FabricItemSettings())),
        CRAFTING_BAG = registerItem("crafting_bag", new CraftingBagItem(new FabricItemSettings().stacksTo(1))),
        CRAFTING_STICK = registerItem("crafting_stick", new CraftingStickItem(new FabricItemSettings().stacksTo(1))),
        BIOMASS = registerItem("biomass", new ItemCyclic(new FabricItemSettings())),
        EXPERIENCE_FOOD = registerItem("experience_food", new ExpItemGain(new FabricItemSettings())),
        TANK = registerBlockItem("tank", new ItemBlockTank(CyclicBlocks.TANK.block(), new FabricItemSettings()));

    private static Item registerItem(String id, Item item) {
        Item registeredItem = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Cyclic.MOD_ID, id), item);
        INSTANCE.add(registeredItem);
        return registeredItem;
    }

    private static Item registerBlockItem(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Cyclic.MOD_ID, id), item);
    }

    public static void register() {}
}
