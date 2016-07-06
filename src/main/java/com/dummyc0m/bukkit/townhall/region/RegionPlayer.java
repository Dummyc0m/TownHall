package com.dummyc0m.bukkit.townhall.region;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dummyc0m on 3/14/16.
 */
public class RegionPlayer {
    private volatile UUID uuid;
    private volatile List<PlayerMessage> inbox;
    private volatile ChunkRef lastRespawnableBlock;
    private volatile Region region;
    private volatile boolean isOwner;
    private volatile boolean isNew;

    public RegionPlayer(UUID uuid) {
        this.uuid = uuid;
        this.inbox = Collections.synchronizedList(new ArrayList<>());
    }

    public List<PlayerMessage> getInbox() {
        return inbox;
    }

    public synchronized List<PlayerMessage> getInboxSnapshot() {
        return ImmutableList.copyOf(inbox);
    }

    protected void setRegion(Region region, boolean isOwner) {
        this.region = region;
        this.isOwner = isOwner;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public Region getRegion() {
        return region;
    }

    public ChunkRef getLastRespawnableBlock() {
        return lastRespawnableBlock;
    }

    public void setLastRespawnableBlock(ChunkRef lastRespawnableBlock) {
        this.lastRespawnableBlock = lastRespawnableBlock;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public boolean isNew() {
        return isNew;
    }

    public RegionPlayer setNew() {
        isNew = true;
        return this;
    }

    public void setSaved() {
        isNew = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegionPlayer that = (RegionPlayer) o;

        return uuid.equals(that.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
