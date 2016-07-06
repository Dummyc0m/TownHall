package com.dummyc0m.bukkit.townhall.core.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * com.dummyc0m.bukkit.townhall.inventory
 * Created by Dummyc0m on 3/15/15.
 */
public class ACMenu {
    private final int size;
    private final String title;
    private final List<ACMenuHolder> viewers;
    private ACMenuButton[] contents;

    public ACMenu(int size, String title, ACMenuButton[] contents) {
        this(size, title);
        this.contents = contents;
    }

    public ACMenu(int size, String title) {
        this.size = size;
        this.title = title;
        this.viewers = new ArrayList<>();
    }

    /**
     * Called when a player opens this inventory.
     *
     * @param inventory The inventory.
     * @param who       The player.
     * @return <p>True if the event is canceled.</p>
     */
    public boolean onOpen(Inventory inventory, HumanEntity who) {
        return false;
    }

    /**
     * Called when a player closes this inventory.
     *
     * @param inventory The inventory.
     * @param who       The player.
     */
    public void onClose(Inventory inventory, HumanEntity who) {
        //noinspection SuspiciousMethodCalls
        this.viewers.remove(inventory.getHolder());
    }

    /**
     * Called when a player clicks in this inventory.
     *
     * @param inventory The inventory.
     * @param type      Inventory slot type.
     * @param slot      Inventory slot.
     * @param click     Type of click.
     * @return <p>True if the event is canceled.</p>
     */
    public final ItemStack onClick(Inventory inventory, InventoryType.SlotType type, int slot, ClickType click, ItemStack item, HumanEntity whoClicked) {
        if (type == InventoryType.SlotType.CONTAINER && inventory.getType() == InventoryType.CHEST && slot > -1 && slot < contents.length && item != null) {
            ACMenuButton button = this.contents[slot];
            if (button != null) {
                switch (click) {
                    case LEFT:
                        return button.onClick(this, whoClicked, ClickAction.LEFT, item);
                    case RIGHT:
                        return button.onClick(this, whoClicked, ClickAction.RIGHT, item);
                    case MIDDLE:
                        return button.onClick(this, whoClicked, ClickAction.MIDDLE, item);
                    case DOUBLE_CLICK:
                        return button.onClick(this, whoClicked, ClickAction.DOUBLE, item);
                    case WINDOW_BORDER_LEFT:
                        return button.onClick(this, whoClicked, ClickAction.BORDER_LEFT, item);
                    case WINDOW_BORDER_RIGHT:
                        return button.onClick(this, whoClicked, ClickAction.BORDER_RIGHT, item);
                    case SHIFT_LEFT:
                        return button.onTransfer(this, whoClicked, TransferAction.SHIFT_LEFT, item);
                    case SHIFT_RIGHT:
                        return button.onTransfer(this, whoClicked, TransferAction.SHIFT_RIGHT, item);
                    case NUMBER_KEY:
                        return button.onTransfer(this, whoClicked, TransferAction.NUMBER_KEY, item);
                    case DROP:
                        return button.onDrop(this, whoClicked, DropAction.DROP, item);
                    case CONTROL_DROP:
                        return button.onDrop(this, whoClicked, DropAction.DROP_STACK, item);
                }
            }
        }
        return item;
    }


    public void display(Player player) {
        ACMenuHolder holder = new ACMenuHolder(this, player);
        Inventory inventory = Bukkit.createInventory(holder, size, title);
        ItemStack[] itemStackList = new ItemStack[size];
        int i = 0;
        for (ACMenuButton b : contents) {
            if (b != null) {
                itemStackList[i] = b.getItem(player);
            }
            i++;
        }
        inventory.setContents(itemStackList);
        player.openInventory(inventory);
        this.viewers.add(holder);
    }

    public ACMenuButton[] getContents() {
        return contents;
    }

    public void setContents(ACMenuButton[] items) {
        this.contents = items;
    }

    public List<ACMenuHolder> getViewers() {
        return viewers;
    }

    public int getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    public enum ClickAction {
        LEFT, RIGHT, MIDDLE, DOUBLE, BORDER_LEFT, BORDER_RIGHT
    }

    public enum TransferAction {
        SHIFT_LEFT, SHIFT_RIGHT, NUMBER_KEY
    }

    public enum DropAction {
        DROP, DROP_STACK
    }


}
