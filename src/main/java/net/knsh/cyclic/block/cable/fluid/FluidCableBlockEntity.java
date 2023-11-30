package net.knsh.cyclic.block.cable.fluid;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.block.cable.CableBase;
import net.knsh.cyclic.block.cable.EnumConnectType;
import net.knsh.cyclic.library.capabilities.FluidTankBase;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FluidCableBlockEntity extends BlockEntityCyclic implements ExtendedScreenHandlerFactory, ForgeImplementedInventory {
    public static ForgeConfigSpec.IntValue BUFFERSIZE;
    public static ForgeConfigSpec.IntValue TRANSFER_RATE;
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    public final Map<Direction, FluidTankBase> flow = new ConcurrentHashMap<>();

    public FluidCableBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.FLUID_PIPE.blockEntity(), pos, state);
        for (Direction f : Direction.values()) {
            flow.put(f, new FluidTankBase(this, (int) (BUFFERSIZE.get() * FluidConstants.BUCKET)));
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, FluidCableBlockEntity e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, FluidCableBlockEntity e) {
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
        if (extractSide == null) return;

        final BlockPos target = this.worldPosition.relative(extractSide);
        final Direction incomingSide = extractSide.getOpposite();
        final Storage<FluidVariant> tank = FluidStorage.SIDED.find(level, target, extractSide.getOpposite());
        FluidTankBase sideHandler = flow.get(extractSide);

        if (tank != null && sideHandler != null) {
            StorageUtil.move(tank, sideHandler, fluidVariant -> true, TRANSFER_RATE.get(), null);
        }
    }

    private void normalFlow() {
        for (Direction incomingSide : Direction.values()) {
            final FluidTankBase sideHandler = flow.get(incomingSide);
            if (sideHandler != null) {
                for (final Direction outgoingSide : UtilDirection.getAllInDifferentOrder()) {
                    if (outgoingSide == incomingSide) continue;

                    EnumConnectType connection = this.getBlockState().getValue(CableBase.FACING_TO_PROPERTY_MAP.get(outgoingSide));

                    if (connection.isExtraction() || connection.isBlocked()) continue;
                    if (sideHandler.getAmount() <= 0) continue;

                    BlockPos sourcePos = getBlockPos().relative(outgoingSide);
                    Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, sourcePos, outgoingSide.getOpposite());

                    if (storage != null) continue;

                    StorageUtil.move(sideHandler, storage, fluidVariant -> true, TRANSFER_RATE.get(), null);
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, inventory);
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, inventory);
        super.saveAdditional(tag);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public void setField(int field, int value) {}

    @Override
    public int getField(int field) {
        return 0;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.FLUID_PIPE.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new FluidCableScreenHandler(i, playerInventory, this, level, worldPosition);
    }
}
