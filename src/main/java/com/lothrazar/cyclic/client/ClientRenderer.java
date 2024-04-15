package com.lothrazar.cyclic.client;

import com.lothrazar.library.util.LevelWorldUtil;
import com.lothrazar.library.util.RenderBlockUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import com.lothrazar.cyclic.item.datacard.LocationGpsCard;
import com.lothrazar.library.core.BlockPosDim;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRenderer {
    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register((context -> {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player == null) return;

            Level world = player.level();
            ItemStack stack = ItemStack.EMPTY;
            List<BlockPos> putboxHere = new ArrayList<>();
            float alpha = 1;
            Map<BlockPos, Color> renderCubes = new HashMap<>();

            // LOCATION GPS RENDER
            stack = player.getMainHandItem();
            if (stack.getItem() instanceof LocationGpsCard) {
                BlockPosDim loc = LocationGpsCard.getPosition(stack);
                if (loc != null) {
                    if (loc.getDimension() == null || loc.getDimension().equalsIgnoreCase(LevelWorldUtil.dimensionToString(world))) {
                        renderCubes.put(loc.getPos(), Color.BLUE);
                    }
                }
            }

            if (!renderCubes.keySet().isEmpty()) {
                float scale = 1;
                PoseStack matrix = context.matrixStack();
                Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                RenderBlockUtils.renderColourCubes(matrix, view, renderCubes, scale, alpha);
            }
        }));
    }
}
