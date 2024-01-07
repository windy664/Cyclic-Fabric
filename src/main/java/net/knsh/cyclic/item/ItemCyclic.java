package net.knsh.cyclic.item;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.knsh.flib.cap.CustomEnergyStorageUtil;
import net.knsh.cyclic.lookups.types.ItemShareTag;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;

public class ItemCyclic extends Item implements ItemShareTag {
    private static final int MAX_ENERGY = 16000;
    public static final String ENERGYTTMAX = "energyttmax";
    public static final String ENERGYTT = "energytt";
    public static final float INACCURACY_DEFAULT = 1.0F;
    public static final float VELOCITY_MAX = 1.5F;
    private boolean hasEnergy;

    public ItemCyclic(Properties properties) {
        super(properties);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
        // energy stuff here later
    }

    @Environment(EnvType.CLIENT)
    public void registerClient() {}

    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        if (hasEnergy) {
            CompoundTag nbt = stack.getOrCreateTag();
            EnergyStorage storage = EnergyStorage.ITEM.find(stack, null);
            if (storage != null) {
                nbt.putLong(ENERGYTT, storage.getAmount());
                nbt.putLong(ENERGYTTMAX, storage.getCapacity());
            }
            return nbt;
        }
        return null;
    }

    public static ItemStackHandler getInventoryFromTag(ItemStack itemStack, ItemStackHandler inventory) {
        if (itemStack.getTag() == null) {
            itemStack.setTag(inventory.serializeNBT());
        } else {
            inventory.deserializeNBT(itemStack.getTag());
        }
        return inventory;
    }

    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt) {
        if (hasEnergy && nbt != null) {
            final CompoundTag stackTag = stack.getOrCreateTag();
            final int serverEnergyValue = nbt.getInt(ENERGYTT);
            stackTag.putInt(ENERGYTT, serverEnergyValue);
            stackTag.putInt(ENERGYTTMAX, nbt.getInt(ENERGYTTMAX));
            final EnergyStorage storage = EnergyStorage.ITEM.find(stack, null);
            if (storage instanceof SimpleEnergyStorage energy) {
                CustomEnergyStorageUtil.setEnergy(serverEnergyValue, energy);
            }
        }
    }
}
