package net.knsh.cyclic.block.generatorfuel;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.block.battery.BatteryBlockEntity;
import net.knsh.cyclic.library.cap.CustomEnergyStorageUtil;
import net.knsh.cyclic.library.cap.ItemStackHandlerWrapper;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.util.FabricHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class TileGeneratorFuel extends BlockEntityCyclic implements ExtendedScreenHandlerFactory {
    enum Fields {
        TIMER, REDSTONE, BURNMAX, FLOWING;
    }

    static final int MAX = BatteryBlockEntity.MENERGY * 10;
    public static IntValue RF_PER_TICK;
    protected SimpleEnergyStorage energy = new SimpleEnergyStorage(MAX, MAX, MAX) {
        @Override
        protected void onFinalCommit() {
            TileGeneratorFuel.this.setChanged();
            TileGeneratorFuel.this.syncEnergy();
        }
    };
    protected ItemStackHandler inputSlots = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemVariant resource) {
            return FabricHelper.getBurnTime(resource.getItem()) > 0;
        }
    };
    ItemStackHandler outputSlots = new ItemStackHandler(0);
    ItemStackHandlerWrapper inventory = new ItemStackHandlerWrapper(inputSlots, outputSlots);
    final int factor = 1;
    private int burnTimeMax = 0; //only non zero if processing
    private int burnTime = 0; //how much of current fuel is left

    public TileGeneratorFuel(BlockPos pos, BlockState state) {
        super(CyclicBlocks.GENERATOR_FUEL.blockEntity(), pos, state);
        this.needsRedstone = 0;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileGeneratorFuel e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileGeneratorFuel e) {
        e.tick();
    }

    public void tick() {
        if (this.flowing == 1) {
            this.exportEnergyAllSides();
        }
        if (this.requiresRedstone() && !this.isPowered()) {
            setLitProperty(false);
            return;
        }
        if (this.burnTime == 0) {
            setLitProperty(false);
        }
        if (level.isClientSide) {
            return;
        }
        //are we EMPTY
        if (this.burnTime == 0) {
            tryConsumeFuel();
        }
        if (this.burnTime > 0 && this.energy.getAmount() + RF_PER_TICK.get() <= this.energy.getCapacity()) {
            this.burnTime--;
            //we have room in the tank, burn one tck and fill up
            boolean burnt;
            try (Transaction transaction = Transaction.openOuter()) {
                burnt = energy.insert(RF_PER_TICK.get(), transaction) > 0;
                transaction.commit();
            }
            setLitProperty(burnt);
        }
    }

    private void tryConsumeFuel() {
        this.burnTimeMax = 0;
        //pull in new fuel
        ItemStack stack = inputSlots.getStackInSlot(0);
        final int factor = 1;
        int burnTimeTicks = factor * FabricHelper.getBurnTime(stack.getItem());
        if (burnTimeTicks > 0) {
            // BURN IT
            this.burnTimeMax = burnTimeTicks;
            this.burnTime = this.burnTimeMax;
            if (stack.getCount() == 1 && stack.getItem().hasCraftingRemainingItem()) {
                inputSlots.setStackInSlot(0, stack.getItem().getCraftingRemainingItem().getDefaultInstance().copy());
            }
            else {
                stack.shrink(1);
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.GENERATOR_FUEL.block().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new ContainerGeneratorFuel(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public void load(CompoundTag tag) {
        CustomEnergyStorageUtil.deserializeNBT(tag.getCompound(NBTENERGY), energy);
        inventory.deserializeNBT(tag.getCompound(NBTINV));
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put(NBTENERGY, CustomEnergyStorageUtil.serializeNBT(energy));
        tag.put(NBTINV, inventory.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public int getField(int id) {
        switch (Fields.values()[id]) {
            case REDSTONE:
                return this.needsRedstone;
            case TIMER:
                return this.burnTime;
            case BURNMAX:
                return this.burnTimeMax;
            case FLOWING:
                return this.flowing;
            default:
                break;
        }
        return 0;
    }

    @Override
    public void setField(int field, int value) {
        switch (Fields.values()[field]) {
            case REDSTONE:
                this.needsRedstone = value % 2;
                break;
            case TIMER:
                this.burnTime = value;
                break;
            case BURNMAX:
                this.burnTimeMax = value;
                break;
            case FLOWING:
                this.flowing = value;
                break;
        }
    }

    public int getEnergyMax() {
        return TileGeneratorFuel.MAX;
    }
}
