package com.lothrazar.cyclic.datagen;

import com.lothrazar.cyclic.datagen.recipes.cyclic.MelterRecipes;
import com.lothrazar.cyclic.datagen.recipes.minecraft.CraftingRecipes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;

public class CyclicDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(CraftingRecipes::new);
		pack.addProvider(MelterRecipes::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);
	}
}
