package net.knsh.cyclic.block.anvilvoid;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.fluid.FluidXpJuiceHolder;
import net.knsh.cyclic.library.capabilities.FluidTankBase;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicFluids;
import net.knsh.cyclic.porting.neoforge.FluidFabricToForge;
import net.knsh.cyclic.library.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class AnvilVoidBlockEntity extends BlockEntityCyclic implements ExtendedScreenHandlerFactory, ImplementedInventory {
    enum Fields {
        TIMER, REDSTONE;
    }
    public static final long CAPACITY = 16 * FluidConstants.BUCKET;
    public static int FLUIDPAY = 25;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    private final FluidTankBase tank = new FluidTankBase(this, (int) CAPACITY) {};

    public AnvilVoidBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.ANVILVOID.blockEntity(), pos, state);
        this.needsRedstone = 1;
        tank.fluidBlockIdentifier = FluidXpJuiceHolder.NAME;
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.ANVILVOID.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new AnvilVoidContainer(syncId, playerInventory, this, level, worldPosition);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.worldPosition);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void load(CompoundTag nbt) {
        tank.variant = FluidVariant.fromNbt(nbt.getCompound("fluidVariant"));
        tank.amount = nbt.getLong("amount");
        ContainerHelper.loadAllItems(nbt, inventory);
        super.load(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("fluidVariant", tank.variant.toNbt());
        nbt.putLong("amount", tank.amount);
        ContainerHelper.saveAllItems(nbt, inventory);
    }

    public static <E extends BlockEntity> void tick(Level world, BlockPos blockPos, BlockState state, AnvilVoidBlockEntity entity) {
        entity.tick(world, blockPos, state);
    }

    public void tick(Level world, BlockPos pos, BlockState state) {
        if (world.isClientSide()) {
            return;
        }

        if (this.requiresRedstone() && !this.isPowered()) {
            setLitProperty(false);
            return;
        }
        setLitProperty(true);

        if (isOutputSlotEmpty()) {
            if (this.vaildRecipe()) {
                setChanged(world, pos, state);
                craftItem();
                world.sendBlockUpdated(pos, state, getBlockState(), Block.UPDATE_CLIENTS);
            }
        } else setChanged(world, pos, state);
    }

    private void craftItem() {
        ItemStack stack = getItem(0);

        if (stack.getItem() == Items.ENCHANTED_BOOK) {
            this.setItem(1, new ItemStack(Items.BOOK, getItem(1).getCount() + 1));
            this.removeItem(0, 1);
        } else {
            stack.getTag().remove("Enchantments");
            this.setItem(1, stack.copy());
            this.removeItem(0, 1);
        }
        if (FLUIDPAY > 0) {
            level.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1f, 1f);
            Fluid newFluid = CyclicFluids.STILL_XP.getSource();
            try (Transaction transaction = Transaction.openOuter()) {
                long insertAmount = tank.insert(FluidVariant.of(newFluid), FluidFabricToForge.toDroplets(FLUIDPAY), transaction);
                transaction.commit();
            }
        }
    }

    public FluidTankBase getFluid() {
        return tank;
    }

    @Override
    public void setFluid(FluidVariant fluid) {
        tank.variant = fluid;
    }

    @Override
    public void setFluidAmount(long amount) {
        tank.amount = amount;
    }

    private boolean vaildRecipe() {
        return getItem(0).getItem() == Items.ENCHANTED_BOOK || (getItem(0).getTag() != null && getItem(0).getTag().contains("Enchantments"));
    }

    private boolean isOutputSlotEmpty() {
        return this.getItem(1).isEmpty() || this.getItem(1).getItem() == Items.BOOK
                && this.getItem(0).getItem() == Items.ENCHANTED_BOOK
                && this.getItem(1).getCount() + 1 <= getItem(1).getMaxStackSize();
    }

    @Override
    public void setField(int field, int value) {
        switch (Fields.values()[field]) {
            case REDSTONE -> this.needsRedstone = value % 2;
            case TIMER -> this.timer = value;
        }
    }

    @Override
    public int getField(int field) {
        switch (Fields.values()[field]) {
            case REDSTONE -> {
                return this.needsRedstone;
            }
            case TIMER -> {
                return this.timer;
            }
            default -> {
            }
        }
        return 0;
    }
}
