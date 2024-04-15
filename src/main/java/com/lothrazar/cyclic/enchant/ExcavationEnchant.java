package com.lothrazar.cyclic.enchant;

import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.library.enchant.EnchantmentCyclic;
import com.lothrazar.library.util.ItemStackUtil;
import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;
import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class ExcavationEnchant extends EnchantmentCyclic {
    public static final String ID = "excavate";
    public static ForgeConfigSpec.BooleanValue CFG;
    public static boolean effectiveToolRequired = true;

    public ExcavationEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot... applicableSlots) {
        super(rarity, category, applicableSlots);

        BlockEvents.BreakEvent.BLOCK_BREAK.register(event -> {
            if (!isEnabled()) {
                return;
            }
            LevelAccessor world = event.getLevel();
            Player player = event.getPlayer();
            if (world.isClientSide()) {
                return;
            }
            BlockPos pos = event.getPos();
            BlockState eventState = event.getState();
            Block block = eventState.getBlock();
            //is this item stack enchanted with ME?
            ItemStack stackHarvestingWith = player.getItemInHand(player.swingingArm);
            int level = this.getCurrentLevelTool(stackHarvestingWith);
            if (level <= 0) {
                return;
            }
            if (effectiveToolRequired && !PortingHooks.isCorrectToolForDrops(eventState, player)) {
                ModCyclic.LOGGER.info("excavate trigger cancelled; tool not effective");
                return;
            }
            //if (eventState.is(DataTags.EXCAVATE_IGNORED)) {
            //    ModCyclic.LOGGER.info("excavate trigger cancelled; see blocktag " + DataTags.EXCAVATE_IGNORED.toString());
            //    return;
            //}
            int harvested = this.harvestSurrounding((Level) world, player, pos, block, 1, level, player.swingingArm);
            if (harvested > 0) {
                //damage but also respect the unbreaking chant
                ItemStackUtil.damageItem(player, stackHarvestingWith);
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return CFG.get();
    }

    @Override
    public boolean isTradeable() {
        return isEnabled() && super.isTradeable();
    }

    @Override
    public boolean isDiscoverable() {
        return isEnabled() && super.isDiscoverable();
    }

    @Override
    public boolean isAllowedOnBooks() {
        return isEnabled() && super.isAllowedOnBooks();
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return isEnabled() && (super.canEnchant(stack) || stack.is(Tags.Items.SHEARS));
    }

    @Override
    public boolean checkCompatibility(Enchantment ench) {
        return true;
        //return super.checkCompatibility(ench) && ench != CyclicEnchants.EXPERIENCE_BOOST;
    }

    private int getHarvestMax(int level) {
        return 26 + 8 * level;
    }

    private int harvestSurrounding(final Level world, final Player player, final BlockPos posIn, final Block block, int totalBroken, final int level, InteractionHand swingingHand) {
        if (totalBroken >= this.getHarvestMax(level) || player.getItemInHand(player.swingingArm).isEmpty()) {
            return totalBroken;
        }
        Set<BlockPos> wasHarvested = new HashSet<>();
        Set<BlockPos> theFuture = this.getMatchingSurrounding(world, posIn, block);
        for (BlockPos targetPos : theFuture) {
            BlockState targetState = world.getBlockState(targetPos);
            //check canHarvest every time -> permission or any other hooks
            if (world.isEmptyBlock(targetPos)
                    || !player.mayBuild()
                    || !player.hasCorrectToolForDrops(targetState) //canHarvestBlock
                    || totalBroken >= this.getHarvestMax(level)
                    || player.getItemInHand(player.swingingArm).isEmpty()
                //          || ForgeEventFactory.doPlayerHarvestCheck(player, targetState, true)
                //          || !ForgeHooks.canHarvestBlock(targetState, player, world, targetPos)
            ) {
                continue;
            }
            if (world instanceof ServerLevel) {
                //important! use the version that takes the item stack. this way it will end up in Block:getDrops that references the LootContext.Builder
                //and since now loot tables are used, fortune and similar things will be respected
                Block.dropResources(targetState, world, targetPos, world.getBlockEntity(targetPos), player, player.getItemInHand(player.swingingArm));
            }
            int bonusLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, player.getMainHandItem());
            int silklevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem());
            int exp = ((CustomExpBlock)targetState).getExpDrop(targetState, world, world.random, targetPos, bonusLevel, silklevel);
            if (exp > 0 && world instanceof ServerLevel) {
                block.popExperience((ServerLevel) world, targetPos, exp);
            }
            world.destroyBlock(targetPos, false);
            wasHarvested.add(targetPos);
            totalBroken++;
        }
        if (wasHarvested.size() == 0) {
            //nothing was harvested here, dont move on
            return totalBroken;
        }
        //AFTER we harvest the close ones only THEN we branch out
        for (BlockPos targetPos : theFuture) {
            if (totalBroken >= this.getHarvestMax(level) || player.getItemInHand(player.swingingArm).isEmpty()) {
                break;
            }
            totalBroken += this.harvestSurrounding(world, player, targetPos, block, totalBroken, level, swingingHand);
        }
        return totalBroken;
    }

    private static final Direction[] VALUES = Direction.values();

    private Set<BlockPos> getMatchingSurrounding(Level world, BlockPos start, Block blockIn) {
        Set<BlockPos> list = new HashSet<BlockPos>();
        List<Direction> targetFaces = Arrays.asList(VALUES);
        try {
            // cannot replicate this error at all at max level (5 = V)
            // java.lang.StackOverflowError: Exception in server tick loop
            //      at java.util.Collections.swap(Unknown Source) ~[?:1.8.0_201] {}
            //      at java.util.Collections.shuffle(Unknown Source) ~[?:1.8.0_201] {}
            //      at java.util.Collections.shuffle(Unknown Source) ~[?:1.8.0_201] {}
            Collections.shuffle(targetFaces);
        }
        catch (Exception e) {
            // java.util shit the bed not my problem
        }
        for (Direction fac : targetFaces) {
            Block target = world.getBlockState(start.relative(fac)).getBlock();
            if (target == blockIn) {
                list.add(start.relative(fac));
            }
        }
        return list;
    }
}
