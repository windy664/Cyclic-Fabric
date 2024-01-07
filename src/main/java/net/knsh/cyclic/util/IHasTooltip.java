package net.knsh.cyclic.util;

import java.util.List;
import net.minecraft.network.chat.Component;

public interface IHasTooltip {
    /**
     * Get the translated list of tooltips for this
     *
     * @return tooltips as a list
     */
    List<Component> getTooltips();

    /**
     * Override the tooltip and set it to a new list containing this string
     *
     * @param tooltip
     */
    void setTooltip(String tooltip);

    /**
     * Add a tooltip to the list
     *
     * @param tooltip
     *          tooltip string in
     */
    void addTooltip(String tooltip);
}
