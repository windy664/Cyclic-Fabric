package net.knsh.cyclic.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemCyclic extends Item {
    private static final int MAX_ENERGY = 16000;
    public static final String ENERGYTTMAX = "energyttmax";
    public static final String ENERGYTT = "energytt";
    public static final float INACCURACY_DEFAULT = 1.0F;
    public static final float VELOCITY_MAX = 1.5F;
    private boolean hasEnergy;

    public ItemCyclic(Properties properties) {
        super(properties);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
        // energy stuff here later
    }
}
