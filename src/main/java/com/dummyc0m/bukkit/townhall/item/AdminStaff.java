package com.dummyc0m.bukkit.townhall.item;

import com.dummyc0m.bukkit.townhall.TownHallPlugin;
import com.dummyc0m.bukkit.townhall.core.chatinput.CoreChatTrigger;
import com.dummyc0m.bukkit.townhall.core.item.ACItem;
import com.dummyc0m.bukkit.townhall.core.item.ACItemData;
import com.dummyc0m.bukkit.townhall.core.util.I18N;
import com.dummyc0m.bukkit.townhall.gui.TownAdminMenu;
import com.dummyc0m.bukkit.townhall.region.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Dummyc0m on 3/7/16.
 */
public class AdminStaff extends ACItem {
    private final TownAdminMenu rootMenu;
    private final CoreChatTrigger coreChatTrigger;
    private final RegionManager regionManager;
    //private final String defaultData;

    public AdminStaff(RegionManager regionManager, CoreChatTrigger coreChatTrigger) {
        super(new ACItemData(Material.BLAZE_ROD,
                        (short) 0,
                        1,
                        new ArrayList<>(Arrays.asList(I18N.translateKey("item.townhall.adminStaff.lore1"),
                                I18N.translateKey("item.townhall.adminStaff.lore2"),
                                I18N.translateKey("item.townhall.adminStaff.lore3"))),
                        I18N.translateKey("item.townhall.adminStaff.name") + " Warning: Can Crash Server",
                        true, false, true, false, false, false),
                "TownHall",
                "townhall:admin_staff");
        rootMenu = new TownAdminMenu(regionManager);
        this.coreChatTrigger = coreChatTrigger;
        this.regionManager = regionManager;
//
//        JsonArray jsonArray = new JsonArray();
//        JsonArray locA = new JsonArray();
//        locA.add(false);
//        locA.add(0);
//        locA.add(0);
//        locA.add(0);
//        JsonArray locB = new JsonArray();
//        locB.add(false);
//        locB.add(0);
//        locB.add(0);
//        locB.add(0);
//        jsonArray.add(locA);
//        jsonArray.add(locB);
//        defaultData =  gson.toJson(jsonArray);
    }

    @Override
    public boolean onInteract(Player who, ItemStack itemStack, Action action, Block clickedBlock, BlockFace clickedFace) {
        switch (action) {
            case LEFT_CLICK_AIR:
                if (who.isSneaking()) {
                    create(who);
                }
                break;
            case LEFT_CLICK_BLOCK:
                if (who.isSneaking()) {
                    create(who);
                } else {
                    Location loc = clickedBlock.getLocation();
                    who.sendMessage(I18N.translateKeyFormat("chat.townhall.setLocA", loc.getBlockX(), loc.getBlockZ()));
                    who.setMetadata("townhall:adminStaff.locA", new FixedMetadataValue(TownHallPlugin.getInstance(), loc));
                }
                break;
            case RIGHT_CLICK_AIR:
                if (who.isSneaking()) {
                    rootMenu.display(who);
                }
                break;
            case RIGHT_CLICK_BLOCK:
//                String data = getData(itemStack);
//                if("".equals(data)) {
//                    data = defaultData;
//                }
//
//                JsonArray locB = parser.parse(data).getAsJsonArray().get(1).getAsJsonArray();
//                Location loc = clickedBlock.getLocation();
//                locB.set(0, new JsonPrimitive(true));
//                locB.set(1, new JsonPrimitive(loc.getBlockX()));
//                locB.set(2, new JsonPrimitive(loc.getBlockY()));
//                locB.set(3, new JsonPrimitive(loc.getBlockZ()));
//
//                putData(itemStack, data);
                if (who.isSneaking()) {
                    rootMenu.display(who);
                } else {
                    Location loc = clickedBlock.getLocation();
                    who.sendMessage(I18N.translateKeyFormat("chat.townhall.setLocB", loc.getBlockX(), loc.getBlockZ()));
                    who.setMetadata("townhall:adminStaff.locB", new FixedMetadataValue(TownHallPlugin.getInstance(), loc));
                }
                break;
        }
        return true;
    }

    private void create(Player player) {
        Optional<MetadataValue> metadataValue = player.getMetadata("townhall:adminStaff.locA").stream()
                .filter(meta -> meta.getOwningPlugin() instanceof TownHallPlugin)
                .findAny();

        Optional<MetadataValue> metadataValue1 = player.getMetadata("townhall:adminStaff.locB").stream()
                .filter(meta -> meta.getOwningPlugin() instanceof TownHallPlugin)
                .findAny();

        if (metadataValue.isPresent() && metadataValue1.isPresent()) {
            Location a = (Location) metadataValue.get().value();
            Location b = (Location) metadataValue1.get().value();

            player.sendMessage(I18N.translateKey("chat.townhall.enterTownDisplayName"));
            List<ChunkRef> selectedChunks = ChunkRef.getChunkRefs(a.getBlockX(), a.getBlockZ(), b.getBlockX(), b.getBlockZ());
            RegionWorld world = regionManager.getWorld(player.getWorld().getName());
            if (world == null) {
                player.sendMessage(I18N.translateKey("chat.townhall.unsupportedWorld"));
                return;
            }
            List<RegionBlock> blocks = new ArrayList<>();
            for (ChunkRef chunkRef : selectedChunks) {
                if (world.containsBlock(chunkRef)) {
                    player.sendMessage(I18N.translateKey("chat.townhall.unsupportedBlocks"));
                    return;
                } else {
                    blocks.add(new RegionBlock(chunkRef, RegionBlockType.NORMAL));
                }
            }
            coreChatTrigger.add(player.getUniqueId(), event -> {
                String message = event.getMessage();
                if (!"".equals(message)) {
                    RegionBuilder builder = new RegionBuilder(player.getWorld().getName());
                    if (!builder.setCenter((a.getBlockX() + b.getBlockX()) / 2, (a.getBlockZ() + b.getBlockZ()) / 2)) {
                        player.sendMessage(I18N.translateKey("chat.townhall.collide"));
                        return;
                    }
                    if (!builder.setName(message)) {
                        player.sendMessage(I18N.translateKey("chat.townhall.unsupportedDisplayName"));
                        return;
                    }
                    if (!builder.setOwner(player.getUniqueId())) {
                        player.sendMessage(I18N.translateKey("chat.townhall.townExists"));
                        return;
                    }
                    Region region = builder.build();
                    if (region == null) {
                        player.sendMessage(I18N.translateKeyFormat("chat.townhall.unexpectedError", "Total Failure"));
                        return;
                    }
                    if (!region.addBlocks(blocks)) {
                        player.sendMessage(I18N.translateKey("chat.townhall.notConnectedMultiple"));
                    }
                    player.sendMessage(I18N.translateKey("chat.townhall.createSuccess"));
                } else {
                    player.sendMessage(I18N.translateKey("chat.townhall.unsupportedDisplayName"));
                }
                event.setCancelled(true);
            });
        }
    }
}
