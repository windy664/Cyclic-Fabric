package com.lothrazar.cyclic.lookups.types;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ItemShareTag {
    default CompoundTag getShareTag(ItemStack stack) {
        return null;
    }

    void readShareTag(ItemStack stack, CompoundTag nbt);
}
