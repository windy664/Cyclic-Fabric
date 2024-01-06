package net.knsh.cyclic.block.generatorfuel;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.knsh.cyclic.block.BlockCyclic;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.registry.CyclicScreens;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class BlockGeneratorFuel extends BlockCyclic {

    public BlockGeneratorFuel(Properties settings) {
        super(settings.strength(1.8F));
        registerDefaultState(defaultBlockState().setValue(LIT, false));
        this.setHasGui();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING).add(LIT);
    }

    @Override
    public void registerClient() {
        MenuScreens.register(CyclicScreens.GENERATOR_FUEL, ScreenGeneratorFuel::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileGeneratorFuel(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CyclicBlocks.GENERATOR_FUEL.blockEntity(), world.isClientSide ? TileGeneratorFuel::clientTick : TileGeneratorFuel::serverTick);
    }

    @Override
    public void registerLookups() {
        EnergyStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.energy), CyclicBlocks.GENERATOR_FUEL.blockEntity());
        ItemStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.inventory), CyclicBlocks.GENERATOR_FUEL.blockEntity());
    }
}
