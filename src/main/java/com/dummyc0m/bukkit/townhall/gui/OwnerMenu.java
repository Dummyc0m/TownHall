package com.dummyc0m.bukkit.townhall.gui;

import com.dummyc0m.bukkit.townhall.core.inventory.ACMenu;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMenuButton;
import com.dummyc0m.bukkit.townhall.core.inventory.ACMetaButtonData;
import com.dummyc0m.bukkit.townhall.core.inventory.ACPagedMenu;
import com.dummyc0m.bukkit.townhall.core.util.I18N;
import com.dummyc0m.bukkit.townhall.core.util.InventoryUtil;
import com.dummyc0m.bukkit.townhall.region.*;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dummyc0m on 3/12/16.
 */
public class OwnerMenu extends ACPagedMenu {
    private RegionManager regionManager;

    public OwnerMenu(RegionManager regionManager, ACMenu prev) {
        super(27, I18N.translateKey("menu.townhall.owner.title"), prev);
        this.regionManager = regionManager;
        ACMenuButton[] buttons = new ACMenuButton[27];
        ItemStack emerald = new ItemStack(Material.EMERALD);
        //Expand button
        buttons[21] = new ACMenuButton(new ACMetaButtonData(Material.IRON_AXE) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.expandButton"));
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity player, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(player.getUniqueId());
                if (region != null) {
                    if (region.checkChunkLimit(RegionBlockType.NORMAL)) {
                        ChunkRef chunkRef = new ChunkRef(player.getLocation());
                        if (region.getWorld().containsBlock(chunkRef)) {
                            player.sendMessage(I18N.translateKey("chat.townhall.collide"));
                        } else if (region.checkConnected(chunkRef)) {
                            if (InventoryUtil.tryRemoveItem(player.getInventory(), emerald, RegionBlockType.NORMAL.getChunkPrice())) {
                                region.addBlock(new RegionBlock(chunkRef, RegionBlockType.NORMAL));
                                player.sendMessage(I18N.translateKey("chat.townhall.expandSuccess"));
                            } else {
                                player.sendMessage(I18N.translateKeyFormat("chat.townhall.insufficientBalance", RegionBlockType.NORMAL.getChunkPrice()));
                            }
                        } else {
                            player.sendMessage(I18N.translateKey("chat.townhall.notConnected"));
                        }
                    } else {
                        player.sendMessage(I18N.translateKey("chat.townhall.limitReached"));
                    }
                }
                return super.onClick(menu, player, clickAction, item);
            }
        };

        buttons[23] = new ACMenuButton(new ACMetaButtonData(Material.DIAMOND_AXE) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.expandSpecialButton"));
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity player, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(player.getUniqueId());
                if (region != null) {
                    if (region.checkChunkLimit(RegionBlockType.SPECIAL)) {
                        ChunkRef chunkRef = new ChunkRef(player.getLocation());
                        if (region.getWorld().containsBlock(chunkRef)) {
                            player.sendMessage(I18N.translateKey("chat.townhall.collide"));
                        } else {
                            if (InventoryUtil.tryRemoveItem(player.getInventory(), emerald, RegionBlockType.SPECIAL.getChunkPrice())) {
                                region.addBlock(new RegionBlock(chunkRef, RegionBlockType.SPECIAL));
                                player.sendMessage(I18N.translateKey("chat.townhall.expandSuccess"));
                            } else {
                                player.sendMessage(I18N.translateKeyFormat("chat.townhall.insufficientBalance", RegionBlockType.SPECIAL.getChunkPrice()));
                            }
                        }
                    } else {
                        player.sendMessage(I18N.translateKey("chat.townhall.limitReached"));
                    }
                }
                return super.onClick(menu, player, clickAction, item);
            }
        };

        //attributes button
        //ProtectBuild
        buttons[1] = new ACMenuButton(new ACMetaButtonData(Material.BRICK) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.protectBuildButton"));
                Region region = getRegion(player.getUniqueId());
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region != null) {
                    if (region.isProtectBuild()) {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"));
                    } else {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"));
                    }
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(humanEntity.getUniqueId());
                if (region != null) {
                    if (region.isProtectBuild()) {
                        region.setProtectBuild(false);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"))));
                        item.setItemMeta(meta);
                    } else {
                        region.setProtectBuild(true);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"))));
                        item.setItemMeta(meta);
                    }
                }
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };

        //ProtectDestroy
        buttons[3] = new ACMenuButton(new ACMetaButtonData(Material.STONE_PICKAXE) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.protectDestroyButton"));
                Region region = getRegion(player.getUniqueId());
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region != null) {
                    if (region.isProtectDestroy()) {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"));
                    } else {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"));
                    }
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(humanEntity.getUniqueId());
                if (region != null) {
                    if (region.isProtectDestroy()) {
                        region.setProtectDestroy(false);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"))));
                        item.setItemMeta(meta);
                    } else {
                        region.setProtectDestroy(true);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"))));
                        item.setItemMeta(meta);
                    }
                }
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };

        //ProtectInteract
        buttons[5] = new ACMenuButton(new ACMetaButtonData(Material.WOOD_BUTTON) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.protectInteractButton"));
                Region region = getRegion(player.getUniqueId());
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region != null) {
                    if (region.isProtectInteract()) {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"));
                    } else {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"));
                    }
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(humanEntity.getUniqueId());
                if (region != null) {
                    if (region.isProtectInteract()) {
                        region.setProtectInteract(false);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"))));
                        item.setItemMeta(meta);
                    } else {
                        region.setProtectInteract(true);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"))));
                        item.setItemMeta(meta);
                    }
                }
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };

        //ProtectExplode
        buttons[7] = new ACMenuButton(new ACMetaButtonData(Material.TNT) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.protectExplodeButton"));
                Region region = getRegion(player.getUniqueId());
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region != null) {
                    if (region.isProtectExplode()) {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"));
                    } else {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"));
                    }
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(humanEntity.getUniqueId());
                if (region != null) {
                    if (region.isProtectExplode()) {
                        region.setProtectExplode(false);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"))));
                        item.setItemMeta(meta);
                    } else {
                        region.setProtectExplode(true);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"))));
                        item.setItemMeta(meta);
                    }
                }
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };

        //ProtectPlayer
        buttons[10] = new ACMenuButton(new ACMetaButtonData(Material.STONE_SWORD) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.protectPlayerButton"));
                Region region = getRegion(player.getUniqueId());
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region != null) {
                    if (region.isProtectPlayer()) {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"));
                    } else {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"));
                    }
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(humanEntity.getUniqueId());
                if (region != null) {
                    if (region.isProtectPlayer()) {
                        region.setProtectPlayer(false);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"))));
                        item.setItemMeta(meta);
                    } else {
                        region.setProtectPlayer(true);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"))));
                        item.setItemMeta(meta);
                    }
                }
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };

        //ProtectEntity
        buttons[16] = new ACMenuButton(new ACMetaButtonData(Material.ITEM_FRAME) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.protectEntityButton"));
                Region region = getRegion(player.getUniqueId());
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region != null) {
                    if (region.isProtectEntity()) {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"));
                    } else {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"));
                    }
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(humanEntity.getUniqueId());
                if (region != null) {
                    if (region.isProtectEntity()) {
                        region.setProtectEntity(false);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"))));
                        item.setItemMeta(meta);
                    } else {
                        region.setProtectEntity(true);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"))));
                        item.setItemMeta(meta);
                    }
                }
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };

        //ProtectEntry
        buttons[19] = new ACMenuButton(new ACMetaButtonData(Material.LEATHER_BOOTS) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.protectEntryButton"));
                Region region = getRegion(player.getUniqueId());
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region != null) {
                    if (region.isProtectEntry()) {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"));
                    } else {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"));
                    }
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(humanEntity.getUniqueId());
                if (region != null) {
                    if (region.isProtectEntry()) {
                        region.setProtectEntry(false);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"))));
                        item.setItemMeta(meta);
                    } else {
                        region.setProtectEntry(true);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"))));
                        item.setItemMeta(meta);
                    }
                }
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };

        //ProtectLockdown
        buttons[25] = new ACMenuButton(new ACMetaButtonData(Material.IRON_FENCE) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.lockDownButton"));
                Region region = getRegion(player.getUniqueId());
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region != null) {
                    if (region.isLockDown()) {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"));
                    } else {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"));
                    }
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(humanEntity.getUniqueId());
                if (region != null) {
                    if (region.isLockDown()) {
                        region.setLockDown(false);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"))));
                        item.setItemMeta(meta);
                    } else {
                        region.setLockDown(true);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"))));
                        item.setItemMeta(meta);
                    }
                }
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };

        //ProtectCreature
        buttons[13] = new ACMenuButton(new ACMetaButtonData(Material.MOB_SPAWNER) {
            @Override
            public ItemMeta getCustomMeta(Player player, ItemMeta itemMeta) {
                itemMeta.setDisplayName(I18N.translateKey("menu.townhall.owner.protectCreatureSpawnButton"));
                Region region = getRegion(player.getUniqueId());
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                if (region != null) {
                    if (region.isProtectCreatureSpawn()) {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"));
                    } else {
                        lore.add(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"));
                    }
                }
                itemMeta.setLore(lore);
                return itemMeta;
            }
        }) {
            @Override
            public ItemStack onClick(ACMenu menu, HumanEntity humanEntity, ClickAction clickAction, ItemStack item) {
                Region region = getRegion(humanEntity.getUniqueId());
                if (humanEntity.isOp() && region != null) {
                    if (region.isProtectCreatureSpawn()) {
                        region.setProtectCreatureSpawn(false);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.disabled"))));
                        item.setItemMeta(meta);
                    } else {
                        region.setProtectCreatureSpawn(true);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(new ArrayList<>(Collections.singleton(I18N.translateKey("menu.townhall.owner.protectButtons.lore.enabled"))));
                        item.setItemMeta(meta);
                    }
                }
                return super.onClick(menu, humanEntity, clickAction, item);
            }
        };
        setContents(buttons);
    }

    private Region getRegion(UUID uuid) {
        RegionPlayer regionPlayer = regionManager.getRegionPlayer(uuid);
        if (regionPlayer != null) {
            Region region = regionPlayer.getRegion();
            if (region != null && regionPlayer.isOwner()) {
                return region;
            }
        }
        return null;
    }

//    menu.townhall.owner.protectBuildButton=Build
//    menu.townhall.owner.protectDestroyButton=Destroy
//    menu.townhall.owner.protectInteractButton=Interact
//    menu.townhall.owner.protectExplodeButton=Entity Explode
//    menu.townhall.owner.protectPlayerButton=Protect Player
//    menu.townhall.owner.protectEntityButton=Protect Entity
//    menu.townhall.owner.protectEntryButton=Entry
//    menu.townhall.owner.lockDownButton=Lock Down
//    menu.townhall.owner.protectCreatureSpawnButton=Creature Spawn
//    menu.townhall.owner.protectButtons.lore.enabled=Enabled
//    menu.townhall.owner.protectButtons.lore.disabled=Disabled
}
