package net.knsh.cyclic.block;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.knsh.cyclic.block.beaconpotion.BeamParams;
import net.knsh.cyclic.library.capabilities.FluidTankBase;
import net.knsh.cyclic.library.core.IHasEnergy;
import net.knsh.cyclic.library.core.IHasFluid;
import net.knsh.cyclic.library.util.SoundUtil;
import net.knsh.cyclic.util.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
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

public abstract class BlockEntityCyclic extends BlockEntity implements IHasFluid {
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
            if (blockstate.getBlock() instanceof BlockCyclic) {
                float[] colorMult = ((BlockCyclic) blockstate.getBlock()).getBeaconColorMultiplier(blockstate, level, blockpos, pos);
                if (colorMult != null) {
                    if (beamParams.checkingBeamSections.size() <= 1) {
                        beaconblockentity$beaconbeamsection = new BeaconBlockEntity.BeaconBeamSection(colorMult);
                        beamParams.checkingBeamSections.add(beaconblockentity$beaconbeamsection);
                    }
                    else if (beaconblockentity$beaconbeamsection != null) {
                        float[] col = beaconblockentity$beaconbeamsection.getColor();
                        if (Arrays.equals(colorMult, col)) {
                            //beaconblockentity$beaconbeamsection.increaseHeight();
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
                    if (beaconblockentity$beaconbeamsection != null) {}
                    //beaconblockentity$beaconbeamsection.increaseHeight();
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
