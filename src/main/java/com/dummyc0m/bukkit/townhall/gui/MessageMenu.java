package com.dummyc0m.bukkit.townhall.gui;

import com.dummyc0m.bukkit.townhall.TownHallPlugin;
import com.dummyc0m.bukkit.townhall.core.chatinput.CoreChatTrigger;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMenu;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMenuButton;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMetaButtonData;
import com.dummyc0m.bukkit.townhall.core.inventory.ACPagedMenu;
import com.dummyc0m.bukkit.townhall.core.util.FormatUtil;
import com.dummyc0m.bukkit.townhall.core.util.I18N;
import com.dummyc0m.bukkit.townhall.region.PlayerMessage;
import com.dummyc0m.bukkit.townhall.region.Region;
import com.dummyc0m.bukkit.townhall.region.RegionManager;
import com.dummyc0m.bukkit.townhall.region.RegionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dummyc0m on 3/19/16.
 */
public class MessageMenu extends ACPagedMenu {
    private RegionManager regionManager = RegionManager.getManager();

    public MessageMenu(CoreChatTrigger trigger, ACMenu prev, List<PlayerMessage> messages, int indexPointer) {
        super(27, I18N.translateKey("menu.townhall.message.title"), prev);
        ACMenuButton[] buttons = getMessageButtons(new ACMenuButton[27], messages, indexPointer);

        buttons[19] = new ACMenuButton(new ACMetaButtonData(Material.ARROW) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.message.prevPageButton.title"));
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                Bukkit.getScheduler().runTask(TownHallPlugin.getInstance(), () -> {
                    if (prev != null) {
                        prev.display((Player) humanEntity);
                    } else {
                        humanEntity.closeInventory();
                    }
                    setPrev(null);
                });
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };

        buttons[22] = new ACMenuButton(new ACMetaButtonData(Material.FEATHER) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.message.writeMessageButton.title"));
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity player, ClickAction clickAction, ItemStack item) {
                Bukkit.getScheduler().runTask(TownHallPlugin.getInstance(), () -> {
                    player.closeInventory();
                    setPrev(null);
                });
                UUID uuid = player.getUniqueId();
                PlayerMessage.PlayerMsgBuilder builder = new PlayerMessage.PlayerMsgBuilder();
                builder.setFrom(uuid, player.getName());
                player.sendMessage(I18N.translateKey("chat.townhall.enterMessageRecipient"));
                trigger.add(uuid, playerNameEvent -> {
                    OfflinePlayer offlinePlayer = Bukkit.getPlayer(playerNameEvent.getMessage());
                    if (offlinePlayer != null) {
                        RegionPlayer regionPlayer = regionManager.getRegionPlayer(offlinePlayer.getUniqueId());
                        if (regionPlayer != null) {
                            player.sendMessage(I18N.translateKey("chat.townhall.enterMessageTitle"));
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
                                            player.sendMessage(I18N.translateKey("chat.townhall.messageSent"));
                                            regionPlayer.getInbox().add(playerMessage);
                                        }
                                        event1.setCancelled(true);
                                    });
                                }
                                event.setCancelled(true);
                            });
                            playerNameEvent.setCancelled(true);
                            return;
                        }
                    }
                    player.sendMessage(I18N.translateKey("chat.townhall.invalidMessageRecipient"));
                    playerNameEvent.setCancelled(true);
                });

                return super.onClick(menu, player, clickAction, item);
            }
        };

        buttons[25] = new ACMenuButton(new ACMetaButtonData(Material.ARROW) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.message.nextPageButton.title") + " Not Functional");
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                //TODO
//                if(messages.size() - indexPointer > 18) {
//                    MessageMenu nextMenu = new MessageMenu(menu, messages, indexPointer + 18);
//                    nextMenu.setContents(getMessageButtons(getContents(), messages, indexPointer + 18));
//                    Bukkit.getScheduler().runTask(TownHallPlugin.getInstance(), () -> {
//                        humanEntity.closeInventory();
//                        nextMenu.display((Player) humanEntity);
//                    });
//                }
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };
        setContents(buttons);
    }

    private ACMenuButton[] getMessageButtons(ACMenuButton[] buttons, List<PlayerMessage> messages, int indexPointer) {
        for (int i = 0; i < messages.size() - indexPointer && i < 18; i++) {
            PlayerMessage playerMessage = messages.get(i + indexPointer);
            if (playerMessage.isApplication()) {
                buttons[i] = new ACMenuButton(new ACMetaButtonData(Material.EMPTY_MAP) {
                    @Override
                    public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                        itemMeta.setDisplayName(FormatUtil.RESET + FormatUtil.GOLD + playerMessage.getTitle());
                        List<String> lore = itemMeta.getLore();
                        if (lore == null) {
                            lore = new ArrayList<>();
                        }
                        String message = playerMessage.getMessage();
                        for (int j = 0; j < message.length(); j += 20) {
                            if (message.length() > j + 19) {
                                lore.add(FormatUtil.RESET + FormatUtil.WHITE + message.substring(j, j + 20));
                            } else {
                                lore.add(FormatUtil.RESET + FormatUtil.WHITE + message.substring(j));
                            }
                        }
                        lore.add(I18N.translateKey("menu.townhall.message.messageButton.lore.accept"));
                        lore.add(I18N.translateKey("menu.townhall.message.messageButton.lore.reject"));
                        itemMeta.setLore(lore);
                        return itemMeta;
                    }
                }) {
                    @Override
                    public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                        switch (clickAction) {
                            case LEFT:
                                RegionPlayer rPlayer = regionManager.getRegionPlayer(humanEntity.getUniqueId());
                                Region region = rPlayer.getRegion();
                                if (region != null) {
                                    RegionPlayer playerToAdd = regionManager.getRegionPlayer(playerMessage.getFrom());
                                    if (playerToAdd != null) {
                                        region.addPlayer(playerToAdd);
                                        humanEntity.sendMessage(I18N.translateKeyFormat("chat.townhall.acceptedPlayer", playerToAdd.getUniqueId()));
                                    }
                                }
                                messages.remove(playerMessage);
                                return null;
                            case RIGHT:
                                messages.remove(playerMessage);
                                return null;
                        }
                        return super.onClick(menu, humanEntity, clickAction, item);
                    }
                };
            } else {
                buttons[i] = new ACMenuButton(new ACMetaButtonData(Material.EMPTY_MAP) {
                    @Override
                    public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                        itemMeta.setDisplayName(FormatUtil.RESET + FormatUtil.YELLOW + playerMessage.getTitle());
                        List<String> lore = itemMeta.getLore();
                        if (lore == null) {
                            lore = new ArrayList<>();
                        }
                        String message = playerMessage.getMessage();
                        for (int j = 0; j < message.length(); j += 20) {
                            if (message.length() > j + 19) {
                                lore.add(FormatUtil.RESET + FormatUtil.WHITE + message.substring(j, j + 20));
                            } else {
                                lore.add(FormatUtil.RESET + FormatUtil.WHITE + message.substring(j));
                            }
                        }
                        lore.add(I18N.translateKey("menu.townhall.message.messageButton.lore.delete"));
                        itemMeta.setLore(lore);
                        return itemMeta;
                    }
                }) {
                    @Override
                    public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                        switch (clickAction) {
                            case RIGHT:
                                messages.remove(playerMessage);
                                return null;
                        }
                        return super.onClick(menu, humanEntity, clickAction, item);
                    }
                };
            }

        }
        return buttons;
    }
}
