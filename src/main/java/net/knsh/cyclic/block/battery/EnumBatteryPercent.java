package net.knsh.cyclic.block.battery;

import net.minecraft.util.StringRepresentable;

public enum EnumBatteryPercent implements StringRepresentable {
    // from cyclic github EnumBatteryPercent.java
    ZERO, TWENTY, FOURTY, SIXTY, EIGHTY, NINETY, ONEHUNDRED;

    @Override
    public String getSerializedName() {
        if (this.name().equals(ONEHUNDRED.name())) {
            return "100";
        } else if (this.name().equals(NINETY.name())) {
            return "90";
        } else if (this.name().equals(EIGHTY.name())) {
            return "80";
        } else if (this.name().equals(SIXTY.name())) {
            return "60";
        } else if (this.name().equals(FOURTY.name())) {
            return "40";
        } else if (this.name().equals(TWENTY.name())) {
            return "20";
        } else {
            return "0";
        }
    }
}
