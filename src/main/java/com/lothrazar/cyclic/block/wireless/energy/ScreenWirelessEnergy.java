package com.lothrazar.cyclic.block.wireless.energy;

import com.lothrazar.cyclic.gui.ButtonMachineField;
import com.lothrazar.cyclic.gui.EnergyBar;
import com.lothrazar.cyclic.gui.ScreenBase;
import com.lothrazar.cyclic.registry.CyclicTextures;
import com.lothrazar.flib.core.Const;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenWirelessEnergy extends ScreenBase<ContainerWirelessEnergy> {
    private ButtonMachineField btnRedstone;
    private EnergyBar energy;

    public ScreenWirelessEnergy(ContainerWirelessEnergy screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void init() {
        super.init();
        this.energy = new EnergyBar(this.font, (int) TileWirelessEnergy.MAX);
        energy.guiLeft = leftPos;
        energy.guiTop = topPos;
        int x, y;
        x = leftPos + 6;
        y = topPos + 6;
        btnRedstone = addRenderableWidget(new ButtonMachineField(x, y, TileWirelessEnergy.Fields.REDSTONE.ordinal(), menu.tile.getBlockPos()));
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
        energy.renderHoveredToolTip(ms, mouseX, mouseY, menu.tile.getEnergy());
        btnRedstone.onValueUpdate(menu.tile);
    }

    @Override
    protected void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        this.drawButtonTooltips(ms, mouseX, mouseY);
        this.drawName(ms, title.getString());
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        this.drawBackground(ms, CyclicTextures.INVENTORY);
        //    this.drawSlot(ms, 79, 35, TextureRegistry.SLOT_GPS);
        int y = 35;
        for (int i = 0; i < 8; i++) {
            this.drawSlot(ms, 7 + i * Const.SQ, y, CyclicTextures.SLOT_GPS, 18);
        }
        energy.draw(ms, menu.tile.getEnergy());
    }
}
