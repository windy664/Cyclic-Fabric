package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.knsh.cyclic.Cyclic;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CyclicTabGroups {
    public static final CreativeModeTab TAB_ITEMS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CyclicItems.GEM_AMBER))
            .title(Component.translatable("itemGroup." + Cyclic.MOD_ID + "items"))
            .displayItems((displayContext, entries) -> CyclicItems.INSTANCE.forEach(entries::accept))
            .build();

    public static final CreativeModeTab TAB_BLOCKS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CyclicBlocks.TRASH.block()))
            .title(Component.translatable("itemGroup." + Cyclic.MOD_ID))
            .displayItems(((displayContext, entries) -> CyclicBlocks.ITEM_INSTANCE.forEach(entries::accept)))
            .build();

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(Cyclic.MOD_ID, "item_group"), TAB_ITEMS);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(Cyclic.MOD_ID, "block_group"), TAB_BLOCKS);
    }
}
