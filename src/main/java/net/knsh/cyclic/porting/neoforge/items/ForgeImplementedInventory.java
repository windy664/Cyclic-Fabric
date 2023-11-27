package net.knsh.cyclic.porting.neoforge.items;

import net.knsh.cyclic.library.ImplementedInventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A simple extension to {@code ImplementedInventory} with ported methods from NeoForge's ItemStackHandler
 *
 * @author KnownSH
 */
@FunctionalInterface
public interface ForgeImplementedInventory extends ImplementedInventory {
    default boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    default void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getItems().size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getItems().size() + ")");
    }

    default int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(64, stack.getMaxStackSize());
    }

    /**
     * Inserts an ItemStack into the given slot and return the remainder. The ItemStack should not be modified in this function!
     *
     * @param slot Slot to insert the itemstack into
     * @param stack Itemstack that should be inserted
     * @param simulate If true, only simulates item insertion
     * @return Items that couldn't be inserted into the slot
     */
    @NotNull
    default ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack existing = this.getItems().get(slot);
        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.getItems().set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            setChanged();
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    /**
     * Extracts an ItemStack from the given slot.
     *
     * @param slot Slot to extract from.
     * @param amount Amount to extract (may be greater than the current stack's max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be empty if nothing can be extracted. The returned ItemStack can
     * be safely modified after, so item handlers should return a new or copied stack.
     */
    @NotNull
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.getItems().get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.getItems().set(slot, ItemStack.EMPTY);
                setChanged();
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.getItems().set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                setChanged();
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }
}
