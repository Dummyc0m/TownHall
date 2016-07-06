package com.dummyc0m.bukkit.townhall.core.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Dummyc0m on 3/11/16.
 */
public class InventoryUtil {
    public static boolean tryRemoveItem(Inventory inventory, ItemStack type, int amount) {
        ItemStack[] items = inventory.getStorageContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (amount < 1) {
                inventory.setStorageContents(items);
                return true;
            }
            if (item != null && item.isSimilar(type)) {
                if (amount > item.getAmount()) {
                    amount -= item.getAmount();
                    items[i] = null;
                } else if (amount == item.getAmount()) {
                    items[i] = null;
                    inventory.setStorageContents(items);
                    return true;
                } else {
                    item.setAmount(item.getAmount() - amount);
                    inventory.setStorageContents(items);
                    return true;
                }
            }
        }
        return false;
    }
}
