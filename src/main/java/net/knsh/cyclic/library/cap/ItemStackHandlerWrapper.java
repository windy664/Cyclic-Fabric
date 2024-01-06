package net.knsh.cyclic.library.cap;

import io.github.fabricators_of_create.porting_lib.core.util.INBTSerializable;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemStackHandlerWrapper implements SlottedStackStorage, INBTSerializable<CompoundTag> {
    public static final String NBT_INPUT = "Input";
    public static final String NBT_OUTPUT = "Output";
    protected final ItemStackHandler input;
    protected final ItemStackHandler output;

    public ItemStackHandlerWrapper(ItemStackHandler input, ItemStackHandler output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Calls with the correct handler, slot for the handler and if it matches the input handler.
     */
    protected <T> T withHandler(int externalSlot, HandlerCallback<T> callback) {
        int numInputSlots = input.getSlotCount();
        boolean isInput = externalSlot < numInputSlots;
        int internalSlot = isInput ? externalSlot : externalSlot - numInputSlots;
        ItemStackHandler handler = isInput ? input : output;
        return callback.apply(handler, internalSlot, isInput);
    }

    /**
     * For functions that return void.
     *
     * @see ItemStackHandlerWrapper#withHandler(int, HandlerCallback)
     */
    protected void withHandlerV(int slot, HandlerCallbackVoid func) {
        withHandler(slot, (h, s, isInput) -> {
            func.apply(h, s, isInput);
            return false; // Because generics can't be void >.<
        });
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag cmp = new CompoundTag();
        cmp.put(NBT_INPUT, input.serializeNBT());
        cmp.put(NBT_OUTPUT, output.serializeNBT());
        return cmp;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        input.deserializeNBT(nbt.getCompound(NBT_INPUT));
        output.deserializeNBT(nbt.getCompound(NBT_OUTPUT));
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return withHandler(slot, ((handler, slot1, isInput) -> handler.getStackInSlot(slot1)));
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        withHandlerV(slot, ((handler, slot1, isInput) -> handler.setStackInSlot(slot1, stack)));
    }

    @Override
    public int getSlotLimit(int slot) {
        return withHandler(slot, ((handler, slot1, isInput) -> handler.getSlotLimit(slot1)));
    }

    @Override
    public int getSlotCount() {
        return input.getSlotCount() + output.getSlotCount();
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return withHandler(slot, ((handler, slot1, isInput) -> handler.getSlot(slot1)));
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return input.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return output.extract(resource, maxAmount, transaction);
    }

    @Override
    public long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return withHandler(slot, ((handler, slot1, isInput) -> isInput ? handler.insertSlot(slot1, resource, maxAmount, transaction) : 0));
    }

    @Override
    public long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return withHandler(slot, ((handler, slot1, isInput) -> isInput ? 0 : handler.extractSlot(slot1, resource, maxAmount, transaction)));
    }

    @FunctionalInterface
    protected interface HandlerCallback<T> {
        T apply(ItemStackHandler handler, int slot, boolean isInput);
    }

    @FunctionalInterface
    protected interface HandlerCallbackVoid {
        void apply(ItemStackHandler handler, int slot, boolean isInput);
    }
}
