package net.knsh.cyclic.library.util;

import net.knsh.cyclic.Cyclic;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class StringParseUtil {

    public static boolean isInList(final List<? extends String> list, ResourceLocation toMatch) {
        return isInList(list, toMatch, true);
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
                if (modIdFromList.equals(modIdToMatch) == false) {
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
