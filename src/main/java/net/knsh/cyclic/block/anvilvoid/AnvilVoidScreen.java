package net.knsh.cyclic.block.anvilvoid;

import net.knsh.cyclic.gui.ButtonMachineField;
import net.knsh.cyclic.gui.ScreenBase;
import net.knsh.cyclic.gui.FluidBar;
import net.knsh.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AnvilVoidScreen extends ScreenBase<AnvilVoidScreenHandler> {
    private ButtonMachineField btnRedstone;
    private FluidBar fluid;

    public AnvilVoidScreen(AnvilVoidScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    public void init() {
        super.init();
        fluid = new FluidBar(this.font, 152, 8, (int) AnvilVoidBlockEntity.CAPACITY);
        fluid.guiLeft = leftPos;
        fluid.guiTop = topPos;
        int left, top;
        left = leftPos + 6;
        top = topPos + 6;
        btnRedstone = addRenderableWidget(
                new ButtonMachineField(left, top, AnvilVoidBlockEntity.Fields.REDSTONE.ordinal(), menu.tile.getBlockPos())
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
        btnRedstone.onValueUpdate(menu.tile);
        fluid.renderHoveredToolTip(context, mouseX, mouseY, menu.tile.getFluid());
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        this.drawButtonTooltips(context, mouseX, mouseY);
        this.drawName(context, this.title.getString());
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        this.drawBackground(context, CyclicTextures.INVENTORY);
        this.drawSlot(context, 54, 34);
        this.drawSlotLarge(context, 104, 30);
        fluid.draw(context, menu.tile.getFluid());
    }
}
