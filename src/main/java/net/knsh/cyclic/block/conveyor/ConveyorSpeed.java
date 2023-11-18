package net.knsh.cyclic.block.conveyor;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public enum ConveyorSpeed implements StringRepresentable {

    SLOWEST, SLOW, MEDIUM, FAST, FASTEST;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }

    public ConveyorSpeed getNext() {
        switch (this) {
            case SLOWEST:
                return SLOW;
            case SLOW:
                return MEDIUM;
            case MEDIUM:
                return FAST;
            case FAST:
                return FASTEST;
            case FASTEST:
            default:
                return SLOWEST;
        }
    }

    public double getSpeed() {
        switch (this) {
            case SLOWEST:
                return 0.11D;
            case SLOW:
                return 0.12D;
            case MEDIUM:
                return 0.16D;
            case FAST:
                return 0.21D;
            case FASTEST:
                return 0.26D;
        }
        return 0;
    }
}