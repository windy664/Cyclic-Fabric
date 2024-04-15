package com.lothrazar.cyclic.block.antipotion;

import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.block.BlockCyclic;
import com.lothrazar.library.util.SoundUtil;
import com.lothrazar.library.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class MilkSpongeBlock extends BlockCyclic {
    public MilkSpongeBlock(Properties settings) {
        super(settings.randomTicks().strength(0.7F).sound(SoundType.GRASS));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock())) {
            this.absorbPotions(level, pos);
        }
    }

    private void absorbPotions(Level world, BlockPos pos) {
        List<LivingEntity> all = world.getEntitiesOfClass(LivingEntity.class, EntityUtil.makeBoundingBox(pos, AntiBeaconBlockEntity.RADIUS.get(), 3));
        ModCyclic.LOGGER.info("SPONGE try absorb potions on " + all.size());
        for (LivingEntity e : all) {
            if (!e.getActiveEffects().isEmpty()) {
                e.removeAllEffects();
                SoundUtil.playSound(e, SoundEvents.GENERIC_DRINK);
                ModCyclic.LOGGER.info("try absorb potions on " + e);
            }
        }
    }
}
