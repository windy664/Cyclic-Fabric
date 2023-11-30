package net.knsh.cyclic.config;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.antipotion.AntiBeaconBlockEntity;
import net.knsh.cyclic.block.anvil.AnvilAutoBlockEntity;
import net.knsh.cyclic.block.anvilmagma.AnvilMagmaBlockEntity;
import net.knsh.cyclic.block.crafter.CrafterBlockEntity;
import net.knsh.cyclic.block.generatorfuel.GeneratorFuelBlockEntity;
import net.knsh.cyclic.library.config.ConfigTemplate;
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

        COMMON_CONFIG = CFG.build();
    }
}
