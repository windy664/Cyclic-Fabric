package net.knsh.cyclic.block.trash;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.flib.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrashBlockEntity extends BlockEntityCyclic implements ImplementedInventory {
    public static final int CAPACITY = (int) (64 * FluidConstants.BUCKET);

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    public final SingleVariantStorage<FluidVariant> tank;

    public TrashBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.TRASH.blockEntity(), pos, state);
        this.tank = new SingleVariantStorage<FluidVariant>() {
            @Override
            protected FluidVariant getBlankVariant() {
                return FluidVariant.blank();
            }

            @Override
            protected long getCapacity(FluidVariant variant) {
                return CAPACITY;
            }

            @Override
            protected void onFinalCommit() {
                setChanged();
            }
        };
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tank.variant = FluidVariant.fromNbt(tag.getCompound("fluidVariant"));
        tank.amount = tag.getLong("fluidamount");
        ContainerHelper.loadAllItems(tag, inventory);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("fluidVariant", tank.variant.toNbt());
        tag.putLong("fluidamount", tank.amount);
        ContainerHelper.saveAllItems(tag, inventory);
        super.saveAdditional(tag);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TrashBlockEntity e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TrashBlockEntity e) {}

    public void tick() {
        if (!inventory.isEmpty()) {
            this.removeItem(0, 64);
            setChanged();
        }

        try (Transaction transaction = Transaction.openOuter()) {
            if (!tank.isResourceBlank()) {
                tank.extract(tank.getResource(), CAPACITY, transaction);
            }
            transaction.commit();
            setChanged();
        }
    }

    @Override
    public void setField(int field, int value) {}

    @Override
    public int getField(int field) {
        return 0;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }
}
