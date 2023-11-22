package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.knsh.cyclic.Cyclic;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CyclicTabGroups {
    public static final CreativeModeTab TAB_ITEMS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CyclicItems.GEM_AMBER))
            .title(Component.translatable("itemGroup." + Cyclic.MOD_ID + "items"))
            .displayItems((displayContext, entries) -> {
                entries.accept(CyclicItems.GEM_AMBER);
            })
            .build();

    public static final CreativeModeTab TAB_BLOCKS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CyclicBlocks.TRASH.block()))
            .title(Component.translatable("itemGroup." + Cyclic.MOD_ID))
            .displayItems(((displayContext, entries) -> {
                entries.accept(CyclicBlocks.CONVEYOR.block());
                entries.accept(CyclicBlocks.ANVILVOID.block());
                entries.accept(CyclicBlocks.TRASH.block());
                entries.accept(CyclicBlocks.HOPPER.block());
                entries.accept(CyclicBlocks.FLUIDHOPPER.block());
                entries.accept(CyclicBlocks.HOPPERGOLD.block());
                entries.accept(CyclicBlocks.GENERATOR_FUEL.block());
            }))
            .build();

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(Cyclic.MOD_ID, "item_group"), TAB_ITEMS);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(Cyclic.MOD_ID, "block_group"), TAB_BLOCKS);
    }
}
