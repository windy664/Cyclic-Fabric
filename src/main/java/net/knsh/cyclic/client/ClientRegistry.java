package net.knsh.cyclic.client;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.knsh.cyclic.block.antipotion.RenderAntiBeacon;
import net.knsh.cyclic.block.anvil.AnvilAutoScreen;
import net.knsh.cyclic.block.anvilmagma.AnvilMagmaScreen;
import net.knsh.cyclic.block.anvilvoid.AnvilVoidScreen;
import net.knsh.cyclic.block.battery.BatteryScreen;
import net.knsh.cyclic.block.beaconpotion.BeaconPotionScreen;
import net.knsh.cyclic.block.beaconpotion.RenderBeaconPotion;
import net.knsh.cyclic.block.cable.fluid.FluidCableScreen;
import net.knsh.cyclic.block.cable.item.ItemCableScreen;
import net.knsh.cyclic.block.conveyor.ConveyorItemRenderer;
import net.knsh.cyclic.block.crafter.CrafterScreen;
import net.knsh.cyclic.block.generatorfuel.GeneratorFuelScreen;
import net.knsh.cyclic.block.melter.ScreenMelter;
import net.knsh.cyclic.fluid.FluidMagmaHolder;
import net.knsh.cyclic.fluid.FluidXpJuiceHolder;
import net.knsh.cyclic.item.crafting.CraftingBagScreen;
import net.knsh.cyclic.item.crafting.simple.CraftingStickScreen;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicEntities;
import net.knsh.cyclic.registry.CyclicFluids;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;

public class ClientRegistry {
    public static void register() {
        // Fluids
        FluidRenderHandlerRegistry.INSTANCE.register(CyclicFluids.STILL_XP, CyclicFluids.FLOWING_XP, new SimpleFluidRenderHandler(
                new ResourceLocation("cyclic:block/fluid/xpjuice_still"),
                new ResourceLocation("cyclic:block/fluid/xpjuice_flow"),
                FluidXpJuiceHolder.COLOR
        ));
        FluidRenderHandlerRegistry.INSTANCE.register(CyclicFluids.STILL_MAGMA, CyclicFluids.FLOWING_MAGMA, new SimpleFluidRenderHandler(
                new ResourceLocation("cyclic:block/fluid/magma_still"),
                new ResourceLocation("cyclic:block/fluid/magma_flow"),
                FluidMagmaHolder.COLOR
        ));

        // Block Render Layers
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.solid(),
                CyclicFluids.STILL_XP, CyclicFluids.FLOWING_XP,
                CyclicFluids.STILL_MAGMA, CyclicFluids.FLOWING_MAGMA);
        BlockRenderLayerMap.INSTANCE.putBlock(CyclicBlocks.BEACON.block(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(CyclicBlocks.ANTI_BEACON.block(), RenderType.cutout());

        // Block Entity Renderers
        BlockEntityRenderers.register(CyclicBlocks.BEACON.blockEntity(), RenderBeaconPotion::new);
        BlockEntityRenderers.register(CyclicBlocks.ANTI_BEACON.blockEntity(), RenderAntiBeacon::new);

        // Entity Model Renderers
        EntityRendererRegistry.register(CyclicEntities.CONVEYOR_ITEM, ConveyorItemRenderer::new);

        // Screens
        MenuScreens.register(CyclicScreens.ANVIL_VOID, AnvilVoidScreen::new);
        MenuScreens.register(CyclicScreens.ANVIL_MAGMA, AnvilMagmaScreen::new);
        MenuScreens.register(CyclicScreens.ANVIL, AnvilAutoScreen::new);
        MenuScreens.register(CyclicScreens.GENERATOR_FUEL, GeneratorFuelScreen::new);
        MenuScreens.register(CyclicScreens.CRAFTER, CrafterScreen::new);
        MenuScreens.register(CyclicScreens.BEACON, BeaconPotionScreen::new);
        MenuScreens.register(CyclicScreens.ITEM_PIPE, ItemCableScreen::new);
        MenuScreens.register(CyclicScreens.FLUID_PIPE, FluidCableScreen::new);
        MenuScreens.register(CyclicScreens.BATTERY, BatteryScreen::new);
        MenuScreens.register(CyclicScreens.CRAFTING_BAG, CraftingBagScreen::new);
        MenuScreens.register(CyclicScreens.CRAFTING_STICK, CraftingStickScreen::new);
        MenuScreens.register(CyclicScreens.MELTER, ScreenMelter::new);
    }
}
