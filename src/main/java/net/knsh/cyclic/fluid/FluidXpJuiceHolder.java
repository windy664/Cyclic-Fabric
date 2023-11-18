package net.knsh.cyclic.fluid;

import net.knsh.cyclic.registry.CyclicFluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public abstract class FluidXpJuiceHolder extends CyclicFluid {
    public static final int COLOR = 0x22FF43;
    public static final String NAME = "block.cyclic.xpjuice";

    @Override
    public Fluid getSource() {
        return CyclicFluids.STILL_XP;
    }

    @Override
    public Fluid getFlowing() {
        return CyclicFluids.FLOWING_XP;
    }

    @Override
    public Item getBucket() {
        return CyclicFluids.XP_BUCKET;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState fluidState) {
        return CyclicFluids.XP_BLOCK.defaultBlockState().setValue(BlockStateProperties.LEVEL, getLegacyLevel(fluidState));
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
    }
}
