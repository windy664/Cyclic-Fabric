package net.knsh.cyclic.block.generatorfuel;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.util.ImplementedInventory;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class GeneratorFuelBlockEntity extends BlockEntityCyclic implements ExtendedScreenHandlerFactory, ImplementedInventory {

    enum Fields {
        TIMER, REDSTONE, BURNMAX, FLOWING;
    }

    static final int MAX = MENERGY * 10;
    public static int RF_PER_TICK = 80;

    private static NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    SimpleEnergyStorage energy = new SimpleEnergyStorage(MAX, MAX, MAX);

    final int factor = 1;
    private int burnTimeMax = 0; //only non zero if processing
    private int burnTime = 0; //how much of current fuel is left

    public GeneratorFuelBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.GENERATOR_FUEL.blockEntity(), pos, state);
        this.needsRedstone = 0;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, GeneratorFuelBlockEntity e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, GeneratorFuelBlockEntity e) {
        e.tick();
    }

    public void tick() {
        if (this.flowing == 1) {
            //export
        }
        if (this.requiresRedstone() && !this.isPowered()) {
            setLitProperty(false);
            return;
        }
        if (this.burnTime == 0) {
            setLitProperty(false);
        }
        if (this.level.isClientSide) {
            return;
        }
        if (this.burnTime == 0) {
            tryComsumeFuel();
        }
        if (this.burnTime > 0 && this.energy.amount + RF_PER_TICK <= this.energy.capacity) {
            this.burnTime--;
            try (Transaction transaction = Transaction.openOuter()) {
                long amount = energy.insert(RF_PER_TICK, transaction);
                if (amount == RF_PER_TICK) {
                    setLitProperty(true);
                }
                transaction.commit();
            }
        }
    }

    private void tryComsumeFuel() {
        this.burnTimeMax = 0;
        ItemStack stack = getItem(0);
        final int factor = 1;
        int burnTimeTicks = factor * AbstractFurnaceBlockEntity.getFuel().getOrDefault(stack, 0);
        if (burnTimeTicks > 0) {
            this.burnTimeMax = burnTimeTicks;
            this.burnTime = this.burnTimeMax;
            if (stack.getCount() == 1 && stack.getItem().hasCraftingRemainingItem()) {
                setItem(0, stack.getItem().getCraftingRemainingItem().getDefaultInstance().copy());
            } else {
                stack.shrink(1);
            }
        }
    }

    public long getEnergy() {
        return energy.amount;
    }

    @Override
    public void load(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, inventory);
        energy.amount = tag.getLong("amount");
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putLong("amount", energy.amount);
        super.saveAdditional(tag);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new GeneratorFuelScreenHandler(i, inventory, this, level, worldPosition);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.worldPosition);
    }

    @Override
    public void setField(int field, int value) {
        switch (Fields.values()[field]) {
            case REDSTONE -> this.needsRedstone = value % 2;
            case TIMER -> this.burnTime = value;
            case BURNMAX -> this.burnTimeMax = value;
            case FLOWING -> this.flowing = value;
        }
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
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.GENERATOR_FUEL.block().getName();
    }
}
