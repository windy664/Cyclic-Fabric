package net.knsh.cyclic.block.battery;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.knsh.cyclic.lookups.CyclicItemLookup;
import net.knsh.cyclic.block.BlockEntityCyclic;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BatteryBlockEntity extends BlockEntityCyclic implements ExtendedScreenHandlerFactory, ForgeImplementedInventory, BatteryImplementation {
    public static final int MAX = 6400000;
    public static ForgeConfigSpec.IntValue SLOT_CHARGING_RATE;
    private final Map<Direction, Boolean> poweredSides;
    SimpleEnergyStorage energy = new SimpleEnergyStorage(MAX, MAX /4, MAX / 4);
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    @Override
    public CompoundTag serializeNBT() {
        return super.serializeNBT();
    }

    enum Fields {
        FLOWING, N, E, S, W, U, D
    }

    public BatteryBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.BATTERY.blockEntity(), pos, state);
        flowing = 0;
        poweredSides = new ConcurrentHashMap<Direction, Boolean>();
        for (Direction d : Direction.values()) {
            poweredSides.put(d, false);
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, BatteryBlockEntity batteryBlockEntity) {
        batteryBlockEntity.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, BatteryBlockEntity batteryBlockEntity) {
        batteryBlockEntity.tick();
    }

    public void tick() {
        setPercentFilled();
        boolean isFlowing = this.getFlowing() == 1;
        setLitProperty(isFlowing);
        if (isFlowing) {
            this.tickCableFlow();
        }
        this.chargeSlot();
    }

    private void chargeSlot() {
        if (level.isClientSide) {
            return;
        }
        ItemStack slotItem = getItem(0);
        BatteryImplementation batteryImplementation = CyclicItemLookup.BATTERY_ITEM.find(slotItem, null);
        if (batteryImplementation == null) {
            return;
        }
        EnergyStorage itemStackStorage = batteryImplementation.getBattery();
        if (itemStackStorage != null) {
            try (Transaction transaction = Transaction.openOuter()) {
                long extracted = energy.extract(SLOT_CHARGING_RATE.get(), transaction);
                itemStackStorage.insert(extracted, transaction);
                transaction.commit();
            }
        }
    }

    public int getFlowing() {
        return flowing;
    }

    public void setFlowing(int flowing) {
        this.flowing = flowing;
    }

    public void setPercentFilled() {
        BlockState st = this.getBlockState();
        if (st.hasProperty(BatteryBlock.PERCENT)) {
            EnumBatteryPercent previousPercent = st.getValue(BatteryBlock.PERCENT);
            EnumBatteryPercent percent = calculateRoundedPercentFilled();
            if (percent != previousPercent) {
                this.level.setBlockAndUpdate(worldPosition, st.setValue(BatteryBlock.PERCENT, percent));
            }
        }
    }

    public EnumBatteryPercent calculateRoundedPercentFilled() {
        int percent = (int) Math.floor((this.getAmount() * 1.0F) / MAX * 10.0) * 10;
        //    ut.printf("%d / %d = %d percent%n", this.getEnergy(), MAX, percent);
        if (percent >= 100) {
            return EnumBatteryPercent.ONEHUNDRED;
        }
        else if (percent >= 90) {
            return EnumBatteryPercent.NINETY;
        }
        else if (percent >= 80) {
            return EnumBatteryPercent.EIGHTY;
        }
        else if (percent >= 60) {
            return EnumBatteryPercent.SIXTY;
        }
        else if (percent >= 40) {
            return EnumBatteryPercent.FOURTY;
        }
        else if (percent >= 20) {
            return EnumBatteryPercent.TWENTY;
        }
        return EnumBatteryPercent.ZERO;
    }

    public boolean getSideHasPower(Direction side) {
        return this.poweredSides.get(side);
    }

    public int getSideField(Direction side) {
        return this.getSideHasPower(side) ? 1 : 0;
    }

    public void setSideField(Direction side, int pow) {
        this.poweredSides.put(side, (pow == 1));
    }

    private void tickCableFlow() {
        for (final Direction exportToSide : UtilDirection.getAllInDifferentOrder()) {
            if (this.poweredSides.get(exportToSide)) {
                EnergyStorage target = EnergyStorage.SIDED.find(level, getBlockPos().relative(exportToSide), exportToSide.getOpposite());
                if (target == null) {
                    continue;
                }
                BlockState targetBlockState = level.getBlockState(getBlockPos().relative(exportToSide));
                EnergyStorageUtil.move(
                        getBattery(),
                        target,
                        MAX / 4,
                        null
                );
                level.sendBlockUpdated(getBlockPos().relative(exportToSide), targetBlockState, targetBlockState, Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        for (Direction f : Direction.values()) {
            poweredSides.put(f, tag.getBoolean("flow_" + f.getName()));
        }
        setEnergy(tag.getLong(NBTENERGY));
        ContainerHelper.loadAllItems(tag, inventory);
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        for (Direction f : Direction.values()) {
            tag.putBoolean("flow_" + f.getName(), poweredSides.get(f));
        }
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putInt("flowing", getFlowing());
        tag.putLong(NBTENERGY, getAmount());
        super.saveAdditional(tag);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(worldPosition);
    }

    @Override
    public int getField(int id) {
        switch (Fields.values()[id]) {
            case D:
                return this.getSideField(Direction.DOWN);
            case E:
                return this.getSideField(Direction.EAST);
            case N:
                return this.getSideField(Direction.NORTH);
            case S:
                return this.getSideField(Direction.SOUTH);
            case U:
                return this.getSideField(Direction.UP);
            case W:
                return this.getSideField(Direction.WEST);
            case FLOWING:
                return flowing;
        }
        return -1;
    }

    @Override
    public void setField(int field, int value) {
        switch (Fields.values()[field]) {
            case FLOWING:
                flowing = value;
                break;
            case D:
                this.setSideField(Direction.DOWN, value % 2);
                break;
            case E:
                this.setSideField(Direction.EAST, value % 2);
                break;
            case N:
                this.setSideField(Direction.NORTH, value % 2);
                break;
            case S:
                this.setSideField(Direction.SOUTH, value % 2);
                break;
            case U:
                this.setSideField(Direction.UP, value % 2);
                break;
            case W:
                this.setSideField(Direction.WEST, value % 2);
                break;
        }
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.BATTERY.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new BatteryContainer(i, playerInventory, this, level, worldPosition);
    }

    @Override
    public BatteryImplementation getBattery() {
        return this;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return energy.insert(maxAmount, transaction);
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return energy.extract(maxAmount, transaction);
    }

    @Override
    public long getAmount() {
        return energy.getAmount();
    }

    @Override
    public long getCapacity() {
        return energy.getCapacity();
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }
}
