package net.knsh.cyclic.block.tank;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.util.FluidHelpers;
import net.knsh.flib.capabilities.ForgeFluidTankBase;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TileTank extends BlockEntityCyclic {
    public static final long CAPACITY = 64 * FluidConstants.BUCKET;
    public static final long TRANSFER_FLUID_PER_TICK = FluidConstants.BUCKET / 20;
    public ForgeFluidTankBase tank = new ForgeFluidTankBase(this, CAPACITY, p -> true) {
        @Override
        protected void onFinalCommit() {
            TileTank.this.setChanged();
            if (!level.isClientSide) {
                ((ServerLevel)level).getChunkSource().blockChanged(worldPosition);
            }
        }
    };

    public TileTank(BlockPos pos, BlockState state) {
        super(CyclicBlocks.TANK.blockEntity(), pos, state);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileTank e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileTank e) {}

    @Override
    public void load(CompoundTag tag) {
        CompoundTag fluid = tag.getCompound(NBTFLUID);
        tank.readFromNBT(fluid);
        super.load(tag);
        ItemBlockTank.readTank(tag, tank);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        ItemBlockTank.writeTank(tag, tank);
        CompoundTag fluid = new CompoundTag();
        tank.writeToNBT(fluid);
        tag.put(NBTFLUID, fluid);
        super.saveAdditional(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return ItemBlockTank.writeTank(new CompoundTag(), tank);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, blockEntity -> ItemBlockTank.writeTank(new CompoundTag(), tank));
    }

    @Override
    public void setField(int field, int value) {}

    @Override
    public int getField(int field) {
        return 0;
    }

    @Override
    public void setFluid(FluidStack fluid) {
        tank.setFluid(fluid);
    }

    public void tick() {
        //drain below but only to one of myself
        BlockEntity below = this.level.getBlockEntity(this.worldPosition.below());
        if (below instanceof TileTank) {
            FluidHelpers.tryFillPositionFromTank(level, this.worldPosition.below(), Direction.UP, tank, (int) TRANSFER_FLUID_PER_TICK);
        }
    }
}
