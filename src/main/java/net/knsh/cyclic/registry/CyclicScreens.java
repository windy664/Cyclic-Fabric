package net.knsh.cyclic.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.anvilvoid.AnvilVoidScreenHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class CyclicScreens {
    public static MenuType<AnvilVoidScreenHandler> ANVIL_VOID;
    //public static MenuType<CableItemScreenHandler> ITEM_PIPE;

    public static void register() {
        ANVIL_VOID = Registry.register(BuiltInRegistries.MENU, new ResourceLocation(Cyclic.MOD_ID, "anvil_void"),
                new ExtendedScreenHandlerType<>(AnvilVoidScreenHandler::new));
        //ITEM_PIPE = Registry.register(BuiltInRegistries.MENU, new ResourceLocation(Cyclic.MOD_ID, "item_pipe"),
                //new ExtendedScreenHandlerType<>(CableItemScreenHandler::new));
    }
}
