package net.knsh.cyclic.fluid;

import net.knsh.cyclic.registry.CyclicFluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public abstract class FluidMagmaHolder extends CyclicFluid {
    public static final int COLOR =  0x4B261F;
    public static final String NAME = "block.cyclic.magma";

    @Override
    public Fluid getSource() {
        return CyclicFluids.STILL_MAGMA;
    }

    @Override
    public Fluid getFlowing() {
        return CyclicFluids.FLOWING_MAGMA;
    }

    @Override
    public Item getBucket() {
        return CyclicFluids.MAGMA_BUCKET;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState fluidState) {
        return CyclicFluids.MAGMA_BLOCK.defaultBlockState().setValue(BlockStateProperties.LEVEL, getLegacyLevel(fluidState));
    }

    public static class Flowing extends FluidXpJuiceHolder {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return fluidState.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return false;
        }

        @Override
        protected int getSlopeFindDistance(LevelReader worldView) {
            return 2;
        }

        @Override
        protected int getDropOff(LevelReader worldView) {
            return 7;
        }
    }

    public static class Still extends FluidXpJuiceHolder {
        @Override
        public int getAmount(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return true;
        }

        @Override
        protected int getSlopeFindDistance(LevelReader worldView) {
            return 2;
        }
    }
}
