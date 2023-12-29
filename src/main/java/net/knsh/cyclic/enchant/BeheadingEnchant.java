package net.knsh.cyclic.enchant;

import net.knsh.cyclic.config.ConfigRegistry;
import net.knsh.cyclic.library.enchant.EnchantmentCyclic;
import net.knsh.cyclic.library.util.ItemStackUtil;
import net.knsh.cyclic.library.util.TagDataUtil;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BeheadingEnchant extends EnchantmentCyclic {
    public static ForgeConfigSpec.IntValue PERCDROP;
    public static ForgeConfigSpec.IntValue PERCPERLEVEL;
    public static final String ID = "beheading";
    public static ForgeConfigSpec.BooleanValue CFG;

    public BeheadingEnchant() {
        super(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        NeoForge.EVENT_BUS.register(this);
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

    @SubscribeEvent
    public void onEntityKill(LivingDeathEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (event.getSource().getEntity() instanceof Player) {
            Player attacker = (Player) event.getSource().getEntity();
            int level = getCurrentLevelTool(attacker);
            if (level <= 0) {
                return;
            }
            Level world = attacker.level();
            if (Mth.nextInt(world.random, 0, 100) > percentForLevel(level)) {
                return;
            }
            LivingEntity target = event.getEntity();
            if (target == null) {
                return;
            } //probably wont happen just extra safe
            BlockPos pos = target.blockPosition();
            if (target instanceof Player) {
                //player head
                ItemStackUtil.drop(world, pos, TagDataUtil.buildNamedPlayerSkull((Player) target));
                return;
            }
            //else the random number was less than 10, so it passed the 10% chance req
            @Nullable
            ResourceLocation type = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
            String key = type == null ? "" : type.toString();
            ////we allow all these, which include config, to override the vanilla skulls below
            Map<String, String> mappedBeheading = ConfigRegistry.getMappedBeheading();
            if (target.getType() == EntityType.ENDER_DRAGON) {
                ItemStackUtil.drop(world, pos, new ItemStack(Items.DRAGON_HEAD));
            }
            else if (target.getType() == EntityType.CREEPER) {
                ItemStackUtil.drop(world, pos, new ItemStack(Items.CREEPER_HEAD));
            }
            else if (target.getType() == EntityType.ZOMBIE) {
                ItemStackUtil.drop(world, pos, new ItemStack(Items.ZOMBIE_HEAD));
            }
            else if (target.getType() == EntityType.SKELETON) {
                ItemStackUtil.drop(world, pos, new ItemStack(Items.SKELETON_SKULL));
            }
            else if (target.getType() == EntityType.WITHER_SKELETON) {
                ItemStackUtil.drop(world, pos, new ItemStack(Items.WITHER_SKELETON_SKULL));
            }
            else if (target.getType() == EntityType.WITHER) { //Drop number of heads equal to level of enchant [1,3]
                ItemStackUtil.drop(world, pos, new ItemStack(Items.WITHER_SKELETON_SKULL, Math.max(level, 3)));
            }
            else if (mappedBeheading.containsKey(key)) {
                //otherwise not a real mob, try the config last
                ItemStackUtil.drop(world, pos, TagDataUtil.buildNamedPlayerSkull(mappedBeheading.get(key)));
            }
        }
    }
}
