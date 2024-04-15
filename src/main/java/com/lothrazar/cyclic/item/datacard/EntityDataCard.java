package com.lothrazar.cyclic.item.datacard;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.lothrazar.cyclic.item.ItemCyclic;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityDataCard extends ItemCyclic {
    private static final String ENTITY_DATA = "entity_data";
    private static final String ENTITY_KEY = "entity_key";

    public EntityDataCard(Properties properties) {
        super(properties);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (stack.hasTag()) {
            MutableComponent t = Component.translatable(stack.getTag().getString(ENTITY_KEY));
            t.withStyle(ChatFormatting.GRAY);
            tooltipComponents.add(t);
        }
        else {
            super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.isCrouching()) {
            CompoundTag atag = player.getItemInHand(usedHand).getOrCreateTag();
            //atag.put(ENTITY_DATA, ...); TODO Add later when components are added
            atag.putString(ENTITY_KEY, "player");
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand usedHand) {
        player.swing(usedHand);
        CompoundTag tag = stack.getOrCreateTag();
        if (target instanceof Player) {
            tag.putString(ENTITY_KEY, "player");
        } else {
            String key = EntityType.getKey(target.getType()).toString();
            tag.putString(ENTITY_KEY, key);
        }
        stack.setTag(tag);
        return super.interactLivingEntity(stack, player, target, usedHand);
    }

    public static boolean matchesEntity(Entity etar, ItemStack stack) {
        if (etar == null || !hasEntity(stack)) {
            return false;
        }
        final EntityType<?> type = getEntityType(stack);
        return type == etar.getType();
    }

    private static EntityType<?> getEntityType(ItemStack stack) {
        if (stack.getItem() instanceof EntityDataCard) {
            final String key = stack.getTag().getString(ENTITY_KEY);
            return EntityType.byString(key).orElse(null);
        }
        return null;
    }

    public static boolean hasEntity(ItemStack stack) {
        if (stack.getItem() instanceof EntityDataCard) {
            return stack.hasTag() && stack.getTag().contains(ENTITY_KEY);
        }
        return false;
    }
}
