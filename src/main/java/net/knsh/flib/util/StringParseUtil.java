package net.knsh.flib.util;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.knsh.cyclic.Cyclic;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class StringParseUtil {

    public static boolean isInList(final List<? extends String> list, ResourceLocation toMatch) {
        return isInList(list, toMatch, true);
    }

    public static String getFluidRatioName(Storage<FluidVariant> handler) {
        if (handler instanceof SingleVariantStorage<FluidVariant> storage) {
            ResourceAmount<FluidVariant> resourceAmount = StorageUtil.findExtractableContent(handler, null);
            if (resourceAmount != null) {
                FluidStack fluid = new FluidStack(resourceAmount);
                String ratio = resourceAmount.amount() + "/" + storage.getCapacity();
                if (resourceAmount.amount() > 0) {
                    ratio += " " + fluid.getDisplayName().getString();
                }
                return ratio;
            } else {
                return 0 + "/" + storage.getCapacity();
            }
        }
        return "0";
    }


    /**
     * when wildcard is true:
     * <p>
     * If the list has "hc:*_sapling" and input is "hc:whatever_sapling" then match is true
     */
    public static boolean isInList(final List<? extends String> list, ResourceLocation toMatch, boolean matchWildcard) {
        if (toMatch == null || list == null) {
            return false;
        }
        String id = toMatch.getNamespace();
        for (String strFromList : list) {
            if (strFromList == null || strFromList.isEmpty()) {
                continue; //just ignore me
            }
            if (strFromList.equals(id)) {
                return true;
            }
            if (matchWildcard) {
                String[] blockIdArray = strFromList.split(":");
                if (blockIdArray.length <= 1) {
                    Cyclic.LOGGER.error("Invalid config value for block : ", strFromList);
                    return false;
                }
                String modIdFromList = blockIdArray[0];
                String blockIdFromList = blockIdArray[1]; //has the *
                String modIdToMatch = toMatch.getNamespace();
                String blockIdToMatch = toMatch.getPath();
                if (!modIdFromList.equals(modIdToMatch)) {
                    continue;
                }
                String blockIdListWC = blockIdFromList.replace("*", "");
                if (blockIdToMatch.contains(blockIdListWC)) {
                    return true;
                }
            }
        }
        return false;
    }
}
