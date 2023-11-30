package net.knsh.cyclic.block.anvil;

import net.knsh.cyclic.gui.ButtonMachineField;
import net.knsh.cyclic.gui.EnergyBar;
import net.knsh.cyclic.gui.ScreenBase;
import net.knsh.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AnvilAutoScreen extends ScreenBase<AnvilAutoScreenHandler> {
    private ButtonMachineField btnRedstone;
    private EnergyBar energy;

    public AnvilAutoScreen(AnvilAutoScreenHandler screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void init() {
        super.init();
        this.energy = new EnergyBar(this.font, AnvilAutoBlockEntity.MAX);
        energy.visible = AnvilAutoBlockEntity.POWERCONF.get() > 0;
        energy.guiLeft = leftPos;
        energy.guiTop = topPos;
        int x, y;
        x = leftPos + 6;
        y = topPos + 6;
        btnRedstone = addRenderableWidget(new ButtonMachineField(x, y, AnvilAutoBlockEntity.Fields.REDSTONE.ordinal(), menu.tile.getBlockPos()));
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
        energy.renderHoveredToolTip(ms, mouseX, mouseY, (int) menu.tile.energy.getAmount());
        btnRedstone.onValueUpdate(menu.tile);
    }

    @Override
    protected void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        this.drawButtonTooltips(ms, mouseX, mouseY);
        this.drawName(ms, this.title.getString());
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        this.drawBackground(ms, CyclicTextures.INVENTORY);
        this.drawSlot(ms, 54, 34);
        this.drawSlotLarge(ms, 104, 30);
        energy.draw(ms, menu.tile.energy.getAmount());
    }
}
