package com.lothrazar.cyclic.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.lothrazar.cyclic.porting.neoforge.items.ItemHandlerHelper;
import com.lothrazar.cyclic.registry.CyclicEnchants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(Block.class)
public class BlockMixin {
    @ModifyReturnValue(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"))
    private static List<ItemStack> getDrops(List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool) {
        if (EnchantmentHelper.getItemEnchantmentLevel(CyclicEnchants.AUTOSMELT, tool) < 1) return original;

        List<ItemStack> drops = new ArrayList<>();
        original.forEach((stack) -> {
            Optional<SmeltingRecipe> optional = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level);
            if (optional.isPresent()) {
                ItemStack smelted = optional.get().getResultItem(level.registryAccess());
                if (!smelted.isEmpty()) {
                    ItemStack copy = ItemHandlerHelper.copyStackWithSize(smelted, stack.getCount() * smelted.getCount());
                    drops.add(copy);
                } else {
                    drops.add(stack);
                }
            } else {
                drops.add(stack);
            }
        });
        return drops;
    }
}
