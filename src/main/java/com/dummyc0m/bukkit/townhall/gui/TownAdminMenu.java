package com.dummyc0m.bukkit.townhall.gui;

import com.dummyc0m.bukkit.townhall.TownHallPlugin;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMenu;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMenuButton;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMetaButtonData;
import com.dummyc0m.bukkit.townhall.core.util.I18N;
import com.dummyc0m.bukkit.townhall.region.Region;
import com.dummyc0m.bukkit.townhall.region.RegionManager;
import com.dummyc0m.bukkit.townhall.region.RegionWorld;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dummyc0m on 3/7/16.
 */
public class TownAdminMenu extends ACMenu {
    public TownAdminMenu(RegionManager regionManager) {
        super(27, I18N.translateKey("menu.townhall.townAdmin.title"));

        ACMenuButton[] buttons = new ACMenuButton[27];
        // 12 13 14
        buttons[11] = new ACMenuButton(new ACMetaButtonData(Material.IRON_HOE) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.townAdmin.infoButton.title"));
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                Region region = regionManager.getRegion(player.getLocation());
                if (region == null) {
                    lore.add(I18N.translateKey("menu.townhall.townAdmin.infoButton.lore.vacant"));
                } else {
                    lore.add(I18N.translateKeyFormat("menu.townhall.townAdmin.infoButton.lore.occupied", region.getName()));
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        });

//        buttons[13] = new ACMenuButton(new ACMetaButtonData(Material.IRON_PICKAXE) {
//            @Override
//            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
//                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.townAdmin.createButton.title"));
//                List<String> lore = itemMeta.getLore();
//                if (lore == null) {
//                    lore = new ArrayList<>();
//                }
//                Region region = regionManager.getRegion(player.getLocation());
//                if (region == null) {
//                    lore.add(I18N.translateKey("menu.townhall.townAdmin.createButton.lore.vacant"));
//                } else {
//                    lore.add(I18N.translateKey("menu.townhall.townAdmin.createButton.lore.occupied"));
//                }
//                itemMeta.setLore(lore);
//                return itemMeta;
//            }
//        }) {
//            @Override
//            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
//                if (regionManager.getRegion(humanEntity.getLocation()) != null) {
//                    return super.onClick(menu, humanEntity, clickAction, item);
//                }
////                CompletableFuture<TownBuilder> future = new CompletableFuture<>();
////                coreChatTrigger.add(humanEntity.getUniqueId(), input -> {
////                    if(!"".equals(input)) {
////                        TownBuilder townBuilder = new TownBuilder(humanEntity.getLocation().getBlockX(),
////                                humanEntity.getLocation().getBlockZ(),
////                                humanEntity.getWorld().getTitle(),
////                                humanEntity.getUniqueId())
////                                .setDisplayName(input)
////                                .setTitle(UUID.randomUUID().toString());
////                        humanEntity.sendMessage(I18N.translateKey("chat.townhall.enterTownRadius"));
////                        future.complete(townBuilder);
////                    } else {
////                        humanEntity.sendMessage(I18N.translateKey("chat.townhall.unsupportedRadius"));
////                        future.complete(null);
////                    }
////                    return false;
////                });
//                UUID uuid = humanEntity.getUniqueId();
//                Bukkit.getScheduler().runTaskLater(TownHallPlugin.getInstance(), () -> {
//                    humanEntity.closeInventory();
//                    humanEntity.sendMessage(I18N.translateKey("chat.townhall.enterTownDisplayName"));
//                }, 1);
//
//                coreChatTrigger.add(uuid, event -> {
//                    String message = event.getMessage();
//                    if ("".equals(message)) {
//                        humanEntity.sendMessage(I18N.translateKey("chat.townhall.unsupportedDisplayName"));
//                    } else {
//                        TownBuilder builder = new TownBuilder(humanEntity.getLocation().getBlockX(),
//                                humanEntity.getLocation().getBlockZ(),
//                                humanEntity.getWorld().getName(),
//                                uuid)
//                                .setDisplayName(message)
//                                .setName(UUID.randomUUID().toString());
//                        humanEntity.sendMessage(I18N.translateKey("chat.townhall.enterTownRadius"));
//                        //Line Awareness
//                        coreChatTrigger.add(uuid, event1 -> {
//                            String message1 = event1.getMessage();
//                            if (MathUtil.isInteger(message1)) {
//                                int radius = Integer.parseInt(message1);
//                                if (radius > 0) {
//                                    humanEntity.sendMessage(I18N.translateKey("chat.townhall.createSuccess"));
//                                    Town town = builder.setRadius(radius).build();
//                                    humanEntity.sendMessage(town.toString());
//                                    Bukkit.getScheduler().runTaskLater(TownHallPlugin.getInstance(),
//                                            () -> regionManager.addRegion(town),
//                                            1);
//                                    return;
//                                }
//                            }
//                            humanEntity.sendMessage(I18N.translateKey("chat.townhall.unsupportedRadius"));
//                        });
//                        //Line Awareness
//                    }
//                });
//                return super.onClick(menu, humanEntity, clickAction, item);
//            }
//        };

        buttons[15] = new ACMenuButton(new ACMetaButtonData(Material.IRON_AXE) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.townAdmin.destroyButton.title"));
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                Region region = regionManager.getRegion(player.getLocation());
                if (region == null) {
                    lore.add(I18N.translateKey("menu.townhall.townAdmin.destroyButton.lore.vacant"));
                } else {
                    lore.add(I18N.translateKeyFormat("menu.townhall.townAdmin.destroyButton.lore.occupied", region.getName()));
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                RegionWorld world = regionManager.getWorld(humanEntity.getWorld().getName());
                if (world != null) {
                    Region region = world.getRegion(humanEntity.getLocation());
                    if (region != null) {
                        world.removeRegion(region.getName());
                    }
                }
                Bukkit.getScheduler().runTask(TownHallPlugin.getInstance(), humanEntity::closeInventory);
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };

        setContents(buttons);
    }
}
