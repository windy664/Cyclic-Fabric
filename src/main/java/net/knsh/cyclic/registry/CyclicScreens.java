package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.anvilvoid.AnvilVoidScreenHandler;
import net.knsh.cyclic.block.beaconpotion.BeaconPotionScreenHandler;
import net.knsh.cyclic.block.crafter.CrafterScreenHandler;
import net.knsh.cyclic.block.generatorfuel.GeneratorFuelScreenHandler;
import net.knsh.cyclic.gui.ScreenHandlerBase;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class CyclicScreens {
    public static MenuType<AnvilVoidScreenHandler> ANVIL_VOID = registerScreen("anvil_void",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new AnvilVoidScreenHandler(syncId, inventory, inventory.player.level(), buf.readBlockPos()))));
    public static MenuType<GeneratorFuelScreenHandler> GENERATOR_FUEL = registerScreen("generator_fuel",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new GeneratorFuelScreenHandler(syncId, inventory, inventory.player.level(), buf.readBlockPos()))));
    public static MenuType<CrafterScreenHandler> CRAFTER = registerScreen("crafter",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new CrafterScreenHandler(syncId, inventory, inventory.player.level(), buf.readBlockPos()))));
    public static MenuType<BeaconPotionScreenHandler> BEACON = registerScreen("beacon",
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new BeaconPotionScreenHandler(syncId, inventory, inventory.player.level(), buf.readBlockPos()))));

    private static <T extends ScreenHandlerBase> MenuType<T> registerScreen(String id, ExtendedScreenHandlerType factory) {
        return Registry.register(BuiltInRegistries.MENU, new ResourceLocation(Cyclic.MOD_ID, id), factory);
    }

    public static void register() {}
}
