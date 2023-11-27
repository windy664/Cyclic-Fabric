package net.knsh.cyclic.block.antipotion;

import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.block.beaconpotion.BeamParams;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

import java.util.List;

public class AntiBeaconBlockEntity extends BlockEntityCyclic {
    public static IntValue RADIUS;
    public static IntValue TICKS;
    public static ConfigValue<List<? extends String>> POTIONS;
    public static BooleanValue HARMFUL_POTIONS;
    private BeamParams beamParams = new BeamParams();

    public AntiBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.ANTI_BEACON.blockEntity(), pos, state);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, AntiBeaconBlockEntity tile) {
        if (tile.isPowered()) {
            return; // redstone power = not running
        }
        //ok go
        tile.tick(level, blockPos);
        if (tile.timer <= 0) {
            AntiBeaconBlock.absorbPotions(level, blockPos);
            tile.timer = TICKS.get();
        }
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, AntiBeaconBlockEntity  e) {
        e.tick(level, blockPos);
    }

    private void tick(Level level, BlockPos blockPos) {
        updateBeam(level, blockPos, beamParams);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        beamParams.lastCheckY = level.getMinBuildHeight() - 1;
    }

    public List<BeaconBlockEntity.BeaconBeamSection> getBeamSections() {
        return beamParams.beamSections;
    }

    @Override
    public void setField(int field, int value) {}

    @Override
    public int getField(int field) {
        return 0;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }
}
