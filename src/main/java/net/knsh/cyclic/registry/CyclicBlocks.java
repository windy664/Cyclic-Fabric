package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.antipotion.AntiBeaconBlock;
import net.knsh.cyclic.block.antipotion.AntiBeaconBlockEntity;
import net.knsh.cyclic.block.antipotion.MilkSpongeBlock;
import net.knsh.cyclic.block.anvil.AnvilAutoBlock;
import net.knsh.cyclic.block.anvil.AnvilAutoBlockEntity;
import net.knsh.cyclic.block.anvilmagma.AnvilMagmaBlock;
import net.knsh.cyclic.block.anvilmagma.AnvilMagmaBlockEntity;
import net.knsh.cyclic.block.anvilvoid.AnvilVoidBlock;
import net.knsh.cyclic.block.anvilvoid.AnvilVoidBlockEntity;
import net.knsh.cyclic.block.battery.BatteryBlock;
import net.knsh.cyclic.block.battery.BatteryBlockEntity;
import net.knsh.cyclic.block.beaconpotion.BeaconPotionBlock;
import net.knsh.cyclic.block.beaconpotion.BeaconPotionBlockEntity;
import net.knsh.cyclic.block.cable.energy.BlockCableEnergy;
import net.knsh.cyclic.block.cable.energy.TileCableEnergy;
import net.knsh.cyclic.block.cable.fluid.BlockCableFluid;
import net.knsh.cyclic.block.cable.fluid.TileCableFluid;
import net.knsh.cyclic.block.cable.item.BlockCableItem;
import net.knsh.cyclic.block.cable.item.TileCableItem;
import net.knsh.cyclic.block.conveyor.ConveyorBlock;
import net.knsh.cyclic.block.conveyor.ConveyorBlockEntity;
import net.knsh.cyclic.block.crafter.CrafterBlock;
import net.knsh.cyclic.block.crafter.CrafterBlockEntity;
import net.knsh.cyclic.block.generatorfuel.BlockGeneratorFuel;
import net.knsh.cyclic.block.generatorfuel.TileGeneratorFuel;
import net.knsh.cyclic.block.hopper.SimpleHopperBlock;
import net.knsh.cyclic.block.hopper.SimpleHopperBlockEntity;
import net.knsh.cyclic.block.hopperfluid.FluidHopperBlock;
import net.knsh.cyclic.block.hopperfluid.FluidHopperBlockEntity;
import net.knsh.cyclic.block.hoppergold.GoldHopperBlock;
import net.knsh.cyclic.block.hoppergold.GoldHopperBlockEntity;
import net.knsh.cyclic.block.melter.BlockMelter;
import net.knsh.cyclic.block.melter.TileMelter;
import net.knsh.cyclic.block.trash.TrashBlock;
import net.knsh.cyclic.block.trash.TrashBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.Collection;

public class CyclicBlocks {
    public static Collection<ItemLike> ITEM_INSTANCE = new ArrayList<>();
    public static Collection<Block> BLOCK_INSTANCE = new ArrayList<>();

    public static ItemBlockEntity<ConveyorBlockEntity> CONVEYOR = registerBlockEntity("conveyor", ConveyorBlockEntity::new, new ConveyorBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<AnvilVoidBlockEntity> ANVILVOID = registerBlockEntity("anvil_void", AnvilVoidBlockEntity::new, new AnvilVoidBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<TrashBlockEntity> TRASH = registerBlockEntity("trash", TrashBlockEntity::new, new TrashBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<SimpleHopperBlockEntity> HOPPER = registerBlockEntity("hopper", SimpleHopperBlockEntity::new, new SimpleHopperBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<FluidHopperBlockEntity> FLUIDHOPPER = registerBlockEntity("hopper_fluid", FluidHopperBlockEntity::new, new FluidHopperBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<GoldHopperBlockEntity> HOPPERGOLD = registerBlockEntity("hopper_gold", GoldHopperBlockEntity::new, new GoldHopperBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<TileGeneratorFuel> GENERATOR_FUEL = registerBlockEntity("generator_fuel", TileGeneratorFuel::new, new BlockGeneratorFuel(FabricBlockSettings.create()));
    public static ItemBlockEntity<CrafterBlockEntity> CRAFTER = registerBlockEntity("crafter", CrafterBlockEntity::new, new CrafterBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<BeaconPotionBlockEntity> BEACON = registerBlockEntity("beacon", BeaconPotionBlockEntity::new, new BeaconPotionBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<AnvilMagmaBlockEntity> ANVIL_MAGMA = registerBlockEntity("anvil_magma", AnvilMagmaBlockEntity::new, new AnvilMagmaBlock(FabricBlockSettings.create().sound(SoundType.ANVIL)));
    public static ItemBlockEntity<AnvilAutoBlockEntity> ANVIL = registerBlockEntity("anvil", AnvilAutoBlockEntity::new, new AnvilAutoBlock(FabricBlockSettings.create().sound(SoundType.ANVIL)));
    public static ItemBlockEntity<AntiBeaconBlockEntity> ANTI_BEACON = registerBlockEntity("anti_beacon", AntiBeaconBlockEntity::new, new AntiBeaconBlock(FabricBlockSettings.create().luminance(p -> 2)));
    public static ItemBlockEntity<TileCableItem> ITEM_PIPE = registerBlockEntity("item_pipe", TileCableItem::new, new BlockCableItem(FabricBlockSettings.create()));
    public static ItemBlockEntity<TileCableFluid> FLUID_PIPE = registerBlockEntity("fluid_pipe", TileCableFluid::new, new BlockCableFluid(FabricBlockSettings.create()));
    public static ItemBlockEntity<TileCableEnergy> ENERGY_PIPE = registerBlockEntity("energy_pipe", TileCableEnergy::new, new BlockCableEnergy(FabricBlockSettings.create()));
    public static BaseBlockEntity<BatteryBlockEntity> BATTERY = registerBaseBlockEntity("battery", BatteryBlockEntity::new, new BatteryBlock(FabricBlockSettings.create()));
    public static ItemBlockEntity<TileMelter> MELTER = registerBlockEntity("melter", TileMelter::new, new BlockMelter(FabricBlockSettings.create()));

    public static ItemBlock SPONGE_MILK = registerBlock("sponge_milk", new MilkSpongeBlock(FabricBlockSettings.create().luminance(p -> 1)));

    public static void register() {}

    private static Block register(String id, Block block) {
        BLOCK_INSTANCE.add(block);
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Cyclic.MOD_ID, id), block);
    }

    private static <T extends BlockEntity> BaseBlockEntity<T> registerBaseBlockEntity(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        Block registeredBlock = register(id, block);
        ITEM_INSTANCE.add(registeredBlock);

        return new BaseBlockEntity<>(
                Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(Cyclic.MOD_ID, id), FabricBlockEntityTypeBuilder.create(factory, block).build()),
                registeredBlock
        );
    }

    private static <T extends BlockEntity> ItemBlockEntity<T> registerBlockEntity(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        Block registeredBlock = register(id, block);
        ITEM_INSTANCE.add(registeredBlock);

        ItemBlockEntity<T> itemBlockEntity = new ItemBlockEntity<>(
                Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(Cyclic.MOD_ID, id), FabricBlockEntityTypeBuilder.create(factory, block).build()),
                registeredBlock,
                Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Cyclic.MOD_ID, id), new BlockItem(block, new FabricItemSettings()))
        );

        return itemBlockEntity;
    }

    private static ItemBlock registerBlock(String id, Block block) {
        Block registeredBlock = register(id, block);
        ITEM_INSTANCE.add(registeredBlock);

        return new ItemBlock(
                registeredBlock,
                Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Cyclic.MOD_ID, id), new BlockItem(block, new FabricItemSettings()))
        );
    }

    public record BaseBlockEntity<T extends BlockEntity>(BlockEntityType<T> blockEntity, Block block) {}
    public record ItemBlockEntity<T extends BlockEntity>(BlockEntityType<T> blockEntity, Block block, Item item) {}
    public record ItemBlock(Block block, Item item) {}
}
