package net.knsh.cyclic.block.melter;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.flib.cap.CustomEnergyStorageUtil;
import net.knsh.flib.capabilities.FluidTankBase;
import net.knsh.cyclic.lookups.types.FluidLookup;
import net.knsh.cyclic.lookups.types.ItemHandlerLookup;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicRecipeTypes;
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
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;
import java.util.function.Predicate;

public class TileMelter extends BlockEntityCyclic implements ExtendedScreenHandlerFactory, ItemHandlerLookup, FluidLookup {
    enum Fields {
        REDSTONE, TIMER, RENDER, BURNMAX
    }

    static final int MAX = 64000;
    public static final int CAPACITY = (int) (64 * FluidConstants.BUCKET);
    public static final int TRANSFER_FLUID_PER_TICK = (int) (FluidConstants.BUCKET / 20);
    public FluidTankBase tank = new FluidTankBase(this, CAPACITY, isFluidValid()) {
        @Override
        protected void onFinalCommit() {
            TileMelter.this.setChanged();
        }
    };
    SimpleEnergyStorage energy = new SimpleEnergyStorage(MAX, MAX, MAX) {
        @Override
        protected void onFinalCommit() {
            TileMelter.this.setChanged();
            TileMelter.this.syncEnergy();
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }
    };
    ItemStackHandler inventory = new ItemStackHandler(2);
    private RecipeMelter currentRecipe;
    private int burnTimeMax = 0;

    public TileMelter(BlockPos pos, BlockState state) {
        super(CyclicBlocks.MELTER.blockEntity(), pos, state);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileMelter e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileMelter e) {
        e.tick();
    }

    public void tick() {
        this.findMatchingRecipe();
        if (currentRecipe == null) return;
        this.timer--;
        if (timer < 0) {
            timer = 0;
        }
        final int cost = 0; //this.currentRecipe.getEnergy().getRfPertick();
        if (energy.getAmount() < cost && cost > 0) {
            this.timer = 0;
            return;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            energy.extract(cost, transaction);
            transaction.commit();
        }
        if (currentRecipe == null || !currentRecipe.matches(this, level)) {
            this.findMatchingRecipe();
            if (currentRecipe == null) {
                this.timer = 0;
                return;
            }
        }
        if (timer == 0 && this.tryProcessRecipe()) {
            if (this.currentRecipe != null) {
                this.timer = this.currentRecipe.getEnergy().getTicks(); // may also reset during findRecipe
            }
        }
    }

    @Override
    public void setField(int field, int value) {
        switch (Fields.values()[field]) {
            case TIMER:
                this.timer = value;
                break;
            case REDSTONE:
                this.needsRedstone = value % 2;
                break;
            case RENDER:
                this.render = value % 2;
                break;
            case BURNMAX:
                this.burnTimeMax = value;
                break;
        }
    }

    @Override
    public int getField(int field) {
        return switch (Fields.values()[field]) {
            case TIMER -> timer;
            case REDSTONE -> this.needsRedstone;
            case RENDER -> this.render;
            case BURNMAX -> this.burnTimeMax;
        };
    }

    public Predicate<FluidStack> isFluidValid() {
        return p -> true;
    }

    @Override
    public ItemStackHandler getItemHandler() {
        return this.inventory;
    }

    @Override
    public FluidTankBase getFluidTank() {
        return this.tank;
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.MELTER.block().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new ContainerMelter(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public void load(CompoundTag tag) {
        tank.readFromNBT(tag.getCompound(NBTFLUID));
        CustomEnergyStorageUtil.deserializeNBT(tag.getCompound(NBTENERGY), energy);
        inventory.deserializeNBT(tag.getCompound(NBTINV));
        burnTimeMax = tag.getInt("burnTimeMax");
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        CompoundTag fluid = new CompoundTag();
        tank.writeToNBT(fluid);
        tag.put(NBTFLUID, fluid);
        tag.put(NBTENERGY, CustomEnergyStorageUtil.serializeNBT(energy));
        tag.put(NBTINV, inventory.serializeNBT());
        tag.putInt("burnTimeMax", this.burnTimeMax);
        super.saveAdditional(tag);
    }

    public float getCapacity() {
        return CAPACITY;
    }

    @Override
    public FluidStack getFluid() {
        return tank == null ? FluidStack.EMPTY : tank.getFluid();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public void setFluid(FluidStack fluid) {
        tank.setFluid(fluid);
    }

    public ItemStack getStackInputSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    private void findMatchingRecipe() {
        if (currentRecipe != null && currentRecipe.matches(this, level)) {
            return;
        }
        currentRecipe = null;
        this.burnTimeMax = 0;
        this.timer = 0;
        List<RecipeMelter> recipes = level.getRecipeManager().getAllRecipesFor(CyclicRecipeTypes.MELTER);
        for (RecipeMelter rec : recipes) {
            if (rec.matches(this, level)) {
                if (this.tank.getFluid() != null && !this.tank.getFluid().isEmpty()) {
                    if (rec.getRecipeFluid().getFluid() != this.tank.getFluid().getFluid()) {
                        continue;
                        //fluid wont fit
                    }
                }
                currentRecipe = rec;
                this.burnTimeMax = this.currentRecipe.getEnergy().getTicks();
                this.timer = this.burnTimeMax;
                return;
            }
        }
    }

    private boolean tryProcessRecipe() {
        long test;
        try (Transaction transaction = Transaction.openOuter()) {
            test = tank.insert(this.currentRecipe.getRecipeFluid().getType(), this.currentRecipe.getRecipeFluid().getAmount(), transaction);
            transaction.abort();
        }
        if (test == this.currentRecipe.getRecipeFluid().getAmount()
                && currentRecipe.matches(this, level)) {
            //ok it has room for all the fluid none will be wasted
            inventory.getStackInSlot(0).shrink(1);
            inventory.getStackInSlot(1).shrink(1);
            try (Transaction transaction = Transaction.openOuter()) {
                tank.insert(this.currentRecipe.getRecipeFluid().getType(), this.currentRecipe.getRecipeFluid().getAmount(), transaction);
                transaction.commit();
            }
            return true;
        }
        return false;
    }
}
