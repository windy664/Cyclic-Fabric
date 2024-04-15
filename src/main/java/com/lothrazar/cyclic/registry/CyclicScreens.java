package com.lothrazar.cyclic.registry;

import com.lothrazar.cyclic.block.anvilvoid.AnvilVoidContainer;
import com.lothrazar.cyclic.block.battery.BatteryContainer;
import com.lothrazar.cyclic.block.crafter.CrafterContainer;
import com.lothrazar.cyclic.gui.ContainerBase;
import com.lothrazar.cyclic.item.datacard.filter.ContainerFilterCard;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.block.anvil.AnvilAutoContainer;
import com.lothrazar.cyclic.block.anvilmagma.AnvilMagmaContainer;
import com.lothrazar.cyclic.block.beaconpotion.BeaconPotionContainer;
import com.lothrazar.cyclic.block.cable.fluid.ContainerCableFluid;
import com.lothrazar.cyclic.block.cable.item.ContainerCableItem;
import com.lothrazar.cyclic.block.generatorfuel.ContainerGeneratorFuel;
import com.lothrazar.cyclic.block.melter.ContainerMelter;
import com.lothrazar.cyclic.block.wireless.energy.ContainerWirelessEnergy;
import com.lothrazar.cyclic.block.wireless.fluid.ContainerWirelessFluid;
import com.lothrazar.cyclic.block.wireless.item.ContainerWirelessItem;
import com.lothrazar.cyclic.item.crafting.CraftingBagContainer;
import com.lothrazar.cyclic.item.crafting.simple.CraftingStickContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class CyclicScreens {
    public static MenuType<AnvilVoidContainer> ANVIL_VOID = registerScreen("anvil_void",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new AnvilVoidContainer(syncId, inventory, inventory.player.level(), buf.readBlockPos()))));
    public static MenuType<ContainerGeneratorFuel> GENERATOR_FUEL = registerScreen("generator_fuel",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new ContainerGeneratorFuel(syncId, inventory.player.level(), buf.readBlockPos(), inventory, inventory.player))));
    public static MenuType<CrafterContainer> CRAFTER = registerScreen("crafter",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new CrafterContainer(syncId, inventory, inventory.player.level(), buf.readBlockPos()))));
    public static MenuType<BeaconPotionContainer> BEACON = registerScreen("beacon",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new BeaconPotionContainer(syncId, inventory, inventory.player.level(), buf.readBlockPos()))));
    public static MenuType<AnvilMagmaContainer> ANVIL_MAGMA = registerScreen("anvil_magma",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new AnvilMagmaContainer(syncId, inventory, inventory.player.level(), buf.readBlockPos()))));
    public static MenuType<AnvilAutoContainer> ANVIL = registerScreen("anvil",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new AnvilAutoContainer(syncId, inventory, inventory.player.level(), buf.readBlockPos()))));
    public static MenuType<ContainerCableItem> ITEM_PIPE = registerScreen("item_pipe",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new ContainerCableItem(syncId, inventory.player.level(), buf.readBlockPos(), inventory, inventory.player))));
    public static MenuType<ContainerCableFluid> FLUID_PIPE = registerScreen("fluid_pipe",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new ContainerCableFluid(syncId, inventory.player.level(), buf.readBlockPos(), inventory, inventory.player))));
    public static MenuType<BatteryContainer> BATTERY = registerScreen("battery",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new BatteryContainer(syncId, inventory, inventory.player.level(), buf.readBlockPos()))));
    public static MenuType<CraftingBagContainer> CRAFTING_BAG = registerScreen("crafting_bag",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new CraftingBagContainer(syncId, inventory, inventory.player, buf.readInt()))));
    public static MenuType<CraftingStickContainer> CRAFTING_STICK = registerScreen("crafting_stick",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new CraftingStickContainer(syncId, inventory, inventory.player, buf.readInt()))));
    public static MenuType<ContainerMelter> MELTER = registerScreen("melter",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new ContainerMelter(syncId, inventory.player.level(), buf.readBlockPos(), inventory, inventory.player))));
    public static MenuType<ContainerFilterCard> FILTER_DATA = registerScreen("filter_data",
            new ExtendedScreenHandlerType(((syncId, inventory, buf) -> new ContainerFilterCard(syncId, inventory, inventory.player))));
    public static MenuType<ContainerWirelessEnergy> WIRELESS_ENERGY = registerScreen("wireless_energy",
            new ExtendedScreenHandlerType(((syncId, inventory, buf) -> new ContainerWirelessEnergy(syncId, inventory.player.level(), buf.readBlockPos(), inventory, inventory.player))));
    public static MenuType<ContainerWirelessItem> WIRELESS_ITEM = registerScreen("wireless_item",
            new ExtendedScreenHandlerType(((syncId, inventory, buf) -> new ContainerWirelessItem(syncId, inventory.player.level(), buf.readBlockPos(), inventory, inventory.player))));
    public static MenuType<ContainerWirelessFluid> WIRELESS_FLUID = registerScreen("wireless_fluid",
            new ExtendedScreenHandlerType(((syncId, inventory, buf) -> new ContainerWirelessFluid(syncId, inventory.player.level(), buf.readBlockPos(), inventory, inventory.player))));

    private static <T extends ContainerBase> MenuType<T> registerScreen(String id, ExtendedScreenHandlerType factory) {
        return Registry.register(BuiltInRegistries.MENU, new ResourceLocation(ModCyclic.MODID, id), factory);
    }

    public static void register() {}
}
