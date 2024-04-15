package com.lothrazar.cyclic.fluid.block;

import io.github.fabricators_of_create.porting_lib.util.SimpleFlowableFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MagmaFluidBlock extends LiquidBlock {
    public static class Flowing extends SimpleFlowableFluid.Flowing {
        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        protected int getSlopeFindDistance(LevelReader world) {
            return 2;
        }

        @Override
        public int getDropOff(LevelReader worldIn) {
            return 7;
        }
    }

    public static class Source extends SimpleFlowableFluid.Still {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public int getSlopeFindDistance(LevelReader worldIn) {
            return 2;
        }

        @Override
        public int getDropOff(LevelReader worldIn) {
            return 1;
        }
    }

    VoxelShape[] shapes = new VoxelShape[16];

    public MagmaFluidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
        int max = 15; //max of the property LEVEL.getAllowedValues()
        float offset = 0.875F;
        for (int i = 0; i <= max; i++) { //x and z go from [0,1]
            shapes[i] = Shapes.create(new AABB(0, 0, 0, 1, offset - i / 8F, 1));
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes[state.getValue(LEVEL).intValue()];
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return shapes[state.getValue(LEVEL).intValue()];
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof LivingEntity ent) {
            if (!ent.isOnFire()
                    && !ent.fireImmune()) {
                int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, ent);
                if (level < 4) {
                    ent.setSecondsOnFire(Mth.floor(worldIn.random.nextDouble() * 10));
                }
            }
        }
        super.entityInside(state, worldIn, pos, entityIn);
    }
}
