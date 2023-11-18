package net.knsh.cyclic.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.knsh.cyclic.block.BlockEntityCyclic;
import net.knsh.cyclic.network.PacketIdentifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ButtonMachineField extends ButtonMachine {
    BlockPos tilePos;
    private TextureEnum textureZero;
    private TextureEnum textureOne;
    private TextureEnum textureTwo = TextureEnum.RENDER_OUTLINE;
    private String tooltipPrefix;

    public ButtonMachineField(int xPos, int yPos, int field, BlockPos pos) {
        this(xPos, yPos, field, pos, TextureEnum.REDSTONE_ON, TextureEnum.REDSTONE_NEEDED, "gui.cyclic.redstone", textSupplier -> textSupplier.get());
    }

    public ButtonMachineField(int xPos, int yPos, int field, BlockPos pos, TextureEnum toff, TextureEnum tonn, String tooltipPrefix, CreateNarration narrationSupplier) {
        super(xPos, yPos, 20, 20, "", (p) -> {
            //save included
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeInt(field);
            buf.writeBlockPos(pos);
            buf.writeBoolean(true);

            ClientPlayNetworking.send(PacketIdentifiers.TILE_DATA, buf);

        }, narrationSupplier);
        this.tilePos = pos;
        this.setTileField(field);
        this.textureZero = toff;
        this.textureOne = tonn;
        this.tooltipPrefix = tooltipPrefix;
    }

    public ButtonMachineField setSize(int size) {
        this.height = size;
        this.width = size;
        return this;
    }

    public void onValueUpdate(BlockEntityCyclic tile) {
        int val = tile.getField(this.getTileField());
        this.onValueUpdate(val);
    }

    private void onValueUpdate(int val) {
        setTooltip(this.tooltipPrefix + val);
        switch (val) {
            case 0:
                setTextureId(textureZero);
                break;
            case 1:
                setTextureId(textureOne);
                break;
            case 2:
                setTextureId(textureTwo);
                break;
        }
    }
}
