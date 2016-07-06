package com.dummyc0m.bukkit.townhall.core.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * com.dummyc0m.bukkit.townhall.inventory
 * Created by Dummyc0m on 3/19/15.
 */
public class ACMenuButton {
    private final ACButtonData data;
    private ItemMeta originalMeta;

    public ACMenuButton(ACButtonData data) {
        this.data = data;
    }

    /**
     * @param menu
     * @param humanEntity
     * @param clickAction
     * @param item
     * @return it replaces the original button in the menu for that *certain instance*
     */
    public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ACMenu.ClickAction clickAction, ItemStack item) {
        return item;
    }

    public ItemStack onDrop(ACMenu menu, HumanEntity humanEntity, ACMenu.DropAction dropAction, ItemStack item) {
        return item;
    }

    public ItemStack onTransfer(ACMenu menu, HumanEntity humanEntity, ACMenu.TransferAction transferAction, ItemStack item) {
        return item;
    }

    public final ItemStack getItem(Player player) {
        ItemStack itemStack = this.data.getItemStack();
        if (originalMeta == null) {
            originalMeta = itemStack.getItemMeta();
        }
        if (data instanceof ACMetaButtonData) {
            itemStack.setItemMeta(((ACMetaButtonData) data).getCustomMeta(player, originalMeta.clone()));
        }
        return itemStack;
    }
}
