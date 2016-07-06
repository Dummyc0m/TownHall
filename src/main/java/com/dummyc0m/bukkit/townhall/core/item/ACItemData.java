package com.dummyc0m.bukkit.townhall.core.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * com.dummyc0m.bukkit.townhall.item
 * Created by Dummyc0m on 4/1/15.
 */
public class ACItemData {
    private final boolean clickable;
    private final boolean interactable;
    private final boolean inventoryInteractable;
    private final boolean droppable;
    private final boolean consumable;
    private final boolean transformable;

    private ItemStack itemInstance;

    public ACItemData() {
        this(Material.AIR);
    }

    public ACItemData(Material material) {
        this(material, (short) 0, 1, new ArrayList<>(), null);
    }

    public ACItemData(Material material, short damage, int amount, List<String> lores, String customName) {
        this(material, damage, amount, lores, customName, true, true, false, false, false, false);
    }

    /**
     * @param transformable true if the player is able to pick up this item.
     */
    public ACItemData(Material material, short damage, int amount, boolean clickable, boolean interactable, boolean inventoryInteractable, boolean droppable, boolean consumable, boolean transformable) {
        this(material, damage, amount, new ArrayList<>(), null, clickable, interactable, inventoryInteractable, droppable, consumable, transformable);
    }

    /**
     * @param transformable true if the player is able to pick up this item.
     */
    public ACItemData(Material material, short damage, int amount, List<String> lores, String customName, boolean clickable, boolean interactable, boolean inventoryInteractable, boolean droppable, boolean consumable, boolean transformable) {
        this.clickable = clickable;
        this.interactable = interactable;
        this.inventoryInteractable = inventoryInteractable;
        this.droppable = droppable;
        this.consumable = consumable;
        this.transformable = transformable;

        this.itemInstance = new ItemStack(material, amount, damage);
        ItemMeta itemMeta = this.itemInstance.getItemMeta();
        if (customName != null) {
            itemMeta.setDisplayName(customName);
        }
        itemMeta.setLore(lores);
        this.itemInstance.setItemMeta(itemMeta);
    }

    public boolean isClickable() {
        return clickable;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public boolean isInventoryInteractable() {
        return inventoryInteractable;
    }

    public boolean isDroppable() {
        return droppable;
    }

    public boolean isConsumable() {
        return consumable;
    }

    public boolean isTransformable() {
        return transformable;
    }

    public ItemStack getItemStack() {
        return itemInstance;
    }
}
