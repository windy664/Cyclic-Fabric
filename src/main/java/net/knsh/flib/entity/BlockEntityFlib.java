package net.knsh.flib.entity;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.knsh.flib.block.BlockFlib;
import net.knsh.flib.util.EntityUtil;
import net.knsh.flib.util.FakePlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Objects;

public abstract class BlockEntityFlib extends BlockEntity {
    public BlockEntityFlib(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
    }

    public abstract void setField(int field, int value);

    public abstract int getField(int field);

    public void setLitProperty(boolean lit) {
        BlockState st = this.getBlockState();
        if (st.hasProperty(BlockFlib.LIT)) {
            boolean previous = st.getValue(BlockFlib.LIT);
            if (previous != lit) {
                this.level.setBlockAndUpdate(worldPosition, st.setValue(BlockFlib.LIT, lit));
            }
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag syncData = super.getUpdateTag();
        this.saveAdditional(syncData);
        return syncData;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean isPowered() {
        return Objects.requireNonNull(this.getLevel()).hasNeighborSignal(this.getBlockPos());
    }

    public int getRedstonePower() {
        return Objects.requireNonNull(this.getLevel()).getBestNeighborSignal(this.getBlockPos());
    }

    public WeakReference<FakePlayer> buildFakePlayer(ServerLevel sw, final String name, final Direction facing) {
        WeakReference<FakePlayer> fakePlayer = FakePlayerUtil.initFakePlayer(sw, name);
        if (fakePlayer == null) return null;
        fakePlayer.get().setPos(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        fakePlayer.get().setYRot(EntityUtil.getYawFromFacing(facing));
        return fakePlayer;
    }

    public static InteractionResult playerAttackBreakBlock(WeakReference<FakePlayer> fakePlayer, Level world, BlockPos targetPos, InteractionHand hand, Direction facing) {
        if (fakePlayer == null) {
            return InteractionResult.FAIL;
        }
        try {
            Objects.requireNonNull(fakePlayer.get()).gameMode.handleBlockBreakAction(targetPos, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, facing, world.getMaxBuildHeight(), 0);
            return InteractionResult.SUCCESS;
        }
        catch (Exception e) {
            return InteractionResult.FAIL;
        }
    }

    public static boolean tryHarvestBlock(WeakReference<FakePlayer> fakePlayer, Level world, BlockPos targetPos) {
        if (fakePlayer == null) {
            return false;
        }
        return Objects.requireNonNull(fakePlayer.get()).gameMode.destroyBlock(targetPos);
    }
}
