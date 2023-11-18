package net.knsh.cyclic.gui;

public class ButtonMachine extends ButtonTextured {
    private int tileField;

    public ButtonMachine(int xPos, int yPos, int width, int height, String displayString, OnPress handler, CreateNarration narrationSupplier) {
        super(xPos, yPos, width, height, displayString, handler, narrationSupplier);
    }

    public ButtonMachine(int xPos, int yPos, int width, int height, TextureEnum texture, int field, OnPress handler, CreateNarration narrationSupplier) {
        super(xPos, yPos, width, height, "", handler, narrationSupplier);
        this.tileField = field;
        this.setTextureId(texture);
    }

    public int getTileField() {
        return tileField;
    }

    public void setTileField(int tileField) {
        this.tileField = tileField;
    }
}
