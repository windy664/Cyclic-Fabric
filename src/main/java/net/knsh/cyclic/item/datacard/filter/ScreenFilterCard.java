package net.knsh.cyclic.item.datacard.filter;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.knsh.cyclic.data.CraftingActionEnum;
import net.knsh.cyclic.gui.ButtonTextured;
import net.knsh.cyclic.gui.ScreenBase;
import net.knsh.cyclic.gui.TextureEnum;
import net.knsh.cyclic.library.core.Const;
import net.knsh.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenFilterCard extends ScreenBase<ContainerFilterCard> {
    private ButtonTextured btnType;
    private final ContainerFilterCard screenContainer;

    public ScreenFilterCard(ContainerFilterCard screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.screenContainer = screenContainer;
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
    }

    @Override
    public void init() {
        super.init();
        int x = leftPos + 150;
        int y = topPos + 8;
        final int size = 20;
        btnType = this.addRenderableWidget(new ButtonTextured(x, y, size, size, TextureEnum.RENDER_HIDE, "", b -> {
            //pressed
            ClientPlayNetworking.send(PacketFilterCard.IDENTIFIER, PacketFilterCard.encode(new PacketFilterCard(CraftingActionEnum.EMPTY)));
            FilterCardItem.toggleFilterType(screenContainer.bag);
        }));
    }

    @Override
    protected void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        super.renderLabels(ms, mouseX, mouseY);
        this.drawButtonTooltips(ms, mouseX, mouseY);
        boolean filter = screenContainer.bag.getOrCreateTag().getBoolean("filter");
        btnType.setTextureId(filter ? TextureEnum.RENDER_HIDE : TextureEnum.RENDER_SHOW);
        btnType.setTooltip("cyclic.screen.filter." + filter);
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        this.drawBackground(ms, CyclicTextures.INVENTORY);
        for (int i = 0; i < 9; i++) {
            int y = 31;
            if (i == FilterCardItem.SLOT_FLUID) {
                this.drawSlot(ms, 7 + i * Const.SQ, y, CyclicTextures.SLOT_BUCKET, 18);
            }
            else {
                this.drawSlot(ms, 7 + i * Const.SQ, y);
            }
        }
    }
}
