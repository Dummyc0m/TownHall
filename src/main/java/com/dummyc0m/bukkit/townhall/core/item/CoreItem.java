package com.dummyc0m.bukkit.townhall.core.item;

import com.dummyc0m.bukkit.townhall.core.util.FormatUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * com.dummyc0m.bukkit.townhall.item
 * Created by Dummyc0m on 4/2/15.
 */
public class CoreItem {
    private final Map<String, ACItem> identifierMap;
    private final String hideFlag;

    public CoreItem() {
        identifierMap = new HashMap<>();
        hideFlag = FormatUtil.RESET + FormatUtil.DARK_GRAY;
    }

    public void registerItems(List<ACItem> acItems) {
        acItems.forEach(this::registerItem);
    }

    public void registerItem(ACItem acItem) {
        this.identifierMap.put(acItem.getIdentifier(), acItem);
    }

    public void deregisterItem(String identifier) {
        this.identifierMap.remove(identifier);
    }

    public String getHideFlag() {
        return hideFlag;
    }

    public ACItem getItem(String identifier) {
        if (identifier.startsWith(hideFlag)) {
            return this.identifierMap.get(identifier.replaceFirst(hideFlag, ""));
        }
        return this.identifierMap.get(identifier);
    }
}
