package net.knsh.cyclic.block.beaconpotion;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.knsh.cyclic.gui.ButtonMachine;
import net.knsh.cyclic.gui.ButtonMachineField;
import net.knsh.cyclic.gui.ScreenBase;
import net.knsh.cyclic.library.util.ChatUtil;
import net.knsh.cyclic.network.PacketIdentifiers;
import net.knsh.cyclic.network.packets.PacketTileData;
import net.knsh.cyclic.registry.CyclicTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BeaconPotionScreen extends ScreenBase<BeaconPotionScreenHandler> {
    private ButtonMachine btnEntity;
    private ButtonMachineField btnRedstone;

    public BeaconPotionScreen(BeaconPotionScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    public void init() {
        super.init();
        int x, y;
        x = leftPos + 6;
        y = topPos + 6;
        btnRedstone = addRenderableWidget(new ButtonMachineField(x, y, BeaconPotionBlockEntity.Fields.REDSTONE.ordinal(), menu.tile.getBlockPos()));
        y += 51;
        btnEntity = addRenderableWidget(new ButtonMachine(x, y, 60, 20, "", (p) -> {
            int f = BeaconPotionBlockEntity.Fields.ENTITYTYPE.ordinal();
            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA,
                    PacketTileData.encode(new PacketTileData(f,menu.tile.getField(f) + 1, menu.tile.getBlockPos())));
        }));
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        this.drawButtonTooltips(ms, mouseX, mouseY);
        this.drawName(ms, this.title.getString());
        btnRedstone.onValueUpdate(menu.tile);
        btnEntity.setTooltip(ChatUtil.lang("cyclic.beacon.entitytype.tooltip"));
        btnEntity.setMessage(ChatUtil.ilang("cyclic.entitytype." + menu.tile.entityFilter.name().toLowerCase()));
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        this.drawBackground(ms, CyclicTextures.INVENTORY);
        this.drawSlot(ms, 148, 8, CyclicTextures.SLOT_FILTER, 18);
        this.drawSlot(ms, 8, 34);
        int x = leftPos + 29, y = topPos + 16;
        this.drawString(ms, menu.tile.getTimerDisplay(), x, y);
        for (String s : menu.tile.getPotionDisplay()) {
            y += 10;
            this.drawString(ms, s, x, y);
        }
    }
}
