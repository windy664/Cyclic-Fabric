package com.lothrazar.cyclic.enchant;

import com.lothrazar.cyclic.compat.CompatConstants;
import com.lothrazar.cyclic.config.ConfigRegistry;
import com.lothrazar.library.enchant.EnchantmentCyclic;
import com.lothrazar.library.util.ItemStackUtil;
import com.lothrazar.library.util.TagDataUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BeheadingEnchant extends EnchantmentCyclic {
    public static ForgeConfigSpec.IntValue PERCDROP;
    public static ForgeConfigSpec.IntValue PERCPERLEVEL;
    public static final String ID = "beheading";
    public static ForgeConfigSpec.BooleanValue CFG;

    public BeheadingEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
        super(rarity, category, applicableSlots);

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (!isEnabled()) {
                return true;
            }
            if (damageSource.getEntity() instanceof Player attacker) {
                int level = getCurrentLevelTool(attacker);
                if (level <= 0) {
                    return true;
                }
                Level world = attacker.level();
                if (Mth.nextInt(world.random, 0, 100) > percentForLevel(level)) {
                    return true;
                }
                if (entity == null) {
                    return true;
                }
                BlockPos pos = entity.blockPosition();
                if (entity instanceof Player) {
                    //player head
                    ItemStackUtil.drop(world, pos, TagDataUtil.buildNamedPlayerSkull((Player) entity));
                    return true;
                }
                @Nullable
                ResourceLocation type = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
                String key = type.toString();
                Map<String, String> mappedBeheading = ConfigRegistry.getMappedBeheading();
                if (entity.getType() == EntityType.ENDER_DRAGON) {
                    ItemStackUtil.drop(world, pos, new ItemStack(Items.DRAGON_HEAD));
                }
                else if (entity.getType() == EntityType.CREEPER) {
                    ItemStackUtil.drop(world, pos, new ItemStack(Items.CREEPER_HEAD));
                }
                else if (entity.getType() == EntityType.ZOMBIE) {
                    ItemStackUtil.drop(world, pos, new ItemStack(Items.ZOMBIE_HEAD));
                }
                else if (entity.getType() == EntityType.SKELETON) {
                    ItemStackUtil.drop(world, pos, new ItemStack(Items.SKELETON_SKULL));
                }
                else if (entity.getType() == EntityType.WITHER_SKELETON) {
                    ItemStackUtil.drop(world, pos, new ItemStack(Items.WITHER_SKELETON_SKULL));
                }
                else if (entity.getType() == EntityType.WITHER) { //Drop number of heads equal to level of enchant [1,3]
                    ItemStackUtil.drop(world, pos, new ItemStack(Items.WITHER_SKELETON_SKULL, Math.max(level, 3)));
                }
                else if (FabricLoader.getInstance().isModLoaded(CompatConstants.TCONSTRUCT)) { // Hephaestus Fabric
                    String id = CompatConstants.TCONSTRUCT;
                    ItemStack tFound = ItemStack.EMPTY;
                    if (entity.getType() == EntityType.DROWNED) {
                        tFound = ItemStackUtil.findItem(id + ":drowned_head");
                    }
                    else if (entity.getType() == EntityType.HUSK) {
                        tFound = ItemStackUtil.findItem(id + ":husk_head");
                    }
                    else if (entity.getType() == EntityType.ENDERMAN) {
                        tFound = ItemStackUtil.findItem(id + ":enderman_head");
                    }
                    else if (entity.getType() == EntityType.SPIDER) {
                        tFound = ItemStackUtil.findItem(id + ":spider_head");
                    }
                    else if (entity.getType() == EntityType.CAVE_SPIDER) {
                        tFound = ItemStackUtil.findItem(id + ":cave_spider_head");
                    }
                    else if (entity.getType() == EntityType.STRAY) {
                        tFound = ItemStackUtil.findItem(id + ":stray_head");
                    }
                    else if (entity.getType() == EntityType.BLAZE) {
                        tFound = ItemStackUtil.findItem(id + ":blaze_head");
                    }
                    if (!tFound.isEmpty()) {
                        ItemStackUtil.drop(world, pos, tFound);
                        return true;
                    }
                }
                else if (mappedBeheading.containsKey(key)) {
                    //otherwise not a real mob, try the config last
                    ItemStackUtil.drop(world, pos, TagDataUtil.buildNamedPlayerSkull(mappedBeheading.get(key)));
                }
            }
            return true;
        });
    }

    @Override
    public boolean isEnabled() {
        return CFG.get();
    }

    @Override
    public boolean isTradeable() {
        return isEnabled() && super.isTradeable();
    }

    @Override
    public boolean isDiscoverable() {
        return isEnabled() && super.isDiscoverable();
    }

    @Override
    public boolean isAllowedOnBooks() {
        return isEnabled() && super.isAllowedOnBooks();
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    private int percentForLevel(int level) {
        return PERCDROP.get() + (level - 1) * PERCPERLEVEL.get();
    }
}
