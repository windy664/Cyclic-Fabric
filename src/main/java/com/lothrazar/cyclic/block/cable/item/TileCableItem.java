package com.lothrazar.cyclic.block.cable.item;

import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.registry.CyclicItems;
import io.github.fabricators_of_create.porting_lib.core.util.INBTSerializable;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import com.lothrazar.cyclic.block.BlockEntityCyclic;
import com.lothrazar.cyclic.block.cable.CableBase;
import com.lothrazar.cyclic.block.cable.EnumConnectType;
import com.lothrazar.cyclic.util.UtilDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("UnstableApiUsage")
public class TileCableItem extends BlockEntityCyclic implements ExtendedScreenHandlerFactory {
    private static final int FLOW_QTY = 64; // fixed, for non-extract motion
    private int extractQty = FLOW_QTY; // default
    ItemStackHandler filter = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemVariant resource) {
            return resource.getItem() == CyclicItems.FILTER_DATA;
        }
    };
    public Map<Direction, SlottedStackStorage> flow = new ConcurrentHashMap<>();

    public TileCableItem(BlockPos pos, BlockState state) {
        super(CyclicBlocks.ITEM_PIPE.blockEntity(), pos, state);
        for (Direction f : Direction.values()) {
            flow.put(f, TileCableItem.createHandler());
        }
    }

    public static void serverTick(Level ignoredLevel, BlockPos ignoredblockPos, BlockState ignoredblockState, TileCableItem e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level ignoredlevel, BlockPos ignoredblockPos, BlockState ignoredblockState, TileCableItem e) {
        e.tick();
    }

    private static ItemStackHandler createHandler() {
        return new ItemStackHandler(1);
    }

    public void tick() {
        for (Direction extractSide : Direction.values()) {
            EnumConnectType connection = this.getBlockState().getValue(CableBase.FACING_TO_PROPERTY_MAP.get(extractSide));
            if (connection.isExtraction()) {
                final SlottedStackStorage sideHandler = flow.get(extractSide);
                tryExtract(sideHandler, extractSide, extractQty, filter);
            }
        }
        normalFlow();
    }

    private void normalFlow() {
        // Label for loop for shortcutting, used to continue after items have been moved
        incomingSideLoop: for (final Direction incomingSide : Direction.values()) {
            //in all cases sideHandler is required
            final SlottedStackStorage sideHandler = flow.get(incomingSide);
            for (final Direction outgoingSide : UtilDirection.getAllInDifferentOrder()) {
                if (outgoingSide == incomingSide) {
                    continue;
                }
                EnumConnectType outgoingConnection = this.getBlockState().getValue(CableBase.FACING_TO_PROPERTY_MAP.get(outgoingSide));
                if (outgoingConnection.isExtraction() || outgoingConnection.isBlocked()) {
                    continue;
                }
                if (this.moveItems(outgoingSide, FLOW_QTY, sideHandler)) {
                    continue incomingSideLoop; //if items have been moved then change side
                }
            }
            //if no items have been moved then move items in from adjacent
            this.moveItems(incomingSide, FLOW_QTY, sideHandler);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load(CompoundTag tag) {
        extractQty = tag.getInt("extractCount");
        SlottedStackStorage item;
        for (Direction f : Direction.values()) {
            item = flow.get(f);
            CompoundTag itemTag = tag.getCompound("item" + f.toString());
            ((INBTSerializable<CompoundTag>) item).deserializeNBT(itemTag);
        }
        filter.deserializeNBT(tag.getCompound("filter"));
        super.load(tag);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("filter", filter.serializeNBT());
        tag.putInt("extractCount", extractQty);
        SlottedStackStorage item;
        for (Direction f : Direction.values()) {
            item = flow.get(f);
            CompoundTag compound = ((INBTSerializable<CompoundTag>) item).serializeNBT();
            tag.put("item" + f.toString(), compound);
        }
        super.saveAdditional(tag);
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
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public @NotNull Component getDisplayName() {
        return CyclicBlocks.ITEM_PIPE.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        assert level != null;
        return new ContainerCableItem(i, level, worldPosition, inventory, player);
    }
}
