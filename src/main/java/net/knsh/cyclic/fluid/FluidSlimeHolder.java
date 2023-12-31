package net.knsh.cyclic.fluid;

import io.github.fabricators_of_create.porting_lib.util.SimpleFlowableFluid;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.fluid.block.HoneyFluidBlock;
import net.knsh.cyclic.fluid.block.SlimeFluidBlock;
import net.knsh.cyclic.registry.CyclicItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

public class FluidSlimeHolder {
    private static final String ID = "slime";
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation("minecraft:block/" + ID + "_block");
    public static final ResourceLocation FLUID_STILL = new ResourceLocation("minecraft:block/" + ID + "_block");
    public static final int COLOR = 0x51A03E;

    public static FlowingFluid STILL = Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(Cyclic.MOD_ID, ID),
            new SlimeFluidBlock.Source(makeProperties()));
    public static FlowingFluid FLOWING = Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(Cyclic.MOD_ID, ID + "_flowing"),
            new SlimeFluidBlock.Flowing(makeProperties()));
    public static LiquidBlock BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Cyclic.MOD_ID, ID + "_block"),
            new SlimeFluidBlock(STILL, FabricBlockSettings.create().liquid().noCollission().strength(100.0F).noLootTable()));
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
