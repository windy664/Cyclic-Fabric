package net.knsh.cyclic.block.wireless.fluid;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.data.PreviewOutlineType;
import net.knsh.cyclic.item.datacard.LocationGpsCard;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.flib.capabilities.FluidTankBase;
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

public class TileWirelessFluid extends BlockEntityCyclic implements ExtendedScreenHandlerFactory {
    enum Fields {
        RENDER, TRANSFER_RATE, REDSTONE;
    }

    public static final long CAPACITY = 64 * FluidConstants.BUCKET;
    static final long MAX = 64000;
    public static final long MAX_TRANSFER = MAX;
    private long transferRate = FluidConstants.BUCKET;
    public FluidTankBase tank = new FluidTankBase(this,CAPACITY, f -> true) {
        @Override
        protected void onFinalCommit() {
            TileWirelessFluid.this.setChanged();
        }
    };
    public ItemStackHandler gpsSlots = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemVariant stack) {
            return stack.getItem() instanceof LocationGpsCard;
        }
    };

    public TileWirelessFluid(BlockPos pos, BlockState state) {
        super(CyclicBlocks.WIRELESS_FLUID.blockEntity(), pos, state);
        this.needsRedstone = 0;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileWirelessFluid e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileWirelessFluid e) {
        e.tick();
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.WIRELESS_FLUID.block().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new ContainerWirelessFluid(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public void load(CompoundTag tag) {
        gpsSlots.deserializeNBT(tag.getCompound(NBTINV));
        this.transferRate = tag.getLong("transferRate");
        tank.readFromNBT(tag.getCompound(NBTFLUID));
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putLong("transferRate", transferRate);
        tag.put(NBTINV, gpsSlots.serializeNBT());
        CompoundTag fluid = new CompoundTag();
        tank.writeToNBT(fluid);
        tag.put(NBTFLUID, fluid);
        super.saveAdditional(tag);
    }

    @Override
    public FluidStack getFluid() {
        return tank == null ? FluidStack.EMPTY : tank.getFluid();
    }

    @Override
    public void setFluid(FluidStack fluid) {
        tank.setFluid(fluid);
    }

    //  @Override
    public void tick() {
        this.syncEnergy();
        if (this.requiresRedstone() && !this.isPowered()) {
            setLitProperty(false);
            return;
        }
        if (level.isClientSide) {
            return;
        }
        boolean moved = false;
        //run the transfer. one slot only
        BlockPosDim loc = getTargetInSlot(0);
        if (loc != null && LevelWorldUtil.dimensionIsEqual(loc, level)) {
            this.moveFluids(loc.getSide(), loc.getPos(), (int) this.transferRate, tank);
        }
        this.setLitProperty(moved);
    }

    BlockPosDim getTargetInSlot(int s) {
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
                transferRate = value;
                break;
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
                return (int) this.transferRate;
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
