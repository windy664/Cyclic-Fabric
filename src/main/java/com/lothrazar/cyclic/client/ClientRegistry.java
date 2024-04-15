package com.lothrazar.cyclic.client;

import com.lothrazar.cyclic.block.anvilvoid.AnvilVoidScreen;
import com.lothrazar.cyclic.block.battery.BatteryScreen;
import com.lothrazar.cyclic.block.conveyor.ConveyorItemRenderer;
import com.lothrazar.cyclic.block.crafter.CrafterScreen;
import com.lothrazar.cyclic.fluid.*;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.registry.CyclicEntities;
import com.lothrazar.cyclic.registry.CyclicItems;
import com.lothrazar.cyclic.registry.CyclicScreens;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.lothrazar.cyclic.block.BlockCyclic;
import com.lothrazar.cyclic.block.antipotion.RenderAntiBeacon;
import com.lothrazar.cyclic.block.anvil.AnvilAutoScreen;
import com.lothrazar.cyclic.block.anvilmagma.AnvilMagmaScreen;
import com.lothrazar.cyclic.block.beaconpotion.BeaconPotionScreen;
import com.lothrazar.cyclic.block.beaconpotion.RenderBeaconPotion;
import com.lothrazar.cyclic.block.tank.RenderTank;
import com.lothrazar.cyclic.item.ItemCyclic;
import com.lothrazar.cyclic.item.crafting.CraftingBagScreen;
import com.lothrazar.cyclic.item.crafting.simple.CraftingStickScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ClientRegistry {
    public static void register() {
        // Auto Register
        CyclicBlocks.BLOCK_INSTANCE.forEach(block -> {
            if (block instanceof BlockCyclic cyclicBlock) {
                cyclicBlock.registerClient();
            }
        });

        CyclicItems.INSTANCE.forEach(itemLike -> {
            if (itemLike instanceof ItemCyclic cyclicItem) {
                cyclicItem.registerClient();
            }
        });

        // Fluids
        FluidRenderHandlerRegistry.INSTANCE.register(FluidXpJuiceHolder.STILL, FluidXpJuiceHolder.FLOWING, new SimpleFluidRenderHandler(
                FluidXpJuiceHolder.FLUID_STILL, FluidXpJuiceHolder.FLUID_FLOWING
        ));
        FluidRenderHandlerRegistry.INSTANCE.register(FluidMagmaHolder.STILL, FluidMagmaHolder.FLOWING, new SimpleFluidRenderHandler(
                FluidMagmaHolder.FLUID_STILL, FluidMagmaHolder.FLUID_FLOWING
        ));
        FluidRenderHandlerRegistry.INSTANCE.register(FluidBiomassHolder.STILL, FluidBiomassHolder.FLOWING, new SimpleFluidRenderHandler(
                FluidBiomassHolder.FLUID_STILL, FluidBiomassHolder.FLUID_FLOWING
        ));
        FluidRenderHandlerRegistry.INSTANCE.register(FluidHoneyHolder.STILL, FluidHoneyHolder.FLOWING, new SimpleFluidRenderHandler(
                FluidHoneyHolder.FLUID_STILL, FluidHoneyHolder.FLUID_FLOWING
        ));
        FluidRenderHandlerRegistry.INSTANCE.register(FluidSlimeHolder.STILL, FluidSlimeHolder.FLOWING, new SimpleFluidRenderHandler(
                FluidSlimeHolder.FLUID_STILL, FluidSlimeHolder.FLUID_FLOWING
        ));
        FluidRenderHandlerRegistry.INSTANCE.register(FluidWaxHolder.STILL, FluidWaxHolder.FLOWING, new SimpleFluidRenderHandler(
                FluidWaxHolder.FLUID_STILL, FluidWaxHolder.FLUID_FLOWING
        ));

        // Block Render Layers
        BlockRenderLayerMap.INSTANCE.putBlock(CyclicBlocks.BEACON.block(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(CyclicBlocks.ANTI_BEACON.block(), RenderType.cutout());

        // Block Entity Renderers
        BlockEntityRenderers.register(CyclicBlocks.BEACON.blockEntity(), RenderBeaconPotion::new);
        BlockEntityRenderers.register(CyclicBlocks.ANTI_BEACON.blockEntity(), RenderAntiBeacon::new);
        BlockEntityRenderers.register(CyclicBlocks.TANK.blockEntity(), RenderTank::new);

        // Entity Model Renderers
        EntityRendererRegistry.register(CyclicEntities.CONVEYOR_ITEM, ConveyorItemRenderer::new);

        // Screens
        MenuScreens.register(CyclicScreens.ANVIL_VOID, AnvilVoidScreen::new);
        MenuScreens.register(CyclicScreens.ANVIL_MAGMA, AnvilMagmaScreen::new);
        MenuScreens.register(CyclicScreens.ANVIL, AnvilAutoScreen::new);
        MenuScreens.register(CyclicScreens.CRAFTER, CrafterScreen::new);
        MenuScreens.register(CyclicScreens.BEACON, BeaconPotionScreen::new);
        MenuScreens.register(CyclicScreens.BATTERY, BatteryScreen::new);
        MenuScreens.register(CyclicScreens.CRAFTING_BAG, CraftingBagScreen::new);
        MenuScreens.register(CyclicScreens.CRAFTING_STICK, CraftingStickScreen::new);
    }
}
