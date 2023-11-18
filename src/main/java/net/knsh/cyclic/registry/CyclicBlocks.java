package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.anvilvoid.AnvilVoidBlock;
import net.knsh.cyclic.block.anvilvoid.AnvilVoidBlockEntity;
import net.knsh.cyclic.block.conveyor.ConveyorBlock;
import net.knsh.cyclic.block.conveyor.ConveyorBlockEntity;
import net.knsh.cyclic.block.hopper.SimpleHopperBlock;
import net.knsh.cyclic.block.hopper.SimpleHopperBlockEntity;
import net.knsh.cyclic.block.hopperfluid.FluidHopperBlock;
import net.knsh.cyclic.block.hopperfluid.FluidHopperBlockEntity;
import net.knsh.cyclic.block.trash.TrashBlock;
import net.knsh.cyclic.block.trash.TrashBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CyclicBlocks {
    public static ItemBlockEntity<ConveyorBlockEntity> CONVEYOR = registerBlockEntity("conveyor", ConveyorBlockEntity::new, new ConveyorBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<AnvilVoidBlockEntity> ANVILVOID = registerBlockEntity("anvil_void", AnvilVoidBlockEntity::new, new AnvilVoidBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<TrashBlockEntity> TRASH = registerBlockEntity("trash", TrashBlockEntity::new, new TrashBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<SimpleHopperBlockEntity> HOPPER = registerBlockEntity("hopper", SimpleHopperBlockEntity::new, new SimpleHopperBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<FluidHopperBlockEntity> FLUIDHOPPER = registerBlockEntity("hopper_fluid", FluidHopperBlockEntity::new, new FluidHopperBlock(FabricBlockSettings.create()));

    public static void register() {}

    private static Block registerBlock(String id, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Cyclic.MOD_ID, id), block);
    }

    private static <T extends BlockEntity> ItemBlockEntity<T> registerBlockEntity(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        return new ItemBlockEntity(
                Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(Cyclic.MOD_ID, id), FabricBlockEntityTypeBuilder.create(factory, block).build()),
                registerBlock(id, block),
                Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Cyclic.MOD_ID, id), new BlockItem(block, new FabricItemSettings()))
        );
    }

    public record ItemBlockEntity<T extends BlockEntity>(BlockEntityType<T> blockEntity, Block block, Item item) {}
}
