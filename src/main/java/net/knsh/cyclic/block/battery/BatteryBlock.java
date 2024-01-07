package net.knsh.cyclic.block.battery;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.knsh.cyclic.lookups.CyclicItemLookup;
import net.knsh.cyclic.block.BlockCyclic;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class BatteryBlock extends BlockCyclic {
    public static final EnumProperty<EnumBatteryPercent> PERCENT = EnumProperty.create("percent", EnumBatteryPercent.class);

    public BatteryBlock(Properties settings) {
        super(settings.strength(1.8F));
        this.setHasGui();
        this.registerDefaultState(defaultBlockState().setValue(PERCENT, EnumBatteryPercent.ZERO));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PERCENT).add(LIT);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return new ArrayList<>();
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        ItemStack newStackBattery = new ItemStack(this);
        if (blockEntity instanceof BatteryBlockEntity battery) {
            EnergyStorage newStackEnergy = CyclicItemLookup.BATTERY_ITEM.find(newStackBattery, null).getBattery();
            if (newStackEnergy instanceof BatteryImplementation) {
                ((BatteryImplementation) newStackEnergy).setEnergy(battery.getBattery().getAmount());
            } else {
                try (Transaction transaction = Transaction.openOuter()) {
                    newStackEnergy.insert(battery.getBattery().getAmount(), transaction);
                    transaction.commit();
                }
            }

            if (battery.getBattery().getAmount() > 0) {
                newStackBattery.getOrCreateTag().putLong(BatteryBlockItem.ENERGYTT, battery.getBattery().getAmount());
                newStackBattery.getOrCreateTag().putLong(BatteryBlockItem.ENERGYTTMAX, battery.getBattery().getCapacity());
            }
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BatteryBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, CyclicBlocks.BATTERY.blockEntity(), level.isClientSide ? BatteryBlockEntity::clientTick : BatteryBlockEntity::serverTick);
    }


    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        long current = 0;
        EnergyStorage storage = CyclicItemLookup.BATTERY_ITEM.find(stack, null).getBattery();
        if (stack.hasTag() && stack.getTag().contains(BatteryImplementation.NBTENERGYS)) {
            current = stack.getTag().getLong(BatteryImplementation.NBTENERGYS);
        } else if (storage != null) {
            current = storage.getAmount();
        }
        BatteryBlockEntity container = (BatteryBlockEntity) level.getBlockEntity(pos);
        BatteryImplementation storageTile = container.getBattery();
        if (storageTile != null) {
            storageTile.setEnergy(current);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BatteryBlockEntity blockEntity = (BatteryBlockEntity) world.getBlockEntity(pos);
            if (blockEntity != null) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), blockEntity.getItem(0));
            }
            world.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, world, pos, newState, movedByPiston);
    }
}
