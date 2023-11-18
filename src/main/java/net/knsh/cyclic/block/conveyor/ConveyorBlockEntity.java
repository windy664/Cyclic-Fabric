package net.knsh.cyclic.block.conveyor;

import net.knsh.cyclic.registry.CyclicBlockEntities;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class ConveyorBlockEntity extends BlockEntity {
    public ConveyorBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.CONVEYOR.blockEntity(), pos, state);
    }

    public static void serverTick(Level world, BlockPos blockPos, BlockState blockState, ConveyorBlockEntity e) {
        e.tick();
    }

    public static <E extends BlockEntity> void clientTick(Level world, BlockPos blockPos, BlockState blockState, ConveyorBlockEntity e) {
        e.tick();
    }

    public void tick() {
        if (level == null || worldPosition == null) {
            return;
        }

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, new AABB(worldPosition).expandTowards(0.0F, 0.5F, 0.0F));
        for (Entity e : entities) {
            makeEntitiesTravel(e, this.getBlockState(), this.worldPosition);
        }
    }

    public static void makeEntitiesTravel(Entity entity, BlockState state, BlockPos pos) {
        if (entity instanceof Player player) {
            if (player.isCrouching()) {
                return;
            }
        }
        double normalizedX = entity.getX() - pos.getX();
        double normalizedZ = entity.getZ() - pos.getZ();
        final double offside = 0.01D;
        //if the normalized values are >1 or <0, they entity is right at the border so dont apply it now
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (facing.getAxis() == Axis.Z && (normalizedX > 1 - offside || normalizedX < 0 + offside)) {
            return;
        }
        if (facing.getAxis() == Axis.X && (normalizedZ > 1 - offside || normalizedZ < 0 + offside)) {
            return;
        }
        ConveyorType type = state.getValue(ConveyorBlock.TYPE);
        double heightLimit = (type.isVertical()) ? pos.getY() + 1.3D : pos.getY() + 0.125D;
        double speed = state.getValue(ConveyorBlock.SPEED).getSpeed(); //0.08D; //temp variable, replace with speed from blockstate later
        double xSpeed = 0.0D, zSpeed = 0.0D, ySpeed = 0.0D;
        if (entity.getY() > heightLimit) {
            return;
        }
        xSpeed = facing.getStepX() * speed;
        ySpeed = 0.0D;
        zSpeed = facing.getStepZ() * speed;
        if (type.isCorner()) {
            if (facing.getAxis() == Axis.Z && (normalizedX < 0.4 || normalizedX > 0.6)) {
                entity.setPosRaw(Math.floor(entity.getX()) + 0.5, entity.getY(), entity.getZ());
            }
            if (facing.getAxis() == Axis.X && (normalizedZ < 0.4 || normalizedZ > 0.6)) {
                //centralize Z
                entity.setPosRaw(entity.getX(), entity.getY(), Math.floor(entity.getZ()) + 0.5);
            }
        }
        if (type.isVertical()) {
            double hackEdge = 0.1;
            if (normalizedX < hackEdge || normalizedZ < hackEdge
                    || normalizedX > 1 - hackEdge || normalizedZ > 1 - hackEdge) {
                // ?? : investigate jump hacks here
                entity.setPosRaw(entity.getX(), entity.getY() + .2, entity.getZ());
            }
            ySpeed = speed * 1.3;
            if (type == ConveyorType.DOWN) {
                ySpeed *= -1;
            }
        }
        if (xSpeed != 0.0D || ySpeed != 0.0D || zSpeed != 0.0D) {
            entity.setDeltaMovement(xSpeed, ySpeed, zSpeed);
        }
    }
}
