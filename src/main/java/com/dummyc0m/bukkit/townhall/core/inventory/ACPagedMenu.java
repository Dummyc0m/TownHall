package com.dummyc0m.bukkit.townhall.core.inventory;

/**
 * com.dummyc0m.bukkit.townhall.inventory
 * Created by Dummyc0m on 3/19/15.
 */
public class ACPagedMenu extends ACMenu {
    private ACMenu prev;

    public ACPagedMenu(int size, String title, ACMenuButton[] contents, ACMenu prev) {
        super(size, title, contents);
        this.prev = prev;
    }

    public ACPagedMenu(int size, String title, ACMenu prev) {
        super(size, title);
        this.prev = prev;
    }

    public ACMenu getPrev() {
        return prev;
    }

    public void setPrev(ACMenu prev) {
        this.prev = prev;
    }
}
