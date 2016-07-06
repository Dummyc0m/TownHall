package com.dummyc0m.bukkit.townhall.core.chatinput;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Dummyc0m on 3/7/16.
 */
public class ChatListener implements Listener {
    private final CoreChatTrigger manager;

    public ChatListener(CoreChatTrigger manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (manager.contains(event.getPlayer().getUniqueId())) {
            manager.pop(event.getPlayer().getUniqueId()).accept(event);
        }
    }
}
