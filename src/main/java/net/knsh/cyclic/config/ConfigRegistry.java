package net.knsh.cyclic.config;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.antipotion.AntiBeaconBlockEntity;
import net.knsh.cyclic.block.anvil.AnvilAutoBlockEntity;
import net.knsh.cyclic.block.anvilmagma.AnvilMagmaBlockEntity;
import net.knsh.cyclic.block.cable.energy.EnergyCableBlockEntity;
import net.knsh.cyclic.block.cable.fluid.FluidCableBlockEntity;
import net.knsh.cyclic.block.crafter.CrafterBlockEntity;
import net.knsh.cyclic.block.generatorfuel.GeneratorFuelBlockEntity;
import net.knsh.cyclic.enchant.TravellerEnchant;
import net.knsh.cyclic.library.config.ConfigTemplate;
import net.knsh.cyclic.porting.neoforge.FluidFabricToForge;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Arrays;

public class ConfigRegistry extends ConfigTemplate {
    private static ForgeConfigSpec COMMON_CONFIG;
    private static ForgeConfigSpec CLIENT_CONFIG;

    public void setupMain() {
        COMMON_CONFIG.setConfig(setup(Cyclic.MOD_ID));
        ForgeConfigRegistry.INSTANCE.register(Cyclic.MOD_ID, ModConfig.Type.COMMON, ConfigRegistry.COMMON_CONFIG);
    }

    public void setupClient() {
        //CLIENT_CONFIG.setConfig(setup(Cyclic.MOD_ID + "-client"));
        //ForgeConfigRegistry.INSTANCE.register(Cyclic.MOD_ID, ModConfig.Type.CLIENT, ConfigRegistry.CLIENT_CONFIG);
    }

    private static final String WALL = "####################################################################################";

    static {
        buildDefaults();
        initConfig();
    }

    private static void buildDefaults() {
        // later
    }

    private static void initConfig() {
        final ForgeConfigSpec.Builder CFG = builder();
        CFG.comment(WALL, " Enchantment related configs (if disabled, they may still show up as NBT on books and such but have functions disabled and are not obtainable in survival)", WALL)
                .push("enchantment");
        TravellerEnchant.CFG = CFG.comment("If true, then the traveller enchantment will be enabled.  This enchantment reduces damage from cactus, sweet berry bushes, and fall damage. It also prevents elytra damage when flying.").define(TravellerEnchant.ID + ".enabled", true);
        CFG.pop(); //enchantment
        CFG.comment(WALL, " Block specific configs", WALL).push("blocks");
        AntiBeaconBlockEntity.HARMFUL_POTIONS = CFG.comment("If true, then all potions marked as harmful/negative will be used in addition to the 'anti_beacon.potion_list' for cures and immunities  (used by both sponge and artemisbeacon).")
                .define("harmful_potions", true);
        AntiBeaconBlockEntity.RADIUS = CFG.comment("Radius to protect players and entities from potion effects being applied (used by both sponge and artemisbeacon). ")
                .defineInRange("anti_beacon.radius", 16, 1, 128);
        AntiBeaconBlockEntity.TICKS = CFG.comment("Ticks to fire anti beacon and remove effects from entities (20 = 1 second).  Does not affect potion immunity which applies regardless of ticks. This only used if you gain a potion effect out of range and then walk into range, so keep this large.")
                .defineInRange("anti_beacon.ticks", 200, 20, 9999);
        AntiBeaconBlockEntity.POTIONS = CFG.comment("List of extra effects to clear. supports wildcard such as 'cyclic:*'. (This list is is used even if harmful_potions=false or true both)")
                .defineList("anti_beacon.potion_list", Arrays.asList("minecraft:poison", "minecraft:*_poison", "minecraft:wither",
                        "cyclic:gravity",
                        "minecraft:weakness", "minecraft:slowness"), it -> it instanceof String);
        GeneratorFuelBlockEntity.RF_PER_TICK = CFG.comment("RF energy per tick generated while burning furnace fuel in this machine.  Burn time in ticks is the same as furnace values, so 1 coal = 1600 ticks")
                .defineInRange("generator_fuel.rf_per_tick", 80, 1, 6400);
        CrafterBlockEntity.POWERCONF = CFG.comment("Power per use crafter").defineInRange("crafter.energy_cost", 500, 0, 64000);
        AnvilAutoBlockEntity.POWERCONF = CFG.comment("Power per repair anvil").defineInRange("anvil.energy_cost", 250, 0, 64000);
        AnvilMagmaBlockEntity.FLUIDCOST = CFG.comment("Cost of magma fluid per action").defineInRange("anvil_magma.fluid_cost", 100, 1, 64000);

        FluidCableBlockEntity.BUFFERSIZE = CFG.comment("How many buckets of buffer fluid the fluid cable can hold (for each direction. for example 2 here means 2000ub in each face)")
                .defineInRange("cables.fluid.buffer", (int) FluidFabricToForge.toDroplets(16), (int) FluidFabricToForge.toDroplets(1), (int) FluidFabricToForge.toDroplets(32));
        FluidCableBlockEntity.TRANSFER_RATE = CFG.comment("How many fluid units per tick can flow through these cables each tick (1 bucket = 1000) including normal flow and extraction mode")
                .defineInRange("cables.fluid.flow", (int) FluidFabricToForge.toDroplets(1000), (int) FluidFabricToForge.toDroplets(100), (int) FluidFabricToForge.toDroplets(32 * 1000));
        EnergyCableBlockEntity.BUFFERSIZE = CFG.comment("How much buffer the energy cables hold (must not be smaller than flow)")
                .defineInRange("cables.energy.buffer", 320000, 10, 320000 * 4);
        EnergyCableBlockEntity.TRANSFER_RATE = CFG.comment("How fast energy flows in these cables (must not be greater than buffer)")
                .defineInRange("cables.energy.flow", 10000, 1000, 32 * 10000);

        COMMON_CONFIG = CFG.build();
    }
}
