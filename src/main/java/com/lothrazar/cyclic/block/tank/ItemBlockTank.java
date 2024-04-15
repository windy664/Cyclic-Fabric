package com.lothrazar.cyclic.block.tank;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import com.lothrazar.cyclic.util.FluidHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ItemBlockTank extends BlockItem {
    public ItemBlockTank(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    public static void readTank(CompoundTag tag, SingleVariantStorage<FluidVariant> tank) {
        tank.variant = FluidVariant.fromNbt(tag.getCompound("variant"));
        tank.amount = tag.getLong("amount");
    }

    public static CompoundTag writeTank(CompoundTag tag, SingleVariantStorage<FluidVariant> tank) {
        tag.put("variant", tank.variant.toNbt());
        tag.putLong("amount", tank.amount);
        return tag;
    }

    public static SingleVariantStorage<FluidVariant> getFluidStorage(ItemStack stack, ContainerItemContext container) {
        SingleVariantStorage<FluidVariant> tank = new SingleVariantStorage<>() {
            @Override
            protected FluidVariant getBlankVariant() {
                return FluidVariant.blank();
            }

            @Override
            protected long getCapacity(FluidVariant variant) {
                return TileTank.CAPACITY;
            }

            @Override
            public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
                long result = super.insert(insertedVariant, maxAmount, transaction);
                updateItem(transaction);
                return result;
            }

            @Override
            public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
                long result = super.extract(extractedVariant, maxAmount, transaction);
                updateItem(transaction);
                return result;
            }

            private void updateItem(TransactionContext transaction) {
                ItemStack newStack = null;
                if (container.getAmount() > 1) {
                    newStack = stack.copy();
                    newStack.getOrCreateTag().put("BlockEntityTag", writeTank(new CompoundTag(), this));
                } else {
                    stack.getOrCreateTag().put("BlockEntityTag", writeTank(new CompoundTag(), this));
                }
                try (Transaction nested = Transaction.openNested(transaction)) {
                    container.exchange(ItemVariant.of(stack), Long.MAX_VALUE, nested);
                    if (newStack != null) {
                        container.extract(ItemVariant.of(stack), 1, nested);
                        container.insert(ItemVariant.of(newStack), 1, nested);
                    }
                    nested.commit();
                }
            }
        };
        if (stack.hasTag() && stack.getOrCreateTag().contains("BlockEntityTag")) {
            readTank(stack.getOrCreateTag().getCompound("BlockEntityTag"), tank);
        }
        return tank;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        FluidStack fstack = copyFluidFromStack(stack);
        return fstack != null && fstack.getAmount() > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        try {
            //this is always null
            FluidStack fstack = copyFluidFromStack(stack);
            float current = fstack.getAmount();
            float max = TileTank.CAPACITY;
            return Math.round(13.0F * current / max);
        }
        catch (Throwable e) {
            //lazy
        }
        return 1;
    }

    public static FluidStack copyFluidFromStack(ItemStack stack) {
        ContainerItemContext ctx = ContainerItemContext.withConstant(stack);
        if (stack.getTag() != null) {
            SingleVariantStorage<FluidVariant> handler = getFluidStorage(stack, ctx);
            return new FluidStack(handler.variant, handler.amount);
        }
        return null;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        FluidStack fstack = copyFluidFromStack(stack);
        return FluidHelpers.getColorFromFluid(fstack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ContainerItemContext ctx = ContainerItemContext.withConstant(stack);
        Storage<FluidVariant> storage = ctx.find(FluidStorage.ITEM);
        if (storage != null && storage instanceof SingleVariantStorage<FluidVariant> handler) {
            FluidStack fs = new FluidStack(handler.variant, handler.amount);
            if (fs != null && !fs.isEmpty()) {
                MutableComponent t = Component.translatable(
                        fs.getDisplayName().getString()
                                + " " + fs.getAmount()
                                + "/" + TileTank.CAPACITY);
                t.withStyle(ChatFormatting.GRAY);
                tooltip.add(t);
            }
        }
    }
}
