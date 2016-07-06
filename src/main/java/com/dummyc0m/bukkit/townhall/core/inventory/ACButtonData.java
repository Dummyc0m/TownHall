package com.dummyc0m.bukkit.townhall.core.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * com.dummyc0m.bukkit.townhall.inventory
 * Created by Dummyc0m on 4/3/15.
 */
public class ACButtonData {
    private final boolean droppable;
    private final boolean transferable;

    private final Material material;
    private final int amount;
    private final short damage;

    private ItemStack itemInstance;

    public ACButtonData() {
        this(Material.AIR);
    }

    public ACButtonData(Material material) {
        this(material, 1, (short) 0);
    }

    public ACButtonData(Material material, int amount, short damage) {
        this(material, amount, damage, false, false);
    }

    public ACButtonData(Material material, int amount, short damage, boolean droppable, boolean transferable) {
        this.droppable = droppable;
        this.transferable = transferable;
        this.material = material;
        this.amount = amount;
        this.damage = damage;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isTransferable() {
        return transferable;
    }

    public boolean isDroppable() {
        return droppable;
    }

    public ItemStack getItemStack() {
        if (this.itemInstance == null) {
            this.itemInstance = new ItemStack(this.material, this.amount, this.damage);
        }
        return itemInstance;
    }
}
