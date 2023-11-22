package net.knsh.cyclic.block.hopper;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.block.hoppergold.GoldHopperBlockEntity;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleHopperBlockEntity extends BlockEntityCyclic implements Hopper {
    public SimpleContainer inventory = new SimpleContainer(getContainerSize()) {
        @Override
        public void setChanged() {
            SimpleHopperBlockEntity.this.setChanged();
        }
    };
    private final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);
    private final BlockApiLookup<Storage<ItemVariant>, @Nullable Direction> blockApiLookup = ItemStorage.SIDED;

    public SimpleHopperBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.HOPPER.blockEntity(), pos, state);
    }

    public SimpleHopperBlockEntity(BlockEntityType<GoldHopperBlockEntity> t, BlockPos pos, BlockState state) {
        super(t, pos, state);
    }

    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return null;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {

    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    public Storage<ItemVariant> getStorage() {
        return inventoryWrapper;
    }

    public Direction getTopDirection() {
        return Direction.UP;
    }

    public Direction getBottomDirection(BlockState state) {
        return state.getValue(SimpleHopperBlock.FACING);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            inventory.setItem(i, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, inventory.items);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, inventory.items);
        super.saveAdditional(tag);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, SimpleHopperBlockEntity e) {
        e.tick(level, blockPos, blockState);
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, SimpleHopperBlockEntity e) {}

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.isPowered()) {
            return;
        }

        this.tryPullFromWorld();

        boolean bl;
        bl = extract(level, blockPos, blockState);
        bl |= insert(level, blockPos, blockState);

        if (bl) {
            setChanged(level, blockPos, blockState);
        }
    }

    protected boolean insert(Level level, BlockPos blockPos, BlockState blockState) {
        Direction direction = getBottomDirection(blockState);
        BlockPos targetPos = blockPos.relative(direction);
        Storage<ItemVariant> target = blockApiLookup.find(level, targetPos, direction.getOpposite());

        if (target != null) {
            BlockEntity blockEntityTarget = level.getBlockEntity(targetPos);
            boolean targetIsEmpty = StorageUtil.findStoredResource(target) == null;
            if (StorageUtil.move(
                    getStorage(),
                    target,
                    itemVariant -> true,
                    getFlow(),
                    null
            ) == 1) {
                return true;
            }
        }
        return false;
    }

    protected boolean extract(Level level, BlockPos blockPos, BlockState blockState) {
        Direction direction = getTopDirection();
        BlockPos sourcePos = blockPos.relative(direction);
        Storage<ItemVariant> source = blockApiLookup.find(level, sourcePos, direction.getOpposite());

        if (source != null) {
            long moved = StorageUtil.move(
                    source,
                    getStorage(),
                    itemVariant -> true,
                    getFlow(),
                    null
            );
            return moved >= 1;
        } else {
            return false;
        }
    }

    private void tryPullFromWorld() {
        List<ItemEntity> itemEntityList = HopperBlockEntity.getItemsAtAndAbove(level, this);
        if (itemEntityList.size() > 0) {
            ItemEntity stackEntity = itemEntityList.get(0);
            ItemStack remainder = stackEntity.getItem();
            try (Transaction transaction = Transaction.openOuter()) {
                long amountInserted = getStorage().insert(ItemVariant.of(remainder), remainder.getCount(), transaction);
                remainder.shrink((int) amountInserted);
                transaction.commit();
            }
            if (remainder.isEmpty()) {
                stackEntity.discard();
            }
        }
    }

    public int getFlow() {
        return 1;
    }

    @Override
    public void setField(int field, int value) {}

    @Override
    public int getField(int field) {
        return 0;
    }

    @Override
    public double getLevelX() {
        return this.getBlockPos().getX();
    }

    @Override
    public double getLevelY() {
        return this.getBlockPos().getY();
    }

    @Override
    public double getLevelZ() {
        return this.getBlockPos().getZ();
    }

    @Override
    public void clearContent() {

    }
}
