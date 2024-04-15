package com.lothrazar.cyclic.block.battery;

import com.lothrazar.cyclic.lookups.CyclicItemLookup;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import com.lothrazar.cyclic.registry.CyclicTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class BatteryBlockItem extends BlockItem implements BatteryImplementation {
    public static final String ENERGYTTMAX = "energyttmax";
    public static final String ENERGYTT = "energytt";
    public final CapabilityProviderEnergyStack energyCap = new CapabilityProviderEnergyStack(BatteryBlockEntity.MAX);

    public BatteryBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        EnergyStorage energy = CyclicItemLookup.BATTERY_ITEM.find(stack, null).getBattery();
        return energy != null && energy.getAmount() > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float current = 0;
        float max = 0;
        EnergyStorage energy = CyclicItemLookup.BATTERY_ITEM.find(stack, null).getBattery();
        if (energy != null) {
            current = energy.getAmount();
            max = energy.getCapacity();
        } else if (stack.hasTag() && stack.getTag().contains(ENERGYTT)) {
            current = stack.getTag().getLong(ENERGYTT);
            max = stack.getTag().getLong(ENERGYTTMAX);
        }
        return (max == 0) ? 0 : Math.round(13.0F * current / max);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return CyclicTextures.COLOUR_RF_BAR;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        long current = 0;
        long energyttmax = 0;
        EnergyStorage storage = CyclicItemLookup.BATTERY_ITEM.find(stack, null).getBattery();
        if (storage != null) {
            current = storage.getAmount();
            energyttmax = storage.getCapacity();
            tooltipComponents.add(Component.translatable(current + "/" + energyttmax).withStyle(ChatFormatting.RED));
        }
        else if (stack.hasTag() && stack.getTag().contains(ENERGYTT)) {
            //TODO 1.19 port  delete this branch
            current = stack.getTag().getInt(ENERGYTT);
            energyttmax = stack.getTag().getInt(ENERGYTTMAX);
            tooltipComponents.add(Component.translatable(current + "/" + energyttmax).withStyle(ChatFormatting.BLUE));
        }
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return energyCap.energy.insert(maxAmount, transaction);
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return energyCap.energy.extract(maxAmount, transaction);
    }

    @Override
    public long getAmount() {
        return energyCap.energy.getAmount();
    }

    @Override
    public long getCapacity() {
        return energyCap.energy.getCapacity();
    }

    @Override
    public BatteryImplementation getBattery() {
        return this;
    }
}
