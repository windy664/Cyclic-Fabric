package net.knsh.cyclic.fluid.block;

import io.github.fabricators_of_create.porting_lib.util.SimpleFlowableFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlimeFluidBlock extends LiquidBlock {
    public static class Flowing extends SimpleFlowableFluid.Flowing {

        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        public int getSlopeFindDistance(LevelReader worldIn) {
            return 1;
        }

        @Override
        public int getDropOff(LevelReader worldIn) {
            return 2;
        }
    }

    public static class Source extends SimpleFlowableFluid.Still {

        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public int getSlopeFindDistance(LevelReader worldIn) {
            return 1;
        }

        @Override
        public int getDropOff(LevelReader worldIn) {
            return 6;
        }
    }

    VoxelShape shapes[] = new VoxelShape[16];

    public SlimeFluidBlock(FlowingFluid supplier, Block.Properties props) {
        super(supplier, props);
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

    @Deprecated
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return shapes[state.getValue(LEVEL).intValue()];
    }


    @Override
    public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn.isSuppressingBounce()) {
            super.fallOn(worldIn, state, pos, entityIn, fallDistance);
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        if (entityIn.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(worldIn, entityIn);
        }
        else {
            this.collision(entityIn);
        }
    }

    /**
     * From SlimeBlock.java bounceUp
     *
     * @param entity
     */
    private void collision(Entity entity) {
        Vec3 vec3d = entity.getDeltaMovement();
        if (vec3d.y < 0.0D) {
            double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setDeltaMovement(vec3d.x, -vec3d.y * d0, vec3d.z);
        }
    }
}
