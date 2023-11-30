package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.cable.CableWrench;
import net.knsh.cyclic.item.GemstoneItem;
import net.knsh.cyclic.item.datacard.EntityDataCard;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Collection;

public class CyclicItems {
    public static Collection<ItemLike> INSTANCE = new ArrayList<>();

    public static Item GEM_AMBER = registerItem("gem_amber", new GemstoneItem(new FabricItemSettings()));
    public static Item ENTITY_DATA = registerItem("entity_data", new EntityDataCard(new FabricItemSettings()));
    public static Item CABLE_WRENCH = registerItem("cable_wrench", new CableWrench(new FabricItemSettings()));

    private static Item registerItem(String id, Item item) {
        Item registeredItem = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Cyclic.MOD_ID, id), item);
        INSTANCE.add(registeredItem);
        return registeredItem;
    }

    public static void register() {}
}
