package com.lothrazar.cyclic.datagen.recipes.cyclic;

import com.lothrazar.cyclic.datagen.builder.MelterRecipeBuilder;
import com.lothrazar.cyclic.fluid.*;
import com.lothrazar.cyclic.registry.CyclicItems;
import com.lothrazar.cyclic.util.FabricHelper;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Consumer;

public class MelterRecipes extends FabricRecipeProvider {
    public MelterRecipes(FabricDataOutput output) {
        super(output);
    }

    @Override
    public String getName() {
        return "melter";
    }

    @Override
    protected ResourceLocation getRecipeIdentifier(ResourceLocation identifier) {
        return super.getRecipeIdentifier(identifier);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        MelterRecipeBuilder.melting(FluidSlimeHolder.STILL, FabricHelper.toDroplets(500), 100, 160)
                .requires(Tags.Items.SLIMEBALLS)
                .requires(Tags.Items.SLIMEBALLS)
                .name("slimeballs")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidSlimeHolder.STILL, FabricHelper.toDroplets(250), 100, 160)
                .requires(Tags.Items.SLIMEBALLS)
                .name("slimeball")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidSlimeHolder.STILL, FabricHelper.toDroplets(4500), 100, 160)
                .requires(Items.SLIME_BLOCK)
                .requires(Items.SLIME_BLOCK)
                .name("slimeblocks")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidSlimeHolder.STILL, FabricHelper.toDroplets(2250), 100, 160)
                .requires(Items.SLIME_BLOCK)
                .name("slimeblock")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidWaxHolder.STILL, FabricHelper.toDroplets(100), 20, 40)
                .requires(ItemTags.CANDLES)
                .name("candle_wax")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidWaxHolder.STILL, FabricHelper.toDroplets(1000), 40, 120)
                .requires(Items.HONEYCOMB_BLOCK)
                .name("honeycomb")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidBiomassHolder.STILL, FabricHelper.toDroplets(1000), 80, 60)
                .requires(CyclicItems.BIOMASS)
                .requires(Items.BAMBOO)
                .name("bamboo")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidBiomassHolder.STILL, FabricHelper.toDroplets(1000), 40, 60)
                .requires(CyclicItems.BIOMASS)
                .requires(CyclicItems.BIOMASS)
                .name("biomasses")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidBiomassHolder.STILL, FabricHelper.toDroplets(500), 40, 60)
                .requires(CyclicItems.BIOMASS)
                .name("biomass")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidBiomassHolder.STILL, FabricHelper.toDroplets(1000), 20, 60)
                .requires(CyclicItems.BIOMASS)
                .requires(Tags.Items.SEEDS)
                .name("seeds")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidBiomassHolder.STILL, FabricHelper.toDroplets(1000), 80, 60)
                .requires(CyclicItems.BIOMASS)
                .requires(Items.KELP)
                .name("kelp")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidMagmaHolder.STILL, FabricHelper.toDroplets(2000), 80, 60)
                .requires(Items.MAGMA_BLOCK)
                .requires(Items.MAGMA_BLOCK)
                .name("magmas")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidMagmaHolder.STILL, FabricHelper.toDroplets(1000), 80, 60)
                .requires(Items.MAGMA_BLOCK)
                .name("magma")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidMagmaHolder.STILL, FabricHelper.toDroplets(200), 80, 60)
                .requires(Items.MAGMA_CREAM)
                .requires(Items.MAGMA_CREAM)
                .name("magma_creams")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidMagmaHolder.STILL, FabricHelper.toDroplets(100), 80, 60)
                .requires(Items.MAGMA_BLOCK)
                .name("magma_cream")
                .save(exporter);

        MelterRecipeBuilder.melting(Fluids.LAVA, FabricHelper.toDroplets(1000), 100, 160)
                .requires(Tags.Items.STONE)
                .requires(Tags.Items.OBSIDIAN)
                .name("stone_obsidian")
                .save(exporter);

        MelterRecipeBuilder.melting(Fluids.LAVA, FabricHelper.toDroplets(1000), 100, 160)
                .requires(Tags.Items.OBSIDIAN)
                .requires(Tags.Items.STONE)
                .name("stone_obsidian1")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidHoneyHolder.STILL, FabricHelper.toDroplets(2000), 80, 60)
                .requires(Items.HONEY_BLOCK)
                .requires(Items.HONEY_BLOCK)
                .name("honeyblocks")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidHoneyHolder.STILL, FabricHelper.toDroplets(1000), 80, 60)
                .requires(Items.HONEY_BLOCK)
                .name("honeyblock")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidHoneyHolder.STILL, FabricHelper.toDroplets(500), 80, 60)
                .requires(Items.HONEY_BOTTLE)
                .requires(Items.HONEY_BOTTLE)
                .name("honeybottles")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidHoneyHolder.STILL, FabricHelper.toDroplets(250), 80, 60)
                .requires(Items.HONEY_BOTTLE)
                .name("honeybottle")
                .save(exporter);

        MelterRecipeBuilder.melting(Fluids.WATER, FabricHelper.toDroplets(200), 40, 120)
                .requires(Items.SNOW_BLOCK)
                .requires(Items.SNOW_BLOCK)
                .name("snow")
                .save(exporter);

        MelterRecipeBuilder.melting(Fluids.WATER, FabricHelper.toDroplets(2000), 80, 60)
                .requires(Items.ICE)
                .requires(Items.ICE)
                .name("ice")
                .save(exporter);

        MelterRecipeBuilder.melting(Fluids.WATER, FabricHelper.toDroplets(1000), 80, 60)
                .requires(Items.ICE)
                .name("ice1")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidXpJuiceHolder.STILL, FabricHelper.toDroplets(2000), 100, 160)
                .requires(CyclicItems.EXPERIENCE_FOOD)
                .requires(CyclicItems.EXPERIENCE_FOOD)
                .name("exp")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidXpJuiceHolder.STILL, FabricHelper.toDroplets(1100), 100, 160)
                .requires(CyclicItems.EXPERIENCE_FOOD)
                .requires(Items.BLAZE_ROD)
                .name("expblaze")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidXpJuiceHolder.STILL, FabricHelper.toDroplets(1020), 100, 160)
                .requires(CyclicItems.EXPERIENCE_FOOD)
                .requires(Tags.Items.BONES)
                .name("expbones")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidXpJuiceHolder.STILL, FabricHelper.toDroplets(1050), 100, 160)
                .requires(CyclicItems.EXPERIENCE_FOOD)
                .requires(Items.ROTTEN_FLESH)
                .name("expflesh")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidXpJuiceHolder.STILL, FabricHelper.toDroplets(1200), 100, 160)
                .requires(CyclicItems.EXPERIENCE_FOOD)
                .requires(Items.GHAST_TEAR)
                .name("exptears")
                .save(exporter);

        MelterRecipeBuilder.melting(FluidXpJuiceHolder.STILL, FabricHelper.toDroplets(1200), 40, 60)
                .requires(CyclicItems.EXPERIENCE_FOOD)
                .requires(Items.WITHER_ROSE)
                .name("expwitherrose")
                .save(exporter);
    }
}
