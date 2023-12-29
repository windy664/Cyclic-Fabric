package net.knsh.cyclic.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import net.knsh.cyclic.Cyclic;
import net.minecraft.resources.ResourceLocation;

// based off of the Create mods REI Plugin implementation
public class CyclicPluginREI implements REIClientPlugin {
    private static final ResourceLocation ID = new ResourceLocation(Cyclic.MOD_ID, "rei");

    @Override
    public void registerCategories(CategoryRegistry registry) {

    }

    @Override
    public void registerScreens(ScreenRegistry registry) {

    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {

    }
}
