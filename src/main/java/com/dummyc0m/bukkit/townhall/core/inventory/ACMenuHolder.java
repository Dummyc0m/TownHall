package com.dummyc0m.bukkit.townhall.core.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * com.dummyc0m.bukkit.townhall.inventory
 * Created by Dummyc0m on 3/16/15.
 */
public class ACMenuHolder implements InventoryHolder {
    private final ACMenu menu;
    private final HumanEntity player;

    public ACMenuHolder(ACMenu menu, HumanEntity player) {
        this.menu = menu;
        this.player = player;
    }

    public ACMenu getMenu() {
        return menu;
    }

    public HumanEntity getPlayer() {
        return player;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
