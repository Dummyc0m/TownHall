package com.dummyc0m.bukkit.townhall.item;

import com.dummyc0m.bukkit.townhall.TownHallPlugin;
import com.dummyc0m.bukkit.townhall.core.chatinput.CoreChatTrigger;
import com.dummyc0m.bukkit.townhall.core.item.ACItem;
import com.dummyc0m.bukkit.townhall.core.item.ACItemData;
import com.dummyc0m.bukkit.townhall.core.util.I18N;
import com.dummyc0m.bukkit.townhall.gui.TownMenu;
import com.dummyc0m.bukkit.townhall.region.RegionManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Dummyc0m on 3/11/16.
 */
public class TownStaff extends ACItem {
    private final TownMenu rootMenu;

    public TownStaff(TownHallPlugin plugin, RegionManager regionManager, CoreChatTrigger coreChatTrigger) {
        super(new ACItemData(Material.EMERALD,
                        (short) 0, 1,
                        new ArrayList<>(Arrays.asList(I18N.translateKey("item.townhall.townStaff.lore1"), I18N.translateKey("item.townhall.townStaff.lore2"))),
                        I18N.translateKey("item.townhall.townStaff.name"),
                        true, false, true, true, true, true),
                "TownHall", "townhall:town_staff");
        rootMenu = new TownMenu(plugin, regionManager, coreChatTrigger);
    }

    @Override
    public boolean onInteract(Player who, ItemStack itemStack, Action action, Block clickedBlock, BlockFace clickedFace) {
        switch (action) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                showChunk(who);
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                rootMenu.display(who);
                break;
        }
        return super.onInteract(who, itemStack, action, clickedBlock, clickedFace);
    }

    private void showChunk(Player player) {
        int blockY = player.getLocation().getBlockY();
        Chunk chunk = player.getLocation().getChunk();
        World world = player.getWorld();
        Location corner1, corner2, corner3, corner4;
        for (int y = blockY; y < blockY + 2 && y < 255; y++) {
            for (int i = 0; i < 15; i++) {
                corner1 = chunk.getBlock(i, y, 0).getLocation().add(1, 0, 1);
                corner2 = chunk.getBlock(15, y, i).getLocation().add(1, 0, 1);
                corner3 = chunk.getBlock(15 - i, y, 15).getLocation().add(1, 0, 1);
                corner4 = chunk.getBlock(0, y, 15 - i).getLocation().add(1, 0, 1);

                world.playEffect(corner1, Effect.SMOKE, 0);
                world.playEffect(corner2, Effect.SMOKE, 0);
                world.playEffect(corner3, Effect.SMOKE, 0);
                world.playEffect(corner4, Effect.SMOKE, 0);
//                world.playEffect(corner1, Effect.MOBSPAWNER_FLAMES, 0);
//                world.playEffect(corner2, Effect.MOBSPAWNER_FLAMES, 0);
//                world.playEffect(corner3, Effect.MOBSPAWNER_FLAMES, 0);
//                world.playEffect(corner4, Effect.MOBSPAWNER_FLAMES, 0);
            }
        }
    }


}
