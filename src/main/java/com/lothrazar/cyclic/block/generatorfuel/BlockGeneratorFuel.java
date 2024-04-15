package com.lothrazar.cyclic.block.generatorfuel;

import com.lothrazar.cyclic.block.BlockCyclic;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.cyclic.registry.CyclicScreens;
import com.lothrazar.library.block.EntityBlockFlib;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;

public class BlockGeneratorFuel extends BlockCyclic {

    public BlockGeneratorFuel(BlockBehaviour.Properties settings) {
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
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileGeneratorFuel(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return EntityBlockFlib.createTickerHelper(type, CyclicBlocks.GENERATOR_FUEL.blockEntity(), world.isClientSide ? TileGeneratorFuel::clientTick : TileGeneratorFuel::serverTick);
    }

    @Override
    public void registerLookups() {
        EnergyStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.energy), CyclicBlocks.GENERATOR_FUEL.blockEntity());
        ItemStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.inventory), CyclicBlocks.GENERATOR_FUEL.blockEntity());
    }
}
