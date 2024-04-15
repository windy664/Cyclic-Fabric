package com.lothrazar.cyclic.block.generatorfuel;

import com.lothrazar.cyclic.block.BlockEntityCyclic;
import com.lothrazar.cyclic.block.battery.BatteryBlockEntity;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.util.FabricHelper;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import com.lothrazar.library.cap.CustomEnergyStorageUtil;
import com.lothrazar.library.cap.ItemStackHandlerWrapper;
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
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class TileGeneratorFuel extends BlockEntityCyclic implements ExtendedScreenHandlerFactory {
    enum Fields {
        TIMER, REDSTONE, BURNMAX, FLOWING
    }

    static final int MAX = BatteryBlockEntity.MENERGY * 10;
    public static IntValue RF_PER_TICK;
    private int burnTimeMax = 0; //only non zero if processing
    private int burnTime = 0; //how much of current fuel is left

    @SuppressWarnings("UnstableApiUsage")
    protected SimpleEnergyStorage energy = new SimpleEnergyStorage(MAX, MAX, MAX) {
        @Override
        protected void onFinalCommit() {
            TileGeneratorFuel.this.setChanged();
            TileGeneratorFuel.this.syncEnergy();
        }
    };
    @SuppressWarnings("UnstableApiUsage")
    protected ItemStackHandler inputSlots = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemVariant resource) {
            return FabricHelper.getBurnTime(resource.getItem()) > 0;
        }
    };
    ItemStackHandler outputSlots = new ItemStackHandler(0);
    ItemStackHandlerWrapper inventory = new ItemStackHandlerWrapper(inputSlots, outputSlots);

    public TileGeneratorFuel(BlockPos pos, BlockState state) {
        super(CyclicBlocks.GENERATOR_FUEL.blockEntity(), pos, state);
        this.needsRedstone = 0;
    }

    public static void serverTick(Level ignoredLevel, BlockPos ignoredBlockPos, BlockState ignoredBlockState, TileGeneratorFuel e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level ignoredLevel, BlockPos ignoredBlockPos, BlockState ignoredBlockState, TileGeneratorFuel e) {
        e.tick();
    }

    @SuppressWarnings("UnstableApiUsage")
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
    public @NotNull Component getDisplayName() {
        return CyclicBlocks.GENERATOR_FUEL.block().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
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
        return switch (Fields.values()[id]) {
            case REDSTONE -> this.needsRedstone;
            case TIMER -> this.burnTime;
            case BURNMAX -> this.burnTimeMax;
            case FLOWING -> this.flowing;
            default -> 0;
        };
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
