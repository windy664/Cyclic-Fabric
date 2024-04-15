package com.lothrazar.cyclic.fluid;

import com.lothrazar.cyclic.registry.CyclicItems;
import io.github.fabricators_of_create.porting_lib.util.SimpleFlowableFluid;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import com.lothrazar.cyclic.Cyclic;
import com.lothrazar.cyclic.fluid.block.HoneyFluidBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

public class FluidHoneyHolder {
    private static final String ID = "honey";
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation("minecraft:block/" + ID + "_block_side");
    public static final ResourceLocation FLUID_STILL = new ResourceLocation("minecraft:block/" + ID + "_block_top");
    public static final int COLOR = 0xFFCE5D;

    public static FlowingFluid STILL = Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(Cyclic.MOD_ID, ID),
            new SimpleFlowableFluid.Still(makeProperties()));
    public static FlowingFluid FLOWING = Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(Cyclic.MOD_ID, ID + "_flowing"),
            new SimpleFlowableFluid.Flowing(makeProperties()));
    public static LiquidBlock BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Cyclic.MOD_ID, ID + "_block"),
            new HoneyFluidBlock(STILL, FabricBlockSettings.create().liquid().noCollission().strength(100.0F).noLootTable()));
    public static Item BUCKET = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Cyclic.MOD_ID, ID + "_bucket"),
            new BucketItem(STILL, new FabricItemSettings().craftRemainder(Items.BUCKET).stacksTo(1)));

    static {
        CyclicItems.INSTANCE.add(BUCKET);
    }

    private static SimpleFlowableFluid.Properties makeProperties() {
        return new SimpleFlowableFluid.Properties(() -> STILL, () -> FLOWING)
                .bucket(() -> BUCKET)
                .block(() -> BLOCK);
    }
}
