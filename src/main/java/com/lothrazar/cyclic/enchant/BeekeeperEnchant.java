package com.lothrazar.cyclic.enchant;

import com.lothrazar.library.enchant.EnchantmentCyclic;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDamageEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec;

public class BeekeeperEnchant extends EnchantmentCyclic {
    public static final String ID = "beekeeper";
    public static ForgeConfigSpec.BooleanValue CFG;

    public BeekeeperEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
        super(rarity, category, applicableSlots);

        LivingEntityEvents.CHANGE_TARGET.register((event -> {
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
        }));

        LivingDamageEvent.DAMAGE.register(event -> {
            if (!isEnabled()) {
                return;
            }
            int level = this.getCurrentArmorLevel(event.getEntity());
            if (level >= 1 && event.getSource() != null
                    && event.getSource().getDirectEntity() != null) {
                // Beekeeper I+
                Entity esrc = event.getSource().getDirectEntity();
                if (esrc.getType() == EntityType.BEE ||
                        esrc.getType() == EntityType.BAT ||
                        esrc.getType() == EntityType.LLAMA_SPIT) {
                    event.setAmount(0);
                }
                if (level >= 2) {
                    if (esrc.getType() == EntityType.PHANTOM) {
                        event.setAmount(0);
                    }
                }
            }
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
        return 2;
    }
}
