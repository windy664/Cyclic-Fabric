package net.knsh.flib.util;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import java.util.List;

public class ItemStackUtil {

    public static final String NBT_LORE = "Lore";
    public static final String NBT_DISPLAY = "display";
    //  IItemRenderProperties is IClientBlockExtensions now.
    //hasContainerItem() is hasCraftingRemainingItem()
    //and getContainerItem() is getCraftingRemainingItem() now

    /**
     * example
     *
     * "display": { "Lore": [ "[{\"text\":\"item.enchantingrunes.rune_a\",\"color\":\"gold\"}]" ] },
     *
     * @param crafting
     * @param lore
     * @param color
     */
    public static void addLoreToStack(ItemStack crafting, String lore, String color) {
        CompoundTag displayTag = new CompoundTag();
        ListTag tagList = new ListTag();
        if (color == null) {
            color = "gold";
        }
        String escaped = "{\"text\":\"" + lore + "\",\"color\":\"" + color + "\"}";
        tagList.add(StringTag.valueOf(escaped));
        displayTag.put(NBT_LORE, tagList);
        crafting.getTag().put(NBT_DISPLAY, displayTag);
    }

    public static void applyRandomEnch(RandomSource random, ItemStack crafting) {
        crafting = EnchantmentHelper.enchantItem(random, crafting, 1, false);
    }

    public static void applyRandomEnch(RandomSource random, ItemStack crafting, int level, boolean allowTreasure) {
        applyRandomEnch(random, crafting);
    }
    //  private void merge(Map<Enchantment, Integer> oldEnch, ItemStack crafting) {
    //    Map<Enchantment, Integer> newEnch = EnchantmentHelper.getEnchantments(crafting);
    //    //anything in new thats also in old, merge it over
    //    for (Entry<Enchantment, Integer> newEntry : newEnch.entrySet()) {
    //      //
    //      //if this exists in the old list, merge into new
    //      if (oldEnch.containsKey(newEntry.getKey())) {
    //        //take max of each
    //        newEnch.put(newEntry.getKey(), Math.max(newEntry.getValue(), oldEnch.get(newEntry.getKey())));
    //      }
    //    }
    //    //anything in old thats NOT in new
    //    for (Entry<Enchantment, Integer> oldEntry : oldEnch.entrySet()) {
    //      if (!newEnch.containsKey(oldEntry.getKey())) {
    //        //new list does NOT hvae this thing from old
    //        newEnch.put(oldEntry.getKey(), oldEntry.getValue());
    //      }
    //    }
    //    EnchantmentHelper.setEnchantments(newEnch, crafting);
    //  }

    public static int countEmptySlots(InventoryStorage handler) {
        if (handler == null) {
            return 0;
        }
        int empty = 0;
        for (int i = 0; i < handler.getSlotCount(); i++) {
            if (handler.getSlot(i).getResource().isBlank()) {
                empty++;
            }
        }
        return empty;
    }

    public static ItemStack findItem(String id) {
        Item head = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(id));
        if (head != null) {
            return new ItemStack(head);
        }
        return ItemStack.EMPTY;
    }

    public static void dropAll(InventoryStorage items, Level world, BlockPos pos) {
        if (items == null) {
            return;
        }
        for (int i = 0; i < items.getSlotCount(); i++) {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), items.getSlot(i).getResource().toStack());
        }
    }

    public static void repairItem(ItemStack s) {
        repairItem(s, 1);
    }

    public static void repairItem(ItemStack s, int amount) {
        s.setDamageValue(Math.max(0, s.getDamageValue() - amount));
    }

    public static void damageItem(LivingEntity player, ItemStack stack) {
        damageItem(player, stack, InteractionHand.MAIN_HAND);
    }

    public static void damageItem(LivingEntity player, ItemStack stack, InteractionHand hand) {
        if (!stack.isDamageableItem()) {
            //unbreakable
            return;
        }
        if (player == null) {
            stack.setDamageValue(stack.getDamageValue() + 1);
        }
        else {
            stack.hurtAndBreak(1, player, (p) -> {
                p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
            });
        }
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            stack.shrink(1);
            stack = ItemStack.EMPTY;
        }
    }

    public static void damageItemRandomly(LivingEntity player, ItemStack stack) {
        if (player.level().random.nextDouble() < 0.001) {
            damageItem(player, stack);
        }
    }

    public static void drop(Level world, BlockPos pos, Block drop) {
        if (!world.isClientSide) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(drop.asItem())));
        }
    }

    public static void drop(Level world, BlockPos pos, ItemStack drop) {
        if (!world.isClientSide) {
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), drop));
        }
    }

    public static boolean matches(ItemStack current, ItemStack in) {
        //first one fails if size is off
        return ItemStack.matches(current, in)
                && ItemStack.isSameItemSameTags(current, in);
    }

    public static void shrink(Player player, ItemStack stac) {
        if (!player.isCreative()) {
            stac.shrink(1);
        }
    }

    public static void drop(Level world, BlockPos center, List<ItemStack> lootDrops) {
        for (ItemStack dropMe : lootDrops) {
            ItemStackUtil.drop(world, center, dropMe);
        }
    }

    public static void dropItemStackMotionless(Level world, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!world.isClientSide) {
            ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
            world.addFreshEntity(entityItem);
            entityItem.setDeltaMovement(0, 0, 0);
        }
    }

    /**
     * Preserve damage but delete the rest of the tag
     *
     * @param itemstack
     */
    public static void deleteTag(ItemStack itemstack) {
        int dmg = itemstack.getDamageValue();
        itemstack.setTag(null);
        itemstack.setDamageValue(dmg);
    }

    /**
     * call from ::inventoryTick
     *
     * @param rnd
     * @param stack
     * @param factor
     */
    public static void randomlyRepair(RandomSource rnd, ItemStack stack, int factor) {
        if (stack.isDamaged() && rnd.nextInt(factor) == 0) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }
}
