package com.dummyc0m.bukkit.townhall.item;

import com.dummyc0m.bukkit.townhall.core.chatinput.CoreChatTrigger;
import com.dummyc0m.bukkit.townhall.core.item.ACItem;
import com.dummyc0m.bukkit.townhall.core.item.ACItemData;
import com.dummyc0m.bukkit.townhall.core.util.I18N;
import com.dummyc0m.bukkit.townhall.gui.MessageMenu;
import com.dummyc0m.bukkit.townhall.region.RegionManager;
import com.dummyc0m.bukkit.townhall.region.RegionPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Dummyc0m on 3/20/16.
 */
public class MessageStaff extends ACItem {
    private RegionManager regionManager;
    private CoreChatTrigger trigger;

    public MessageStaff(RegionManager manager, CoreChatTrigger trigger) {
        super(new ACItemData(Material.PAPER,
                (short) 0, 1,
                new ArrayList<>(Arrays.asList(I18N.translateKey("item.townhall.messageStaff.lore1"), I18N.translateKey("item.townhall.messageStaff.lore2"))),
                I18N.translateKey("item.townhall.messageStaff.name"),
                true, false, true, true, true, true), "TownHall", "townhall:message_staff");
        regionManager = manager;
        this.trigger = trigger;
    }

    @Override
    public boolean onInteract(Player who, ItemStack itemStack, Action action, Block clickedBlock, BlockFace clickedFace) {
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            RegionPlayer rPlayer = regionManager.getRegionPlayer(who.getUniqueId());
            if (rPlayer != null) {
                MessageMenu menu = new MessageMenu(trigger, null, rPlayer.getInbox(), 0);
                menu.display(who);
            }
        }
        return super.onInteract(who, itemStack, action, clickedBlock, clickedFace);
    }
}
