package com.lothrazar.cyclic.block.anvil;

import com.lothrazar.cyclic.porting.neoforge.items.ForgeImplementedInventory;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.library.util.ItemStackUtil;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import com.lothrazar.cyclic.block.BlockEntityCyclic;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class AnvilAutoBlockEntity extends BlockEntityCyclic implements ExtendedScreenHandlerFactory, ForgeImplementedInventory {
    enum Fields {
        TIMER, REDSTONE
    }

    static final int MAX = 64000;
    public static ForgeConfigSpec.IntValue POWERCONF;
    SimpleEnergyStorage energy = new SimpleEnergyStorage(MAX, MAX, MAX) {
        @Override
        public boolean supportsExtraction() {
            return false;
        }

        @Override
        protected void onFinalCommit() {
            AnvilAutoBlockEntity.this.setChanged();
        }
    };
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    public AnvilAutoBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.ANVIL.blockEntity(), pos, state);
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, AnvilAutoBlockEntity e) {
        e.tick();
    }

    public static <E extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, AnvilAutoBlockEntity e) {
        e.tick();
    }

    public void tick() {
        if (this.requiresRedstone() && !this.isPowered()) {
            setLitProperty(false);
            return;
        }
        setLitProperty(true);
        ItemStack stack = getItem(0);
        if (stack.isEmpty()) {
            return;
        }
        final int repair = POWERCONF.get();
        boolean work = false;
        if (repair > 0 &&
                energy.getAmount() >= repair &&
                stack.isDamageableItem() &&
                stack.getDamageValue() > 0) {
            //we can repair so steal some power
            //ok drain power
            try (Transaction transaction = Transaction.openOuter()) {
                energy.extract(repair, transaction);
                transaction.commit();
            }
            work = true;
        }
        if (work) {
            ItemStackUtil.repairItem(stack);
            boolean done = stack.getDamageValue() == 0;
            if (done && getItem(1).isEmpty()) {
                insertItem(1, stack.copy(), false);
                extractItem(0, stack.getCount(), false);
            }
        }
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

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public void setField(int field, int value) {
        switch (Fields.values()[field]) {
            case REDSTONE:
                this.needsRedstone = value % 2;
                break;
            case TIMER:
                this.timer = value;
                break;
        }
    }

    @Override
    public int getField(int id) {
        switch (Fields.values()[id]) {
            case REDSTONE:
                return this.needsRedstone;
            case TIMER:
                return this.timer;
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
        return CyclicBlocks.ANVIL.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new AnvilAutoContainer(i, playerInventory, this, level, worldPosition);
    }

    public int getEnergyMax() {
        return MAX;
    }
}
