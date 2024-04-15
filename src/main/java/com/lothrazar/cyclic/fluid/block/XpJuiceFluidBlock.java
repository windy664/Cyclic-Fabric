package com.lothrazar.cyclic.fluid.block;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public class XpJuiceFluidBlock extends LiquidBlock {
    public XpJuiceFluidBlock(FlowingFluid supplier, BlockBehaviour.Properties props) {
        super(supplier, props);
    }
}
