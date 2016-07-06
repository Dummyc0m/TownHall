package com.dummyc0m.bukkit.townhall.core.item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * com.dummyc0m.bukkit.townhall.inventory
 * Created by Dummyc0m on 3/15/15.
 */
public class ItemListener implements Listener {

    private final CoreItem manager;

    public ItemListener(JavaPlugin plugin, CoreItem manager) {
        this.manager = manager;
//        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
//        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS) {
//            @Override
//            public void onPacketSending(PacketEvent event) {
//                if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
//                    PacketContainer packet = event.getPacket().deepClone();
//                    StructureModifier<ItemStack> sm = packet.getItemModifier();
//                    for (int i = 0; i < sm.size(); i++) {
//                        if (sm.getValues().get(i) != null) {
//                            modifyItemStack(sm.getValues().get(i));
//                        }
//                    }
//                    event.setPacket(packet);
//                } else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
//                    PacketContainer packet = event.getPacket().deepClone();
//                    StructureModifier<ItemStack[]> sm = packet.getItemArrayModifier();
//                    for (int i = 0; i < sm.size(); i++) {
//                        for (int j = 0; j < sm.getValues().get(i).length; j++) {
//                            if (sm.getValues().get(i)[j] != null) {
//                                modifyItemStack(sm.getValues().get(i)[j]);
//                            }
//                        }
//                    }
//                    event.setPacket(packet);
//                }
//
//            }
//        });
    }
//
//    private void modifyItemStack(ItemStack itemStack) {
//        ItemMeta itemMeta = itemStack.getItemMeta();
//        if (itemMeta.hasLore()) {
//            List<String> lore = itemMeta.getLore();
//            Iterator<String> iterator = lore.iterator();
//            while (iterator.hasNext()) {
//                String s = iterator.next();
//                if (s.startsWith(manager.getHideFlag())) {
//                    iterator.remove();
//                }
//            }
//            itemMeta.setLore(lore);
//        }
//        itemStack.setItemMeta(itemMeta);
//    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) return;
        List<String> lore = itemStack.getItemMeta().getLore();
        ACItem item = manager.getItem(lore.get(lore.size() - 1));
        if (item != null) {
            event.setCancelled(item.onInteract(event.getPlayer(), itemStack, event.getAction(), event.getClickedBlock(), event.getBlockFace()));
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) return;
        List<String> lore = itemStack.getItemMeta().getLore();
        ACItem item = manager.getItem(lore.get(lore.size() - 1));
        if (item != null) {
            event.setCancelled(item.onEntityInteract(event.getPlayer(), itemStack, event.getRightClicked(), event.getClickedPosition()));
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) return;
        List<String> lore = itemStack.getItemMeta().getLore();
        ACItem item = manager.getItem(lore.get(lore.size() - 1));
        if (item != null) {
            event.setCancelled(item.onPickUp(event.getPlayer(), itemStack, event.getRemaining()));
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) return;
        List<String> lore = itemStack.getItemMeta().getLore();
        ACItem item = manager.getItem(lore.get(lore.size() - 1));
        if (item != null) {
            event.setCancelled(item.onDrop(event.getPlayer(), itemStack));
        }
    }

    @EventHandler
    public void onPlayerSelectItem(PlayerItemHeldEvent event) {
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) return;
        List<String> lore = itemStack.getItemMeta().getLore();
        ACItem item = manager.getItem(lore.get(lore.size() - 1));
        if (item != null) {
            event.setCancelled(item.onSelected(event.getPlayer(), itemStack, event.getPreviousSlot(), event.getNewSlot()));
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) return;
        List<String> lore = itemStack.getItemMeta().getLore();
        ACItem item = manager.getItem(lore.get(lore.size() - 1));
        if (item != null) {
            event.setCancelled(item.onConsume(event.getPlayer(), itemStack));
        }
    }

    @EventHandler
    public void onPlayerBreakItem(PlayerItemBreakEvent event) {
        ItemStack itemStack = event.getBrokenItem();
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) return;
        List<String> lore = itemStack.getItemMeta().getLore();
        ACItem item = manager.getItem(lore.get(lore.size() - 1));
        if (item != null) {
            item.onBreak(event.getPlayer(), itemStack);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) return;
        List<String> lore = itemStack.getItemMeta().getLore();
        if (lore == null) return;
        ACItem item = manager.getItem(lore.get(lore.size() - 1));
        if (item != null) {
            event.setCancelled(item.onInventoryInteract(event.getWhoClicked(), event.getSlotType(), event.getSlot(), event.getClick(), itemStack));
        }
    }
}
