package com.lothrazar.cyclic.block.antipotion;

import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.block.BlockCyclic;
import com.lothrazar.cyclic.registry.CyclicBlocks;
import com.lothrazar.library.block.EntityBlockFlib;
import com.lothrazar.library.util.EntityUtil;
import com.lothrazar.library.util.StringParseUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AntiBeaconBlock extends BlockCyclic {
    private static final float[] COLOR = new float[] { 1, 1, 1 };

    public AntiBeaconBlock(BlockBehaviour.Properties settings) {
        super(settings.randomTicks().strength(0.7F).noOcclusion());
    }

    @Override
    public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return COLOR;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new AntiBeaconBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return EntityBlockFlib.createTickerHelper(blockEntityType, CyclicBlocks.ANTI_BEACON.blockEntity(), level.isClientSide ? AntiBeaconBlockEntity::clientTick : AntiBeaconBlockEntity::serverTick);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock())) {
            absorbPotions(level, pos);
        }
    }

    public static void absorbPotions(Level world, BlockPos pos) {
        List<LivingEntity> all = world.getEntitiesOfClass(LivingEntity.class, EntityUtil.makeBoundingBox(pos, AntiBeaconBlockEntity.RADIUS.get(), 3));
        for (LivingEntity e : all) {
            cureAllRelevant(e);
        }
    }

    private static void cureAllRelevant(LivingEntity e) {
        List<MobEffect> cureMe = new ArrayList<>();
        for (MobEffect mobEffect : e.getActiveEffectsMap().keySet()) {
            if (AntiBeaconBlock.doesConfigBlockEffect(mobEffect)) {
                cureMe.add(mobEffect);
            }
        }
        for (MobEffect curedEffect : cureMe) {
            ModCyclic.LOGGER.info("[potion cured] " + curedEffect);
            e.removeEffect(curedEffect);
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean doesConfigBlockEffect(MobEffect mobEffect) {
        if (AntiBeaconBlockEntity.HARMFUL_POTIONS.get() && mobEffect.getCategory() == MobEffectCategory.HARMFUL) {
            return true;
        }
        ResourceLocation potionId = Registries.MOB_EFFECT.registry().withPath(mobEffect.getDescriptionId());
        return StringParseUtil.isInList(AntiBeaconBlockEntity.POTIONS.get(), potionId);
    }

    /*
    public void isPotionApplicable(@Nullable MobEffectInstance instance) {
        if (instance == null) {
            return;
        }
        //this will cancel it
        if (AntiBeaconBlock.doesConfigBlockEffect(instance.getEffect())) {
            final boolean isPowered = false; // if im NOT powered, im running
            List<BlockPos> blocks = BlockstatesUtil.findBlocks(event.getEntity().getCommandSenderWorld(),
                    event.getEntity().blockPosition(), this, AntiBeaconBlockEntity.RADIUS.get(), isPowered);
            //can
            if (blocks != null && blocks.size() > 0) {
                Cyclic.LOGGER.info("[potion blocked] " + instance);
                event.setResult(Result.DENY);
            }
        }
    }*/
}
