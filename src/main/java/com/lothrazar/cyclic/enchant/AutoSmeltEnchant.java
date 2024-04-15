package com.lothrazar.cyclic.enchant;

import com.google.common.base.Suppliers;
import com.lothrazar.library.enchant.EnchantmentCyclic;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.LootModifier;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Optional;
import java.util.function.Supplier;

public class AutoSmeltEnchant extends EnchantmentCyclic {
    public static final String ID = "auto_smelt";
    public static ForgeConfigSpec.BooleanValue CFG;

    public AutoSmeltEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
        super(rarity, category, applicableSlots);
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
    public boolean isEnabled() {
        return CFG.get();
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected boolean checkCompatibility(Enchantment ench) {
        return ench != Enchantments.SILK_TOUCH && ench != Enchantments.BLOCK_FORTUNE && super.checkCompatibility(ench);
    }

    public static class EnchantAutoSmeltModifier extends LootModifier {
        public static final Supplier<Codec<EnchantAutoSmeltModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, EnchantAutoSmeltModifier::new)));

        public EnchantAutoSmeltModifier(LootItemCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @Override
        protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> originalLoot, LootContext context) {
            ObjectArrayList<ItemStack> newLoot = new ObjectArrayList<>();
            originalLoot.forEach((stack) -> {
                Optional<SmeltingRecipe> optional = context.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), context.getLevel());
                if (optional.isPresent()) {
                    ItemStack smeltedItemStack = optional.get().getResultItem(context.getLevel().registryAccess());
                    if (!smeltedItemStack.isEmpty()) {
                        ItemStack copy = ItemHandlerHelper.copyStackWithSize(smeltedItemStack, stack.getCount() * smeltedItemStack.getCount());
                        newLoot.add(copy);
                    }
                    else {
                        newLoot.add(stack);
                    }
                }
                else {
                    newLoot.add(stack);
                }
            });
            return newLoot;
        }

        @Override
        public Codec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }
}
