package net.knsh.cyclic.block;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.knsh.cyclic.library.capabilities.FluidTankBase;
import net.knsh.cyclic.library.core.IHasEnergy;
import net.knsh.cyclic.library.core.IHasFluid;
import net.knsh.cyclic.util.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BlockEntityCyclic extends BlockEntity implements IHasFluid {
    public static final String NBTINV = "inv";
    public static final String NBTFLUID = "fluid";
    public static final String NBTENERGY = "energy";
    public static final int MENERGY = 64 * 1000;
    protected int flowing = 1;
    protected int needsRedstone = 1;
    protected int render = 0;
    protected int timer = 0;

    public BlockEntityCyclic(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getTimer() {
        return timer;
    }

    public boolean requiresRedstone() {
        return this.needsRedstone == 1;
    }

    public boolean isPowered() {
        return this.getLevel().hasNeighborSignal(this.getBlockPos());
    }

    public void setLitProperty(boolean lit) {
        BlockState state = this.getBlockState();
        if (!state.hasProperty(BlockCyclic.LIT)) {
            return;
        }
        //boolean previous = state.get(BlockBreaker.LIT);
        //if (previous != lit) {
        //    this.level.setBlockAndUpdate(worldPosition, st.setValue(BlockBreaker.LIT, lit));
        //}
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void load(CompoundTag tag) {
        flowing = tag.getInt("flowing");
        needsRedstone = tag.getInt("needsRedstone");
        render = tag.getInt("renderParticles");
        timer = tag.getInt("timer");
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("flowing", flowing);
        tag.putInt("needsRedstone", needsRedstone);
        tag.putInt("renderParticles", render);
        tag.putInt("timer", timer);
        super.saveAdditional(tag);
    }

    public void setNeedsRedstone(int value) {
        this.needsRedstone = value % 2;
    }

    public abstract void setField(int field, int value);

    public abstract int getField(int field);

    public void tryExtract(BlockApiLookup<Storage<ItemVariant>, @Nullable Direction> blockApiLookup, InventoryStorage myself, Direction extractSide, int qty) {
        if (extractSide == null) {
            return;
        }
        if (!myself.getSlot(0).isResourceBlank()) {
            return;
        }
        BlockPos posTarget = worldPosition.relative(extractSide);
        BlockEntity tile = level.getBlockEntity(posTarget);
        if (tile != null) {
            Storage<ItemVariant> itemHandlerFrom = blockApiLookup.find(level, posTarget, extractSide.getOpposite());
            if (itemHandlerFrom != null) {
                itemHandlerFrom.forEach((itemVariantStorageView -> {
                    long moved;
                    ItemStack simStack = itemVariantStorageView.getResource().toStack();
                    moved = StorageUtil.simulateExtract(itemVariantStorageView, itemVariantStorageView.getResource(), qty, null);
                    simStack.shrink((int) moved);
                    if (simStack.isEmpty()) {
                        return;
                    }
                    StorageUtil.move(itemHandlerFrom, myself, itemVariant -> true, qty, null);
                }));
            }
        }
    }

    @Override
    public FluidTankBase getFluid() {
        return null;
    }

    @Override
    public void setFluid(FluidVariant fluid) {

    }

    @Override
    public void setFluidAmount(long amount) {

    }
}
