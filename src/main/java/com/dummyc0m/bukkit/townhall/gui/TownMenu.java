package com.dummyc0m.bukkit.townhall.gui;

import com.dummyc0m.bukkit.townhall.TownHallPlugin;
import com.dummyc0m.bukkit.townhall.core.chatinput.CoreChatTrigger;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMenu;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMenuButton;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMetaButtonData;
import com.dummyc0m.bukkit.townhall.core.util.I18N;
import com.dummyc0m.bukkit.townhall.core.util.InventoryUtil;
import com.dummyc0m.bukkit.townhall.region.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dummyc0m on 3/7/16.
 */
public class TownMenu extends ACMenu {
    private OwnerMenu ownerMenu;

    public TownMenu(TownHallPlugin plugin, RegionManager regionManager, CoreChatTrigger coreChatTrigger) {
        super(27, I18N.translateKey("menu.townhall.town.title"));
        ownerMenu = new OwnerMenu(regionManager, this);
        ACMenuButton[] buttons = new ACMenuButton[27];
        //Info Done
        buttons[11] = new ACMenuButton(new ACMetaButtonData(Material.IRON_HOE) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                Region region = regionManager.getRegion(player.getLocation());
                if (region == null) {
                    itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.infoButton.title.vacant"));
                } else {
                    itemMeta.setDisplayName(I18N.translateKeyFormat("menu.townhall.town.infoButton.title.occupied", region.getName()));
                    lore.add(I18N.translateKeyFormat("menu.townhall.town.infoButton.lore.owner", plugin.getServer()
                            .getOfflinePlayer(region.getOwner().getUniqueId()).getName()));
                    lore.add(I18N.translateKeyFormat("menu.townhall.town.infoButton.lore.memberCount", region.getPlayerCount()));
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        });

        ItemStack emerald = new ItemStack(Material.EMERALD, RegionBlockType.CENTER.getChunkPrice());
        //Create or Expand TODO
        buttons[13] = new ACMenuButton(new ACMetaButtonData(Material.IRON_PICKAXE) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                Region region = regionManager.getRegion(player.getLocation());
                RegionPlayer rPlayer = regionManager.getRegionPlayer(player.getUniqueId());
                RegionWorld world = regionManager.getWorld(player.getWorld().getName());
                if (world == null) {
                    itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.createButton.title.unsupportedWorld"));
                    return itemMeta;
                }
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region == null) {
                    if (rPlayer.getRegion() != null) {
                        if (rPlayer.isOwner()) {
                            itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.createButton.title.owner"));
                            lore.add(I18N.translateKey("menu.townhall.town.createButton.lore.owner"));
                        } else {
                            itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.createButton.title.leave"));
                            lore.add(I18N.translateKey("menu.townhall.town.createButton.lore.leave"));
                        }
                    } else {
                        itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.createButton.title.create"));
                        lore.add(I18N.translateKey("menu.townhall.town.createButton.lore.create"));
                    }
                } else if (rPlayer.isOwner()) {
                    itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.createButton.title.owner"));
                    lore.add(I18N.translateKey("menu.townhall.town.createButton.lore.owner"));
                } else if (rPlayer.getRegion() != null) {
                    itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.createButton.title.leave"));
                    lore.add(I18N.translateKey("menu.townhall.town.createButton.lore.leave"));
                } else {
                    itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.createButton.title.occupied"));
                    lore.add(I18N.translateKey("menu.townhall.town.createButton.lore.occupied"));
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity player, ClickAction clickAction, ItemStack item) {
                Region region = regionManager.getRegion(player.getLocation());
                RegionPlayer rPlayer = regionManager.getRegionPlayer(player.getUniqueId());
                RegionWorld world = regionManager.getWorld(player.getWorld().getName());
                if (world == null) {
                    player.sendMessage(I18N.translateKey("chat.townhall.unsupportedWorld"));
                    return super.onClick(menu, player, clickAction, item);
                }
                if (region == null) {
                    if (rPlayer.getRegion() == null) {
                        //create logic
                        if (player.getInventory().containsAtLeast(emerald, RegionBlockType.CENTER.getChunkPrice())) {
                            UUID uuid = player.getUniqueId();
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                player.closeInventory();
                                player.sendMessage(I18N.translateKey(I18N.translateKey("chat.townhall.enterTownDisplayName")));
                            });

                            coreChatTrigger.add(uuid, event -> {
                                String name = event.getMessage();
                                if ("".equals(name) || name.length() > 20) {
                                    player.sendMessage(I18N.translateKey("chat.townhall.unsupportedDisplayName"));
                                } else {
                                    player.sendMessage(I18N.translateKey("chat.townhall.repeatTownDisplayName"));
                                    coreChatTrigger.add(uuid, event1 -> {
                                        if (event.getMessage().equals(name)) {
                                            RegionBuilder regionBuilder = new RegionBuilder(player.getWorld().getName());
                                            regionBuilder.setName(name);
                                            regionBuilder.setCenter(player.getLocation());
                                            regionBuilder.setOwner(uuid);
                                            Region built = regionBuilder.build();
                                            boolean deduction = InventoryUtil.tryRemoveItem(player.getInventory(), emerald, RegionBlockType.CENTER.getChunkPrice());
                                            if (!deduction || built == null) {
                                                player.sendMessage(I18N.translateKeyFormat("chat.townhall.unexpectedError", "Creation Rejected"));
                                            } else {
                                                player.sendMessage(I18N.translateKey("chat.townhall.createSuccess"));
                                                world.addRegion(built);
                                            }
                                        } else {
                                            player.sendMessage(I18N.translateKey("chat.townhall.incorrectDisplayName"));
                                        }
                                        event1.setCancelled(true);
                                    });
                                }
                                event.setCancelled(true);
                            });
                        } else {
                            player.sendMessage(I18N.translateKeyFormat("chat.townhall.insufficientBalance", RegionBlockType.CENTER.getChunkPrice()));
                            Bukkit.getScheduler().runTask(plugin, player::closeInventory);
                        }
                    } else {
                        //cannot create, player already has one
                        if (rPlayer.isOwner()) {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                ownerMenu.display((Player) player);
                            });
                        } else {
                            rPlayer.getRegion().removePlayer(rPlayer);
                            Bukkit.getScheduler().runTask(plugin, player::closeInventory);
                            player.sendMessage(I18N.translateKey("chat.townhall.leftTown"));
                        }
                    }
                } else if (rPlayer.isOwner()) {
                    //on his own town, open Owner Menu
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        ownerMenu.display((Player) player);
                    });
                } else if (rPlayer.getRegion() != null) {
                    //leave
                    rPlayer.getRegion().removePlayer(rPlayer);
                    Bukkit.getScheduler().runTask(plugin, player::closeInventory);
                    player.sendMessage(I18N.translateKey("chat.townhall.leftTown"));
                } else {
                    //not players other people's
                    player.sendMessage(I18N.translateKey("chat.townhall.townOccupied"));
                }
                return super.onClick(menu, player, clickAction, item);
            }
        };

        buttons[15] = new ACMenuButton(new ACMetaButtonData(Material.IRON_AXE) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                Region region = regionManager.getRegion(player.getLocation());
                RegionPlayer rPlayer = regionManager.getRegionPlayer(player.getUniqueId());
                RegionWorld world = regionManager.getWorld(player.getWorld().getName());
                if (world == null) {
                    itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.memberButton.title"));
                    return itemMeta;
                }
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region == null || region.getOwner().equals(rPlayer)) {
                    Region pRegion = rPlayer.getRegion();
                    if (pRegion != null) {
                        itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.memberButton.title.exist"));
                        List<RegionPlayer> players = pRegion.getPlayers();
                        Server server = plugin.getServer();
                        for (int i = 0; i < players.size() && i < 7; i++) {
                            OfflinePlayer player1 = server.getOfflinePlayer(players.get(i).getUniqueId());
                            if (player1 == null) {
                                continue;
                            }
                            lore.add(I18N.translateKeyFormat("menu.townhall.town.memberButton.lore.exist.member", player1.getName()));
                        }
                        lore.add(I18N.translateKeyFormat("menu.townhall.town.memberButton.lore.exist.total", players.size()));
                    } else {
                        itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.memberButton.title.join"));
                        lore.add(I18N.translateKey("menu.townhall.town.memberButton.lore.join"));
                    }
                } else {
                    itemMeta.setDisplayName(I18N.translateKey("menu.townhall.town.memberButton.title.occupied"));
                    List<RegionPlayer> players = region.getPlayers();
                    Server server = plugin.getServer();
                    for (int i = 0; i < players.size() && i < 7; i++) {
                        OfflinePlayer player1 = server.getOfflinePlayer(players.get(i).getUniqueId());
                        if (player1 == null) {
                            continue;
                        }
                        lore.add(I18N.translateKeyFormat("menu.townhall.town.memberButton.lore.occupied.member", player1.getName()));
                    }
                    lore.add(I18N.translateKeyFormat("menu.townhall.town.memberButton.lore.occupied.total", players.size()));
                    if (rPlayer.getRegion() == null) {
                        lore.add(I18N.translateKey("menu.townhall.town.memberButton.lore.occupied.join"));
                    }
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity player, ClickAction clickAction, ItemStack item) {
                Region region = regionManager.getRegion(player.getLocation());
                RegionPlayer rPlayer = regionManager.getRegionPlayer(player.getUniqueId());
                RegionWorld world = regionManager.getWorld(player.getWorld().getName());
                if (world == null) {
                    player.sendMessage(I18N.translateKey("chat.townhall.unsupportedWorld"));
                    return super.onClick(menu, player, clickAction, item);
                }
                Region pRegion = rPlayer.getRegion();
                if (rPlayer.getRegion() == null) {
                    if (region == null) {
                        //join other
                        player.sendMessage(I18N.translateKey("chat.townhall.enterApplicationTarget"));
                        coreChatTrigger.add(player.getUniqueId(), event -> {
                            Region targetRegion = world.getRegion(event.getMessage());
                            if (targetRegion == null) {
                                player.sendMessage(I18N.translateKey("chat.townhall.invalidApplicationTarget"));
                            } else {
                                applyToRegion(coreChatTrigger, player, targetRegion);
                            }
                            event.setCancelled(true);
                        });
                    } else {
                        //join this
                        applyToRegion(coreChatTrigger, player, region);
                    }
                    Bukkit.getScheduler().runTask(plugin, player::closeInventory);
                }
                return super.onClick(menu, player, clickAction, item);
            }
        };
        setContents(buttons);
    }

//    chat.townhall.enterMessageRecipient=[§bTownHall Messages§r] Please enter the message recipient:
//    chat.townhall.enterMessageTitle=[§bTownHall Messages§r] Please enter the message title:
//    chat.townhall.enterMessageBody=[§bTownHall Messages§r] Please enter the message:
//    chat.townhall.enterApplicationTarget=[§bTownHall Messages§r] Please enter the name of the town:
//    chat.townhall.invalidMessageRecipient=[§bTownHall Messages§r] The player does not exist
//    chat.townhall.invalidApplicationTarget=[§bTownHall Messages§r] The town does not exist

    private void applyToRegion(CoreChatTrigger trigger, HumanEntity player, Region region) {
        UUID uuid = player.getUniqueId();
        player.sendMessage(I18N.translateKey("chat.townhall.enterMessageTitle"));
        PlayerMessage.PlayerMsgBuilder builder = new PlayerMessage.PlayerMsgBuilder();
        builder.setFrom(uuid, player.getName());
        builder.setApplication(true);
        trigger.add(uuid, event -> {
            String message = event.getMessage();
            if (message.length() > 20) {
                player.sendMessage(I18N.translateKey("chat.townhall.invalidMessageTitle"));
            } else {
                builder.setTitle(message);
                player.sendMessage(I18N.translateKey("chat.townhall.enterMessageBody"));
                trigger.add(uuid, event1 -> {
                    String message1 = event1.getMessage();
                    if (message1.length() > 140) {
                        player.sendMessage(I18N.translateKey("chat.townhall.invalidMessageBody"));
                    } else {
                        builder.setMessage(message1);
                        PlayerMessage playerMessage = builder.build();
                        RegionPlayer owner = region.getOwner();
                        if (owner == null) {
                            player.sendMessage(I18N.translateKeyFormat("chat.townhall.unexpectedError", "Invalid Region"));
                        } else {
                            player.sendMessage(I18N.translateKey("chat.townhall.messageSent"));
                            owner.getInbox().add(0, playerMessage);
                        }
                    }
                    event1.setCancelled(true);
                });
            }
            event.setCancelled(true);
        });
    }
}
