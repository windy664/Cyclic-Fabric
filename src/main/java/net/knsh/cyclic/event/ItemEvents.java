package net.knsh.cyclic.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.cable.CableBase;
import net.knsh.cyclic.enchant.TravellerEnchant;
import net.knsh.cyclic.event.fabric.BeforeDamageCallback;
import net.knsh.cyclic.library.util.EnchantUtil;
import net.knsh.cyclic.library.util.SoundUtil;
import net.knsh.cyclic.registry.CyclicEnchants;
import net.knsh.cyclic.registry.CyclicItems;
import net.knsh.cyclic.registry.CyclicSounds;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;

public class ItemEvents {
    public static void register() {
        UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> {
            Cyclic.LOGGER.info(String.valueOf(world.isClientSide));
            if (player.getItemInHand(hand).isEmpty()) return InteractionResult.PASS;

            //TODO scaffolding

            if (player.isCrouching() && player.getItemInHand(hand).is(CyclicItems.CABLE_WRENCH)) {
                if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof CableBase) {
                    player.swing(hand);
                    CableBase.crouchClick(world.getBlockState(hitResult.getBlockPos()), world, hitResult.getBlockPos(), player, hitResult);
                    SoundUtil.playSound(player, CyclicSounds.THUNK, 0.2F, 1F);
                }
            }
            return InteractionResult.PASS;
        }));

        BeforeDamageCallback.BEFORE_DAMAGE.register((source, amount) -> {
            if (!TravellerEnchant.CFG.get()) return amount;
            if (source.getEntity() instanceof LivingEntity livingEntity) {
                int level = EnchantUtil.getCurrentLevelSlot(livingEntity, EquipmentSlot.LEGS, CyclicEnchants.TRAVELLER);
                if (level > 0) {
                    DamageSources sources = livingEntity.level().damageSources();
                    if (source == sources.cactus() || source == sources.flyIntoWall() || source == sources.sweetBerryBush() || source == sources.sting(null)) {
                        return 0.1F;
                    }
                    if (source == sources.fall()) {
                        if (livingEntity.fallDistance <= 8) {
                            return 0.1F;
                        }
                    } else if (livingEntity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem) {
                        if (amount > livingEntity.getHealth() - 0.5F) {
                            return livingEntity.getHealth() - 1F;
                        }
                    }
                }
            }
            return amount;
        });
    }
}
