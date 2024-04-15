package com.lothrazar.cyclic.config;

import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.block.antipotion.AntiBeaconBlockEntity;
import com.lothrazar.cyclic.block.anvil.AnvilAutoBlockEntity;
import com.lothrazar.cyclic.block.anvilmagma.AnvilMagmaBlockEntity;
import com.lothrazar.cyclic.block.cable.energy.TileCableEnergy;
import com.lothrazar.cyclic.block.cable.fluid.TileCableFluid;
import com.lothrazar.cyclic.block.crafter.CrafterBlockEntity;
import com.lothrazar.cyclic.block.generatorfuel.TileGeneratorFuel;
import com.lothrazar.cyclic.enchant.*;
import com.lothrazar.cyclic.util.FabricHelper;
import com.lothrazar.library.config.ConfigTemplate;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.*;

public class ConfigRegistry extends ConfigTemplate {
    private static ForgeConfigSpec COMMON_CONFIG;
    private static ForgeConfigSpec CLIENT_CONFIG;

    public void setupMain() {
        COMMON_CONFIG.setConfig(setup(ModCyclic.MODID));
        ForgeConfigRegistry.INSTANCE.register(ModCyclic.MODID, ModConfig.Type.COMMON, ConfigRegistry.COMMON_CONFIG);
    }

    public void setupClient() {
        CLIENT_CONFIG.setConfig(setup(ModCyclic.MODID + "-client"));
        ForgeConfigRegistry.INSTANCE.register(ModCyclic.MODID, ModConfig.Type.CLIENT, ConfigRegistry.CLIENT_CONFIG);
    }

    // Defaults
    private static final List<String> BEHEADING = new ArrayList<>();
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> BEHEADING_SKINS;
    private static final List<String> DISARM_IGNORE = new ArrayList<>();
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> DISARM_IGNORE_LIST;

    private static final String WALL = "####################################################################################";

    static {
        buildDefaults();
        initConfig();
    }

    private static void buildDefaults() {
        //http://minecraft.gamepedia.com/Player.dat_format#Player_Heads
        //mhf https://twitter.com/Marc_IRL/status/542330244473311232  https://pastebin.com/5mug6EBu
        //other https://www.planetminecraft.com/blog/minecraft-playerheads-2579899/
        //NBT image data from  http://www.minecraft-heads.com/custom/heads/animals/6746-llama
        BEHEADING.add("minecraft:blaze:MHF_Blaze");
        BEHEADING.add("minecraft:cat:MHF_Ocelot");
        BEHEADING.add("minecraft:cave_spider:MHF_CaveSpider");
        BEHEADING.add("minecraft:chicken:MHF_Chicken");
        BEHEADING.add("minecraft:cow:MHF_Cow");
        BEHEADING.add("minecraft:enderman:MHF_Enderman");
        BEHEADING.add("minecraft:ghast:MHF_Ghast");
        BEHEADING.add("minecraft:iron_golem:MHF_Golem");
        BEHEADING.add("minecraft:magma_cube:MHF_LavaSlime");
        BEHEADING.add("minecraft:mooshroom:MHF_MushroomCow");
        BEHEADING.add("minecraft:ocelot:MHF_Ocelot");
        BEHEADING.add("minecraft:pig:MHF_Pig");
        BEHEADING.add("minecraft:zombie_pigman:MHF_PigZombie");
        BEHEADING.add("minecraft:sheep:MHF_Sheep");
        BEHEADING.add("minecraft:slime:MHF_Slime");
        BEHEADING.add("minecraft:spider:MHF_Spider");
        BEHEADING.add("minecraft:squid:MHF_Squid");
        BEHEADING.add("minecraft:villager:MHF_Villager");
        BEHEADING.add("minecraft:witch:MHF_Witch");
        BEHEADING.add("minecraft:wolf:MHF_Wolf");
        BEHEADING.add("minecraft:guardian:MHF_Guardian");
        BEHEADING.add("minecraft:elder_guardian:MHF_Guardian");
        BEHEADING.add("minecraft:snow_golem:MHF_SnowGolem");
        BEHEADING.add("minecraft:silverfish:MHF_Silverfish");
        BEHEADING.add("minecraft:endermite:MHF_Endermite");
    }

    private static void initConfig() {
        final ForgeConfigSpec.Builder CFG = builder();
        CFG.comment(WALL, " Enchantment related configs (if disabled, they may still show up as NBT on books and such but have functions disabled and are not obtainable in survival)", WALL)
                .push("enchantment");
        BeheadingEnchant.CFG = CFG.comment("If true, then the beheading enchantment will be enabled. \nThis enchantment increases the chance of mob heads dropping when killing mobs.").define(BeheadingEnchant.ID + ".enabled", true);
        BEHEADING_SKINS = CFG.comment("Beheading enchant add player skin head drop, add any mob id and any skin").defineList(BeheadingEnchant.ID + ".EntityMHF", BEHEADING,
                it -> it instanceof String);
        BeheadingEnchant.PERCDROP = CFG.comment("Base perecentage chance to drop a head on kill").defineInRange(BeheadingEnchant.ID + ".percent", 20, 1, 99);
        BeheadingEnchant.PERCPERLEVEL = CFG.comment("Percentage increase per level of enchant. Formula [percent + (level - 1) * per_level] ").defineInRange(BeheadingEnchant.ID + ".per_level", 25, 1, 99);
        TravellerEnchant.CFG = CFG.comment("If true, then the traveller enchantment will be enabled. \nThis enchantment reduces damage from cactus, sweet berry bushes, and fall damage. It also prevents elytra damage when flying.").define(TravellerEnchant.ID + ".enabled", true);
        AutoSmeltEnchant.CFG = CFG.comment("If true, then the auto smelt enchantment will be enabled. \nThis enchantment will smelt blocks as they are mined.").define(AutoSmeltEnchant.ID + ".enabled", true);
        ReachEnchant.CFG = CFG.comment("If true, then the reach enchantment will be enabled. \nThis enchantment increases the reach of the player by 5 blocks.").define(ReachEnchant.ID + ".enabled", true);
        ReachEnchant.REACH_BOOST = CFG.comment("How much reach to add to the player (in blocks). \nDefault is 11.").defineInRange(ReachEnchant.ID + ".reach_boost", 11, 0, 20);
        BeekeeperEnchant.CFG = CFG.comment("If true, then the beekeeper enchantment will be enabled. \nThis enchantment makes bees not attack the player.").define(BeekeeperEnchant.ID + ".enabled", true);
        DisarmEnchant.CFG = CFG.comment("If true, then the disarm enchantment will be enabled. \nThis enchantment has a chance to disarm the target when attacking.").define(DisarmEnchant.ID + ".enabled", true);
        DisarmEnchant.PERCENTPERLEVEL = CFG.comment("Enchant level drop rate.  % = drop + (level-1)*drop").defineInRange(DisarmEnchant.ID + ".percentPerLevel", 15, 1, 100);
        EnderPearlEnchant.CFG = CFG.comment("If true, then the ender pearl enchantment will be enabled. \nThis enchantment allows the player to throw ender pearls when right clicking a weapon.").define(EnderPearlEnchant.ID + ".enabled", true);
        DISARM_IGNORE_LIST = CFG.comment("Mobs in this list cannot be disarmed and have their weapon stolen by the disarm enchantment")
                .defineList(DisarmEnchant.ID + ".ingoredMobs", DISARM_IGNORE,
                        it -> it instanceof String);
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
        TileGeneratorFuel.RF_PER_TICK = CFG.comment("RF energy per tick generated while burning furnace fuel in this machine.  Burn time in ticks is the same as furnace values, so 1 coal = 1600 ticks")
                .defineInRange("generator_fuel.rf_per_tick", 80, 1, 6400);
        CrafterBlockEntity.POWERCONF = CFG.comment("Power per use crafter").defineInRange("crafter.energy_cost", 500, 0, 64000);
        AnvilAutoBlockEntity.POWERCONF = CFG.comment("Power per repair anvil").defineInRange("anvil.energy_cost", 250, 0, 64000);
        AnvilMagmaBlockEntity.FLUIDCOST = CFG.comment("Cost of magma fluid per action").defineInRange("anvil_magma.fluid_cost", 100, 1, 64000);

        TileCableFluid.BUFFERSIZE = CFG.comment("How many buckets of buffer fluid the fluid cable can hold (for each direction. for example 2 here means 2000ub in each face)")
                .defineInRange("cables.fluid.buffer", (int) FabricHelper.toDroplets(16), (int) FabricHelper.toDroplets(1), (int) FabricHelper.toDroplets(32));
        TileCableFluid.TRANSFER_RATE = CFG.comment("How many fluid units per tick can flow through these cables each tick (1 bucket = 1000) including normal flow and extraction mode")
                .defineInRange("cables.fluid.flow", (int) FabricHelper.toDroplets(1000), (int) FabricHelper.toDroplets(100), (int) FabricHelper.toDroplets(32 * 1000));
        TileCableEnergy.BUFFERSIZE = CFG.comment("How much buffer the energy cables hold (must not be smaller than flow)")
                .defineInRange("cables.energy.buffer", 10000, 10, 320000 * 4);
        TileCableEnergy.TRANSFER_RATE = CFG.comment("How fast energy flows in these cables (must not be greater than buffer)")
                .defineInRange("cables.energy.flow", 10000, 1000, 32 * 10000);

        COMMON_CONFIG = CFG.build();
        initClientConfig();
    }

    private static void initClientConfig() {
        final ForgeConfigSpec.Builder CFGC = builder();
        CFGC.comment(WALL, "Client-side properties", WALL)
                .push(ModCyclic.MODID);
        CFGC.comment(WALL, "Block Rendering properties.  Color MUST have one # symbol and then six spots after so #000000 up to #FFFFFF", WALL)
                .push("blocks");
        CFGC.push("colors");
        ClientConfigCyclic.COLLECTOR_ITEM = CFGC.comment("Specify hex color of preview mode.  default #444044").define("collector_item", "#444044");
        ClientConfigCyclic.COLLECTOR_FLUID = CFGC.comment("Specify hex color of preview mode.  default #444044").define("collector_fluid", "#444044");
        ClientConfigCyclic.DETECTOR_ENTITY = CFGC.comment("Specify hex color of preview mode.  default #00FF00").define("detector_entity", "#00FF00");
        ClientConfigCyclic.DETECTOR_ITEM = CFGC.comment("Specify hex color of preview mode.  default #00AA00").define("detector_item", "#00AA00");
        ClientConfigCyclic.PEAT_FARM = CFGC.comment("Specify hex color of preview mode.  default #404040").define("peat_farm", "#404040");
        ClientConfigCyclic.MINER = CFGC.comment("Specify hex color of preview mode.  default #0000AA").define("miner", "#0000AA");
        ClientConfigCyclic.DROPPER = CFGC.comment("Specify hex color of preview mode.  default #AA0011").define("dropper", "#AA0011");
        ClientConfigCyclic.FORESTER = CFGC.comment("Specify hex color of preview mode.  default #11BB00").define("forester", "#11BB00");
        ClientConfigCyclic.HARVESTER = CFGC.comment("Specify hex color of preview mode.  default #00EE00").define("harvester", "#00EE00");
        ClientConfigCyclic.STRUCTURE = CFGC.comment("Specify hex color of preview mode.  default #FF0000").define("structure", "#FF0000");
        CFGC.pop();
        CFGC.push("text");
        ClientConfigCyclic.FLUID_BLOCK_STATUS = CFGC.comment("True means this will hide the fluid contents chat message (right click) on relevant blocks (pylon, fluid generator, fluid hopper, solidifier, sprinkler, tank, cask)").define("FluidContents", true);
        CFGC.pop();
        CFGC.pop(); //end of blocks
        CFGC.comment(WALL, "Item Rendering properties.  Color MUST have one # symbol and then six spots after so #000000 up to #FFFFFF", WALL)
                .push("items");
        CFGC.push("colors");
        ClientConfigCyclic.LOCATION = CFGC.comment("Specify hex color of preview mode for the GPS data card.  default #0000FF").define("location", "#0000FF");
        ClientConfigCyclic.SHAPE_DATA = CFGC.comment("Specify hex color of preview mode.  default #FFC800").define("shape_data", "#FFC800"); // orange
        ClientConfigCyclic.RANDOMIZE_SCEPTER = CFGC.comment("Specify hex color of preview mode.  default #0000FF").define("randomize_scepter", "#00EE00");
        ClientConfigCyclic.OFFSET_SCEPTER = CFGC.comment("Specify hex color of preview mode.  default #0000FF").define("offset_scepter", "#00FF00");
        ClientConfigCyclic.REPLACE_SCEPTER = CFGC.comment("Specify hex color of preview mode.  default #0000FF").define("replace_scepter", "#FFFF00");
        ClientConfigCyclic.BUILD_SCEPTER = CFGC.comment("Specify hex color of preview mode.  default #0000FF").define("build_scepter", "#0000FF");
        CFGC.pop();
        CFGC.pop(); //end of items
        CFGC.pop();
        CLIENT_CONFIG = CFGC.build();
    }

    @SuppressWarnings("unchecked")
    public static List<String> getDisarmIgnoreList() {
        return (List<String>) DISARM_IGNORE_LIST.get();
    }

    public static Map<String, String> getMappedBeheading() {
        Map<String, String> mappedBeheading = new HashMap<String, String>();
        for (String s : BEHEADING_SKINS.get()) {
            try {
                String[] stuff = s.split(":");
                String entity = stuff[0] + ":" + stuff[1];
                String skin = stuff[2];
                mappedBeheading.put(entity, skin);
            }
            catch (Exception e) {
                ModCyclic.LOGGER.error("Beheading Enchantment: Invalid config entry " + s);
            }
        }
        return mappedBeheading;
    }
}
