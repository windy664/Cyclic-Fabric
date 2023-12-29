package net.knsh.cyclic.enchant;

import net.knsh.cyclic.library.enchant.EnchantmentCyclic;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class BeekeeperEnchant extends EnchantmentCyclic {
    public static final String ID = "beekeeper";
    public static ForgeConfigSpec.BooleanValue CFG;

    public BeekeeperEnchant() {
        super(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
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
        return 2;
    }

    @SubscribeEvent
    public void onEntityTick(LivingChangeTargetEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (event.getOriginalTarget() instanceof Player && event.getEntity().getType() == EntityType.BEE && event.getEntity() instanceof Bee bee) {
            int level = this.getCurrentArmorLevel(event.getOriginalTarget());
            if (level > 0) {
                event.setCanceled(true);
                bee.setAggressive(false);
                bee.setRemainingPersistentAngerTime(0);
                bee.setPersistentAngerTarget(null);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDamageEvent(LivingDamageEvent event) {
        if (!isEnabled()) {
            return;
        }
        int level = this.getCurrentArmorLevel(event.getEntity());
        if (level >= 1 && event.getSource() != null && event.getSource().getDirectEntity() != null) {
            Entity esrc = event.getSource().getDirectEntity();
            if (esrc.getType() == EntityType.BEE ||
                    esrc.getType() == EntityType.BAT ||
                    esrc.getType() == EntityType.LLAMA_SPIT) {
                event.setAmount(0);
            }
            if (level >= 2) {
                //Beekeeper II+
                //all of level I and also
                if (esrc.getType() == EntityType.PHANTOM) {
                    event.setAmount(0);
                }
            }
        }
    }
}
