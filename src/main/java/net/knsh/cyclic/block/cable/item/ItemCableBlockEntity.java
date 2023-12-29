package net.knsh.cyclic.block.cable.item;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.block.cable.CableBase;
import net.knsh.cyclic.block.cable.EnumConnectType;
import net.knsh.cyclic.porting.neoforge.items.ForgeImplementedInventory;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.util.UtilDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemCableBlockEntity extends BlockEntityCyclic implements ExtendedScreenHandlerFactory, ForgeImplementedInventory {
    private static final int FLOW_QTY = 64; // fixed, for non-extract motion
    private int extractQty = FLOW_QTY; // default
    private final BlockApiLookup<Storage<ItemVariant>, @Nullable Direction> blockApiLookup = ItemStorage.SIDED;
    private Map<Direction, InventoryStorage> flow = new ConcurrentHashMap<>();
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    public ItemCableBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.ITEM_PIPE.blockEntity(), pos, state);
        for (Direction f : Direction.values()) {
            flow.put(f, ItemCableBlockEntity.createHandler());
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, ItemCableBlockEntity e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, ItemCableBlockEntity e) {
        e.tick();
    }

    private static InventoryStorage createHandler() {
        return InventoryStorage.of(new SimpleContainer(1), null);
    }

    private void tick() {
        for (Direction extractSide : Direction.values()) {
            EnumConnectType connection = this.getBlockState().getValue(CableBase.FACING_TO_PROPERTY_MAP.get(extractSide));
            if (connection.isExtraction()) {
                final InventoryStorage container = flow.get(extractSide);
                if (container != null) {
                    BlockPos targetPos = getBlockPos().relative(extractSide);
                    Storage<ItemVariant> storage = blockApiLookup.find(level, targetPos, extractSide.getOpposite());
                    if (storage != null) {
                        BlockEntity blockEntityTarget = level.getBlockEntity(targetPos);
                        StorageUtil.move(
                                storage,
                                container,
                                itemVariant -> true,
                                FLOW_QTY,
                                null
                        );
                    }
                }
            }
        }
        normalFlow();
    }

    private void normalFlow() {
        // Label for loop for shortcutting, used to continue after items have been moved
        incomingSideLoop: for (final Direction incomingSide : Direction.values()) {
            //in all cases sideHandler is required
            final InventoryStorage sideHandler = flow.get(incomingSide);
            for (final Direction outgoingSide : UtilDirection.getAllInDifferentOrder()) {
                if (outgoingSide == incomingSide) {
                    continue;
                }
                EnumConnectType outgoingConnection = this.getBlockState().getValue(CableBase.FACING_TO_PROPERTY_MAP.get(outgoingSide));
                if (outgoingConnection.isExtraction() || outgoingConnection.isBlocked()) {
                    continue;
                }
                if (this.pushItems(outgoingSide, FLOW_QTY, sideHandler)) {
                    continue incomingSideLoop; //if items have been moved then change side
                }
            }
            //if no items have been moved then move items in from adjacent
            this.pushItems(incomingSide, FLOW_QTY, sideHandler);
        }
    }

    private boolean pushItems(Direction direction, int amount, InventoryStorage container) {
        BlockPos sourcePos = getBlockPos().relative(direction);
        Storage<ItemVariant> storage = blockApiLookup.find(level, sourcePos, direction.getOpposite());

        if (storage != null) {
            long moved = StorageUtil.move(
                    container,
                    storage,
                    itemVariant -> true,
                    amount,
                    null
            );
            return moved >= 1;
        }
        return false;
    }

    public Map<Direction, InventoryStorage> getFlow() {
        return flow;
    }

    @Override
    public void load(CompoundTag tag) {
        extractQty = tag.getInt("extractCount");
        ContainerHelper.loadAllItems(tag, inventory);
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("extractCount", extractQty);
        ContainerHelper.saveAllItems(tag, inventory);
        super.saveAdditional(tag);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public void setField(int field, int value) {
        this.extractQty = value;
    }

    @Override
    public int getField(int field) {
        return this.extractQty;
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.ITEM_PIPE.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new ItemCableContainer(i, playerInventory, this, level, worldPosition);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }
}
