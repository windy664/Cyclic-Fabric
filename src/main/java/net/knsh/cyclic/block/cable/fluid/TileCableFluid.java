package net.knsh.cyclic.block.cable.fluid;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.block.cable.CableBase;
import net.knsh.cyclic.block.cable.EnumConnectType;
import net.knsh.cyclic.library.capabilities.ForgeFluidTankBase;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.util.FluidHelpers;
import net.knsh.cyclic.util.UtilDirection;
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
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TileCableFluid extends BlockEntityCyclic implements ExtendedScreenHandlerFactory {
    public static IntValue BUFFERSIZE;
    public static IntValue TRANSFER_RATE;
    final ItemStackHandler filter = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemVariant resource) {
            return super.isItemValid(slot, resource);
        }
    };
    public final Map<Direction, ForgeFluidTankBase> flow = new ConcurrentHashMap<>();

    public TileCableFluid(BlockPos pos, BlockState state) {
        super(CyclicBlocks.FLUID_PIPE.blockEntity(), pos, state);
        for (Direction f : Direction.values()) {
            flow.put(f, new ForgeFluidTankBase(this, (int) (BUFFERSIZE.get() * FluidConstants.BUCKET), p -> true));
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileCableFluid e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileCableFluid e) {
        e.tick();
    }

    List<Integer> rawList = IntStream.rangeClosed(0, 5).boxed().collect(Collectors.toList());

    public void tick() {
        for (Direction extractSide : Direction.values()) {
            EnumConnectType connection = this.getBlockState().getValue(CableBase.FACING_TO_PROPERTY_MAP.get(extractSide));
            if (connection.isExtraction()) {
                tryExtract(extractSide);
            }
        }
        normalFlow();
    }

    private void tryExtract(Direction extractSide) {
        if (extractSide == null) {
            return;
        }
        final BlockPos target = this.worldPosition.relative(extractSide); // .offset(
        final Direction incomingSide = extractSide.getOpposite();
        //when draining from a tank (instead of a source/waterlogged block) check the filter
        final Storage<FluidVariant> tankTarget = FluidHelpers.getTank(level, target, incomingSide);
        if (tankTarget == null) {
            return;
        }
        /*
        if (tankTarget != null
                && tankTarget.getTanks() > 0
                && !FilterCardItem.filterAllowsExtract(filter.getStackInSlot(0), tankTarget.getFluidInTank(0))) {
            return;
        }*/
        //first try standard fluid transfer
        if (FluidHelpers.tryFillPositionFromTank(level, worldPosition, extractSide, tankTarget, TRANSFER_RATE.get())) {
            return;
        }
        //handle special cases
        //waterlogged
        //cauldron
        ForgeFluidTankBase sideHandler = flow.get(extractSide);
        if (sideHandler != null && sideHandler.getSpace() >= FluidConstants.BUCKET) {
            FluidHelpers.extractSourceWaterloggedCauldron(level, target, sideHandler);
        }
    }

    private void normalFlow() {
        for (Direction incomingSide : Direction.values()) {
            final ForgeFluidTankBase sideHandler = flow.get(incomingSide);
            for (final Direction outgoingSide : UtilDirection.getAllInDifferentOrder()) {
                if (outgoingSide == incomingSide) {
                    continue;
                }
                EnumConnectType connection = this.getBlockState().getValue(CableBase.FACING_TO_PROPERTY_MAP.get(outgoingSide));
                if (connection.isExtraction() || connection.isBlocked()) {
                    continue;
                }
                if (sideHandler.getFluidAmount() <= 0) {
                    continue;
                }
                this.moveFluids(outgoingSide, worldPosition.relative(outgoingSide), TRANSFER_RATE.get(), sideHandler);
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        filter.deserializeNBT(tag.getCompound("filter"));
        ForgeFluidTankBase fluidh;
        for (Direction dir : Direction.values()) {
            fluidh = flow.get(dir);
            if (tag.contains("fluid" + dir.toString())) {
                fluidh.readFromNBT(tag.getCompound("fluid" + dir.toString()));
            }
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("filter", filter.serializeNBT());
        ForgeFluidTankBase fluidh;
        for (Direction dir : Direction.values()) {
            fluidh = flow.get(dir);
            CompoundTag fluidtag = new CompoundTag();
            if (fluidh != null) {
                fluidh.writeToNBT(fluidtag);
            }
            tag.put("fluid" + dir.toString(), fluidtag);
        }
        super.saveAdditional(tag);
    }

    @Override
    public void setField(int field, int value) {}

    @Override
    public int getField(int field) {
        return 0;
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.FLUID_PIPE.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new ContainerCableFluid(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }
}
