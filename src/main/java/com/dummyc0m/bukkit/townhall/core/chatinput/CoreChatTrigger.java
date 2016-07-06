package com.dummyc0m.bukkit.townhall.core.chatinput;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by Dummyc0m on 3/7/16.
 */
public class CoreChatTrigger {
    private Map<UUID, Consumer<AsyncPlayerChatEvent>> chatTriggerMap = new ConcurrentHashMap<>();

    public void add(UUID uuid, Consumer<AsyncPlayerChatEvent> trigger) {
        chatTriggerMap.put(uuid, trigger);
    }

    protected Consumer<AsyncPlayerChatEvent> pop(UUID uuid) {
        return chatTriggerMap.remove(uuid);
    }

    public boolean contains(UUID uuid) {
        return chatTriggerMap.containsKey(uuid);
    }
}
