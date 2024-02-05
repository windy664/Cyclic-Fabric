package net.knsh.cyclic.block.wireless.energy;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.knsh.cyclic.Cyclic;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.block.melter.TileMelter;
import net.knsh.cyclic.data.PreviewOutlineType;
import net.knsh.cyclic.item.datacard.LocationGpsCard;
import net.knsh.cyclic.lookups.CyclicLookup;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicScreens;
import net.knsh.flib.cap.CustomEnergyStorageUtil;
import net.knsh.flib.core.BlockPosDim;
import net.knsh.flib.util.LevelWorldUtil;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.HashSet;
import java.util.Set;

public class TileWirelessEnergy extends BlockEntityCyclic implements ExtendedScreenHandlerFactory {
    enum Fields {
        RENDER, TRANSFER_RATE, REDSTONE;
    }

    public TileWirelessEnergy(BlockPos pos, BlockState state) {
        super(CyclicBlocks.WIRELESS_ENERGY.blockEntity(), pos, state);
        this.needsRedstone = 0;
    }

    static final long MAX = 64000;
    private long transferRate = MAX / 8;
    protected SimpleEnergyStorage energy = new SimpleEnergyStorage(MAX, MAX, MAX) {
        @Override
        protected void onFinalCommit() {
            TileWirelessEnergy.this.setChanged();
            TileWirelessEnergy.this.syncEnergy();
        }

        @Override
        public boolean supportsInsertion() {
            return true;
        }
    };
    ItemStackHandler gpsSlots = new ItemStackHandler(8) {
        @Override
        public boolean isItemValid(int slot, ItemVariant resource) {
            return resource.getItem() instanceof LocationGpsCard;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            TileWirelessEnergy.this.setChanged();
        }
    };

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.WIRELESS_ENERGY.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new ContainerWirelessEnergy(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public void load(CompoundTag tag) {
        gpsSlots.deserializeNBT(tag.getCompound(NBTINV));
        CustomEnergyStorageUtil.deserializeNBT(tag.getCompound(NBTENERGY), energy);
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {;
        tag.put(NBTINV, gpsSlots.serializeNBT());
        tag.put(NBTENERGY, CustomEnergyStorageUtil.serializeNBT(energy));
        super.saveAdditional(tag);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileWirelessEnergy e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileWirelessEnergy e) {
        //   e.tick();
    }

    public void tick() {
        if (this.requiresRedstone() && !this.isPowered()) {
            setLitProperty(false);
            return;
        }
        boolean moved = false;
        //run the transfer. one slot only
        Set<BlockPosDim> used = new HashSet<>();
        for (int slot = 0; slot < gpsSlots.getSlotCount(); slot++) {
            BlockPosDim loc = getTargetInSlot(slot);
            if (used.contains(loc)) {
                continue;
            }
            if (LevelWorldUtil.dimensionIsEqual(loc, level)) {
                EnergyStorage sendTo = EnergyStorage.SIDED.find(level, loc.getPos(), null);
                if (sendTo != null) {
                    long transfered = EnergyStorageUtil.move(
                            this.energy,
                            sendTo,
                            transferRate,
                            null
                    );

                    Cyclic.LOGGER.info(String.valueOf(energy.getAmount()));
                    if (transfered > 0) {
                        used.add(loc);
                        moved = true;
                    }
                }
            }
        }
        this.setLitProperty(moved);
    }

    private BlockPosDim getTargetInSlot(int s) {
        return LocationGpsCard.getPosition(gpsSlots.getStackInSlot(s));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(worldPosition);
    }

    @Override
    public void setField(int field, int value) {
        switch (Fields.values()[field]) {
            case REDSTONE:
                this.needsRedstone = value % 2;
                break;
            case RENDER:
                this.render = value % PreviewOutlineType.values().length;
                break;
            case TRANSFER_RATE:
                //        transferRate = value;
                //      break;
        }
    }

    @Override
    public int getField(int field) {
        switch (Fields.values()[field]) {
            case REDSTONE:
                return this.needsRedstone;
            case RENDER:
                return render;
            case TRANSFER_RATE:
                //        return this.transferRate;
        }
        return 0;
    }

    public float getRed() {
        return 0.89F;
    }

    public float getBlue() {
        return 0;
    }

    public float getGreen() {
        return 0.12F;
    }

    public float getAlpha() {
        return 0.9F;
    }

    public float getThick() {
        return 0.065F;
    }
}
