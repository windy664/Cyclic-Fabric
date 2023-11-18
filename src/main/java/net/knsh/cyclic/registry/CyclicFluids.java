package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.fluid.FluidXpJuiceHolder;
import net.knsh.cyclic.fluid.block.XpJuiceFluidBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;

public class CyclicFluids {
    public static FlowingFluid STILL_XP;
    public static FlowingFluid FLOWING_XP;
    public static Block XP_BLOCK;
    public static Item XP_BUCKET;

    public static void register() {
        STILL_XP = Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(Cyclic.MOD_ID + ":block/fluid/xpjuice_still"), new FluidXpJuiceHolder.Still());
        FLOWING_XP = Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(Cyclic.MOD_ID + ":block/fluid/xpjuice_flow"), new FluidXpJuiceHolder.Flowing());
        XP_BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Cyclic.MOD_ID, "xpjuice_block"),
                new XpJuiceFluidBlock(STILL_XP, FabricBlockSettings.of().liquid().lightLevel(s -> 8).strength(100.0F).noLootTable()));
        XP_BUCKET = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Cyclic.MOD_ID, "xpjuice_bucket"),
                new BucketItem(STILL_XP, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    }
}
