package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.anvil.AnvilAutoContainer;
import net.knsh.cyclic.block.anvilmagma.AnvilMagmaContainer;
import net.knsh.cyclic.block.anvilvoid.AnvilVoidContainer;
import net.knsh.cyclic.block.battery.BatteryContainer;
import net.knsh.cyclic.block.beaconpotion.BeaconPotionContainer;
import net.knsh.cyclic.block.cable.fluid.ContainerCableFluid;
import net.knsh.cyclic.block.cable.item.ContainerCableItem;
import net.knsh.cyclic.block.crafter.CrafterContainer;
import net.knsh.cyclic.block.generatorfuel.ContainerGeneratorFuel;
import net.knsh.cyclic.block.melter.ContainerMelter;
import net.knsh.cyclic.block.wireless.energy.ContainerWirelessEnergy;
import net.knsh.cyclic.block.wireless.fluid.ContainerWirelessFluid;
import net.knsh.cyclic.block.wireless.item.ContainerWirelessItem;
import net.knsh.cyclic.gui.ContainerBase;
import net.knsh.cyclic.item.crafting.CraftingBagContainer;
import net.knsh.cyclic.item.crafting.simple.CraftingStickContainer;
import net.knsh.cyclic.item.datacard.filter.ContainerFilterCard;
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
        return Registry.register(BuiltInRegistries.MENU, new ResourceLocation(Cyclic.MOD_ID, id), factory);
    }

    public static void register() {}
}
