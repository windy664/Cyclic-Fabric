package net.knsh.cyclic.block.beaconpotion;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.item.datacard.EntityDataCard;
import net.knsh.flib.data.EntityFilterType;
import net.knsh.cyclic.registry.CyclicBlocks;
import net.knsh.cyclic.porting.neoforge.items.ForgeImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BeaconPotionBlockEntity extends BlockEntityCyclic implements ExtendedScreenHandlerFactory, ForgeImplementedInventory {
    enum Fields {
        TIMER, REDSTONE, RANGE, ENTITYTYPE
    }

    static final int MAX = 64000;
    private static final int TICKS_FIRE_PER = 60;
    static final int TICKS_PER_DURATION = 160000;
    private static final int POTION_TICKS = 20 * 20; //cant be too low BC night vision flicker
    private static final int MAX_RADIUS = 64;
    private int radius = MAX_RADIUS;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    private final List<MobEffectInstance> effects = new ArrayList<>();
    EntityFilterType entityFilter = EntityFilterType.PLAYERS;
    private final BeamParams beamParams = new BeamParams();

    public BeaconPotionBlockEntity(BlockPos pos, BlockState state) {
        super(CyclicBlocks.BEACON.blockEntity(), pos, state);
        timer = 0;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, BeaconPotionBlockEntity e) {
        e.tick(level, blockPos, blockState);
    }

    public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, BeaconPotionBlockEntity e) {
        e.tick(level, blockPos, blockState);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (this.requiresRedstone() && !this.isPowered()) {
            setLitProperty(false);
            return;
        }
        if (effects.size() == 0) {
            timer = 0;
        }
        timer--;
        if (timer > 0) {
            setLitProperty(true);
            tryAffectEntities();
        } else {
            effects.clear();
            ItemStack s = getItem(1);
            if (!s.isEmpty()) {
                List<MobEffectInstance> newEffects = PotionUtils.getMobEffects(s);
                if (newEffects.size() > 0) {
                    pullFromItem(newEffects);
                }
            } else {
                setLitProperty(false);
            }
            return;
        }
        updateBeam(level, pos, beamParams);
    }

    private void pullFromItem(List<MobEffectInstance> newEffects) {
        //add new effects
        //this.timer = TICKS_PER_DURATION;
        setLitProperty(true);
        //first read all potins
        int maxDur = 0;
        for (MobEffectInstance eff : newEffects) {
            if (this.isPotionValid(eff)) { //cannot set the duration time so we must copy it
                effects.add(new MobEffectInstance(eff.getEffect(), POTION_TICKS, eff.getAmplifier(), true, false));
                maxDur = Math.max(eff.getDuration(), maxDur);
            }
        }
        this.timer = maxDur;
        extractItem(1, 1, false);
    }

    private void tryAffectEntities() {
        if (timer % TICKS_FIRE_PER == 0 && effects.size() > 0) {
            affectEntities();
        }
    }

    private int affectEntities() {
        boolean showParticles = false;
        int affecdted = 0;
        List<? extends LivingEntity> list = this.entityFilter.getEntities(level, worldPosition, radius);
        for (LivingEntity entity : list) {
            if (entity == null) {
                continue;
            }
            if (EntityDataCard.hasEntity(getItem(0))
                    && !EntityDataCard.matchesEntity(entity, getItem(0))) {
                continue;
            }
            for (MobEffectInstance eff : this.effects) {
                affecdted++;
                if (entity.hasEffect(eff.getEffect())) {
                    entity.getEffect(eff.getEffect()).update(eff);
                }
                else {
                    entity.addEffect(new MobEffectInstance(eff.getEffect(), POTION_TICKS, eff.getAmplifier(), true, showParticles));
                }
            }
        }
        return affecdted;
    }

    private boolean isPotionValid(MobEffectInstance eff) {
        return eff.getEffect().isBeneficial() && !eff.getEffect().isInstantenous();
    }

    @Override
    public void load(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, inventory);
        this.radius = tag.getInt("radius");
        entityFilter = EntityFilterType.values()[tag.getInt("entityFilter")];
        if (tag.contains("Effects", 9)) {
            ListTag listnbt = tag.getList("Effects", 10);
            this.effects.clear();
            for (int i = 0; i < listnbt.size(); ++i) {
                MobEffectInstance effectinstance = MobEffectInstance.load(listnbt.getCompound(i));
                if (effectinstance != null) {
                    effects.add(effectinstance);
                }
            }
        }
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putInt("radius", radius);
        tag.putInt("entityFilter", entityFilter.ordinal());
        if (!this.effects.isEmpty()) {
            ListTag listnbt = new ListTag();
            for (MobEffectInstance effectinstance : this.effects) {
                listnbt.add(effectinstance.save(new CompoundTag()));
            }
            tag.put("Effects", listnbt);
        }
        super.saveAdditional(tag);
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
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public void setField(int field, int value) {
        switch (Fields.values()[field]) {
            case REDSTONE -> this.needsRedstone = value % 2;
            case TIMER -> this.timer = value;
            case ENTITYTYPE -> {
                value = value % EntityFilterType.values().length;
                this.entityFilter = EntityFilterType.values()[value];
            }
            case RANGE -> {
                if (value > MAX_RADIUS) {
                    radius = MAX_RADIUS;
                } else {
                    this.radius = Math.min(value, MAX_RADIUS);
                }
            }
        }
    }

    @Override
    public int getField(int id) {
        return switch (Fields.values()[id]) {
            case REDSTONE -> this.needsRedstone;
            case TIMER -> this.timer;
            case ENTITYTYPE -> this.entityFilter.ordinal();
            case RANGE -> this.radius;
        };
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return CyclicBlocks.BEACON.block().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new BeaconPotionContainer(i, playerInventory, this, level, worldPosition);
    }

    public List<String> getPotionDisplay() {
        List<String> list = new ArrayList<>();
        for (MobEffectInstance eff : this.effects) {
            list.add(Component.translatable(eff.getDescriptionId()).getString());
        }
        return list;
    }

    public String getTimerDisplay() {
        if (this.effects.size() == 0) {
            return Component.translatable("cyclic.gui.empty").getString();
        }
        return this.getTimerSeconds() + " seconds";
    }

    private int getTimerSeconds() {
        return timer / 20;
    }
}
