package net.knsh.cyclic.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.knsh.cyclic.datagen.recipes.cyclic.MelterRecipes;
import net.knsh.cyclic.datagen.recipes.minecraft.CraftingRecipes;
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
