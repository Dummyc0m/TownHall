package com.dummyc0m.bukkit.townhall.core.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Dummyc0m on 3/7/16.
 */
public abstract class ACMetaButtonData extends ACButtonData {
    public ACMetaButtonData() {
    }

    public ACMetaButtonData(Material material) {
        super(material);
    }

    public ACMetaButtonData(Material material, int amount, short damage) {
        super(material, amount, damage);
    }

    public ACMetaButtonData(Material material, int amount, short damage, boolean droppable, boolean transferable) {
        super(material, amount, damage, droppable, transferable);
    }

    public abstract ItemMeta getCustomMeta(Player player, ItemMeta itemMeta);
}
