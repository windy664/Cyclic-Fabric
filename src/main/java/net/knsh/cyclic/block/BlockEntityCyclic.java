package net.knsh.cyclic.block;

import com.google.common.collect.Lists;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.knsh.cyclic.block.beaconpotion.BeamParams;
import net.knsh.cyclic.library.core.IHasFluid;
import net.knsh.cyclic.library.util.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public abstract class BlockEntityCyclic extends BlockEntity implements Container, IHasFluid {
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
        boolean previous = state.getValue(BlockCyclic.LIT);
        if (previous != lit) {
            this.level.setBlockAndUpdate(worldPosition, state.setValue(BlockCyclic.LIT, lit));
        }
    }

    public boolean moveItems(Direction myFacingDir, int max, SlottedStackStorage handlerHere) {
        return moveItems(myFacingDir, worldPosition.relative(myFacingDir), max, handlerHere, 0);
    }

    public boolean moveItems(Direction myFacingDir, BlockPos posTarget, int max, SlottedStackStorage handlerHere, int theslot) {
        if (this.level.isClientSide()) {
            return false;
        }
        if (handlerHere == null) {
            return false;
        }
        Direction themFacingMe = myFacingDir.getOpposite();
        BlockEntity tileTarget = level.getBlockEntity(posTarget);
        if (tileTarget == null) {
            return false;
        }

        Storage<ItemVariant> handlerOutput = ItemStorage.SIDED.find(tileTarget.getLevel(), tileTarget.getBlockPos(), themFacingMe);
        if (handlerOutput == null) {
            return false;
        }

        long sizeafter = StorageUtil.move(
                handlerHere,
                handlerOutput,
                itemVariant -> true,
                max,
                null
        );
        return sizeafter > 0;
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

    public void tryExtract(SlottedStackStorage myself, Direction extractSide, int qty, @Nullable ItemStackHandler nullableFilter) {
        if (extractSide == null) {
            return;
        }
        if (extractSide == null || !myself.getStackInSlot(0).isEmpty()) {
            return;
        }
        BlockPos posTarget = worldPosition.relative(extractSide);
        BlockEntity tile = level.getBlockEntity(posTarget);
        if (tile != null) {
            Storage<ItemVariant> itemHandlerFrom = ItemStorage.SIDED.find(tile.getLevel(), posTarget, extractSide.getOpposite());

            if (itemHandlerFrom != null) {
                try (Transaction transaction = Transaction.openOuter()) {
                    long itemTarget;
                    if (itemHandlerFrom instanceof SlottedStorage<ItemVariant> slottedStorage) {
                        for (int i = 0; i < slottedStorage.getSlotCount(); i++) {
                            SingleSlotStorage<ItemVariant> slot = slottedStorage.getSlot(i);
                            ItemVariant slotResource = slot.getResource();

                            if (slotResource.isBlank()) {
                                continue;
                            }

                            try (Transaction simulatedTransaction = Transaction.openNested(transaction)) {
                                itemTarget = slot.extract(slotResource, qty, simulatedTransaction);
                            }

                            if (itemTarget <= 0) {
                                continue;
                            }

                            itemTarget = slot.extract(slotResource, qty, transaction);
                            itemTarget = myself.insertSlot(0, slotResource, itemTarget, transaction);
                            transaction.commit();
                            return;
                        }
                    } else {
                        ItemVariant slotResource = itemHandlerFrom.iterator().next().getResource();
                        itemTarget = itemHandlerFrom.extract(slotResource, qty, transaction);
                        myself.insertSlot(0, slotResource, itemTarget, transaction);
                        transaction.commit();
                    }
                }
            }
        }
    }

    public static void updateBeam(Level level, BlockPos pos, BeamParams beamParams) {
        BlockPos blockpos;
        if (beamParams.lastCheckY < pos.getY()) {
            blockpos = pos;
            beamParams.checkingBeamSections = Lists.newArrayList();
            beamParams.lastCheckY = pos.getY() - 1;
        } else {
            blockpos = new BlockPos(pos.getX(), beamParams.lastCheckY + 1, pos.getZ());
        }
        BeaconBlockEntity.BeaconBeamSection beaconblockentity$beaconbeamsection = beamParams.checkingBeamSections.isEmpty() ? null : beamParams.checkingBeamSections.get(beamParams.checkingBeamSections.size() - 1);
        int surfaceHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
        for (int yLoop = 0; yLoop < 10 && blockpos.getY() <= surfaceHeight; ++yLoop) {
            BlockState blockstate = level.getBlockState(blockpos);
            // important: start one up OR give your beacon block an override to getBeaconColorMultiplier
            if (level.getBlockState(pos).getBlock() instanceof BlockCyclic) {
                float[] colorMult = ((BlockCyclic) level.getBlockState(pos).getBlock()).getBeaconColorMultiplier(blockstate, level, blockpos, pos);
                if (colorMult != null) {
                    if (beamParams.checkingBeamSections.size() <= 1) {
                        beaconblockentity$beaconbeamsection = new BeaconBlockEntity.BeaconBeamSection(colorMult);
                        beamParams.checkingBeamSections.add(beaconblockentity$beaconbeamsection);
                    }
                    else if (beaconblockentity$beaconbeamsection != null) {
                        float[] col = beaconblockentity$beaconbeamsection.getColor();
                        if (Arrays.equals(colorMult, col)) {
                            //((BeaconBeamSectionMixin) beaconblockentity$beaconbeamsection).increaseHeight();
                        } else {
                            beaconblockentity$beaconbeamsection = new BeaconBlockEntity.BeaconBeamSection(new float[] { (col[0] + colorMult[0]) / 2.0F, (col[1] + colorMult[1]) / 2.0F, (col[2] + colorMult[2]) / 2.0F });
                            beamParams.checkingBeamSections.add(beaconblockentity$beaconbeamsection);
                        }
                    }
                } else {
                    if (beaconblockentity$beaconbeamsection == null || blockstate.getLightBlock(level, blockpos) >= 15 && !blockstate.is(Blocks.BEDROCK)) {
                        beamParams.checkingBeamSections.clear();
                        beamParams.lastCheckY = surfaceHeight;
                        break;
                    }
                    //((BeaconBeamSectionMixin) beaconblockentity$beaconbeamsection).increaseHeight();
                }
                blockpos = blockpos.above();
                ++beamParams.lastCheckY;
            }
        }
        if (level.getGameTime() % 80L == 0L) {
            if (!beamParams.beamSections.isEmpty()) {
                SoundUtil.playSound(level, pos, SoundEvents.BEACON_AMBIENT);
            }
        }
        if (beamParams.lastCheckY >= surfaceHeight) {
            beamParams.lastCheckY = level.getMinBuildHeight() - 1;
            beamParams.beamSections = beamParams.checkingBeamSections;
        }
    }

    public FluidStack getFluid() {
        return FluidStack.EMPTY;
    }

    public void setFluid(FluidStack fluid) {}

    @Deprecated
    @Override
    public int getContainerSize() {
        return 0;
    }

    @Deprecated
    @Override
    public boolean isEmpty() {
        return true;
    }

    @Deprecated
    @Override
    public ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    public void setItem(int index, ItemStack stack) {}

    @Deprecated
    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Deprecated
    @Override
    public void clearContent() {}
}
