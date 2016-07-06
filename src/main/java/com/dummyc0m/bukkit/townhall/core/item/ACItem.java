package com.dummyc0m.bukkit.townhall.core.item;

import com.dummyc0m.bukkit.townhall.core.util.FormatUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * com.dummyc0m.bukkit.townhall.item
 * Created by Dummyc0m on 3/15/15.
 */
public class ACItem {
    private final String module;
    private final String identifier;
    private final ACItemData data;
    private ItemStack itemStack;

    public ACItem() {
        this(new ACItemData(), "Amethyst", "amethyst:default");
    }

    public ACItem(ACItemData data, String module, String identifier) {
        this.data = data;
        this.module = module;
        this.identifier = identifier;
    }

    /**
     * Called when a player interacts with an object or air using this item.
     *
     * @param who          The player.
     * @param itemStack    The Item
     * @param action       The action performed with the item.
     * @param clickedBlock The block clicked.
     * @param clickedFace  The block face clicked.
     * @return <p>True if the event is canceled.</p>
     * <p>This event will fire as cancelled if the vanilla behavior is to do nothing (e.g interacting with air)</p>
     */
    public boolean onInteract(Player who, ItemStack itemStack, Action action, Block clickedBlock, BlockFace clickedFace) {
        return data.isClickable();
    }

    /**
     * Called when a player interacts with an entity using this item.
     *
     * @param who           The player.
     * @param clickedEntity The entity clicked.
     * @param position      The location of the click.
     * @return <p>True if the event is canceled.</p>
     */
    public boolean onEntityInteract(Player who, ItemStack itemStack, Entity clickedEntity, Vector position) {
        return data.isInteractable();
    }

    /**
     * Called when a player picks up this item.
     *
     * @param who       The player.
     * @param remaining The amount of item (if any) remaining on the ground.
     * @return <p>True if the event is canceled.</p>
     */
    public boolean onPickUp(Player who, ItemStack itemStack, int remaining) {
        return !data.isTransformable();
    }

    /**
     * Called when a player drops this item.
     *
     * @param who The player.
     * @return <p>True if the event is canceled.</p>
     */
    public boolean onDrop(Player who, ItemStack itemStack) {
        return !data.isDroppable();
    }

    /**
     * Called when a player selects this item.
     *
     * @param who      The player.
     * @param previous The previously selected item slot.
     * @param current  The currently selected item slot.
     * @return <p>True if the event is canceled.</p>
     */
    public boolean onSelected(Player who, ItemStack itemStack, int previous, int current) {
        return false;
    }

    /**
     * Called when a player consumes this item.
     *
     * @param who The player.
     * @return <p>True if the event is canceled.</p>
     */
    public boolean onConsume(Player who, ItemStack itemStack) {
        return data.isConsumable();
    }

    /**
     * Called when a player breaks this item.
     *
     * @param who The player.
     */
    public void onBreak(Player who, ItemStack itemStack) {
    }

    public ItemStack getItemStack() {
        if (itemStack == null) {
            itemStack = data.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lores = itemMeta.getLore();
            lores.add(FormatUtil.RESET + FormatUtil.DARK_PURPLE + FormatUtil.ITALIC + FormatUtil.BOLD + this.module);
            lores.add(FormatUtil.RESET + FormatUtil.DARK_GRAY + this.identifier);
            itemMeta.setLore(lores);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public ACItemData getData() {
        return this.data;
    }

    public String getModule() {
        return module;
    }

    public String getIdentifier() {
        return identifier;
    }

    /**
     * Called when a player clicks this item in an AUMenu.
     *
     * @return The item to be replaced in the player's hand.
     */
    public boolean onInventoryInteract(HumanEntity humanEntity, InventoryType.SlotType type, int slot, ClickType click, ItemStack item) {
        return !this.data.isInventoryInteractable();
    }

}
