package com.dummyc0m.bukkit.townhall.region;

import com.dummyc0m.bukkit.townhall.core.util.I18N;
import com.google.common.collect.ImmutableList;

import java.util.*;

/**
 * Created by Dummyc0m on 3/14/16.
 */
public class Region extends Observable {
    private volatile List<RegionBlock> blocks;
    private volatile int specialCount;
    private volatile List<RegionPlayer> players;
    private volatile RegionPlayer owner;
    private volatile RegionBlock center;
    private RegionWorld world;
    private volatile String name;
    private volatile String enterMessage;
    private volatile String exitMessage;
    private volatile RegionLevel level;
    //flags
    private volatile boolean protectBuild = true;
    private volatile boolean protectDestroy = true;
    private volatile boolean protectInteract = true;
    private volatile boolean protectExplode = true;
    private volatile boolean protectPlayer = true;
    private volatile boolean protectEntity = true;
    //default false
    private volatile boolean protectEntry; //false
    private volatile boolean lockDown; //false
    //Admin
    private volatile boolean protectCreatureSpawn;
    //Serilization
    private volatile boolean isNew;

    protected Region(String name, RegionPlayer owner, RegionBlock center, RegionWorld world) {
        this.name = name;
        blocks = Collections.synchronizedList(new ArrayList<>());
        blocks.add(center);
        players = Collections.synchronizedList(new ArrayList<>());
        this.owner = owner;
        players.add(owner);
        owner.setRegion(this, true);
        this.center = center;
        center.setCenter();
        center.setRegion(this);
        this.world = world;
        level = RegionLevel.VILLAGE;
        enterMessage = I18N.translateKeyFormat("chat.townhall.enterRegion", name);
        exitMessage = I18N.translateKeyFormat("chat.townhall.exitRegion", name);
    }

    public int getSpecialCount() {
        return specialCount;
    }

    public void setSpecialCount(int specialCount) {
        this.specialCount = specialCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!this.name.equals(name)) {
            String oldName = this.name;
            this.name = name;
            setChanged();
            notifyObservers(new Object[]{NotificationType.NAME_CHANGE, oldName});
        }
    }

    public RegionLevel getLevel() {
        return level;
    }

    public List<RegionBlock> getBlocksSnapshot() {
        return ImmutableList.copyOf(blocks);
    }

    protected List<RegionBlock> getBlocks() {
        return blocks;
    }

    public boolean checkConnected(ChunkRef chunkRef) {
        for (RegionBlock block : blocks) {
            if (block.getType() != RegionBlockType.SPECIAL && block.isConnected(chunkRef)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check limit before adding!!! use checkChunkLimit()
     *
     * @param block
     * @return
     */
    public boolean addBlock(RegionBlock block) {
        if (!world.containsBlock(block.getChunk()) && (block.getType() == RegionBlockType.SPECIAL || checkConnected(block.getChunk()))) {
            if (block.getType() == RegionBlockType.SPECIAL) {
                specialCount++;
            }
            blocks.add(block);
            block.setRegion(this);
            setChanged();
            notifyObservers(new Object[]{NotificationType.BLOCK_ADD, block});
            return true;
        }
        return false;
    }

    protected void addBlockWithoutCheck(RegionBlock block) {
        if (block.getType() == RegionBlockType.SPECIAL) {
            specialCount++;
        }
        blocks.add(block);
        block.setRegion(this);
        setChanged();
        notifyObservers(new Object[]{NotificationType.BLOCK_ADD, block});
    }

    /**
     * from AdminWand does not check level
     *
     * @param blocks
     * @return
     */
    public synchronized boolean addBlocks(List<RegionBlock> blocks) {
        List<RegionBlock> regionBlocks = new ArrayList<>();
        for (RegionBlock block : blocks) {
            if (!regionBlocks.contains(block)) {
                regionBlocks.add(block);
            }
        }
        List<RegionBlock> regionBlocks1 = new ArrayList<>(regionBlocks);
        regionBlocks1.addAll(this.blocks);
        for (RegionBlock outer : regionBlocks1) {
            if (outer.getType() == RegionBlockType.SPECIAL) {
                continue;
            }
            boolean connected = false;
            for (RegionBlock inner : regionBlocks1) {
                if (inner.getType() != RegionBlockType.SPECIAL && outer.isConnected(inner.getChunk())) {
                    connected = true;
                    break;
                }
            }
            if (!connected) {
                return false;
            }
        }
        this.blocks = regionBlocks1;
        setChanged();
        notifyObservers(new Object[]{NotificationType.BLOCKS_ADD, regionBlocks});
        return true;
    }

    public RegionPlayer getOwner() {
        return owner;
    }

    public boolean removeBlock(RegionBlock block) {
        if (!center.equals(block) && blocks.remove(block)) {
            for (RegionBlock remain : blocks) {
                boolean notConnected = true;
                for (RegionBlock remain1 : blocks) {
                    if (remain.isConnected(remain1.getChunk()) || remain.getType() == RegionBlockType.SPECIAL) {
                        notConnected = false;
                        break;
                    }
                }
                if (notConnected) {
                    blocks.add(block);
                    return false;
                }
            }
            if (block.getType() == RegionBlockType.SPECIAL) {
                specialCount--;
            }
            block.setRegion(null);
            setChanged();
            notifyObservers(new Object[]{NotificationType.BLOCK_DEL, block});
            return true;
        }
        return false;
    }

    public RegionBlock getCenter() {
        return center;
    }

    public RegionWorld getWorld() {
        return world;
    }

    public boolean setOwner(RegionPlayer owner) {
        if (players.contains(owner)) {
            this.owner.setRegion(this, false);
            owner.setRegion(this, true);
            this.owner = owner;
            return true;
        }
        return false;
    }

    public synchronized boolean addPlayer(RegionPlayer player) {
        if (!players.contains(player) && player.getRegion() == null && players.add(player)) {
            player.setRegion(this, false);
            player.setLastRespawnableBlock(center.getChunk());
            if (players.size() > level.getNextLevelPlayerRequirement()) {
                level = level.getNextLevel();
            }
            return true;
        }
        return false;
    }

    public boolean removePlayer(RegionPlayer player) {
        if (player.getRegion().equals(this) && players.remove(player)) {
            if (players.size() < level.getPlayerRequirement()) {
                level = level.getLastLevel();
            }
            player.setRegion(null, false);
            player.setLastRespawnableBlock(null);
        }
        return false;
    }

    public boolean containsPlayer(RegionPlayer player) {
        return players.contains(player);
    }

    public boolean containsPlayer(UUID uuid) {
        return players.stream().anyMatch(regionPlayer -> regionPlayer.getUniqueId().equals(uuid));
    }

    public RegionPlayer getPlayer(UUID uuid) {
        Optional<RegionPlayer> player = players.stream().filter(regionPlayer -> regionPlayer.getUniqueId().equals(uuid)).findFirst();
        if (player.isPresent()) {
            return player.get();
        }
        return null;
    }

    public int getPlayerCount() {
        return players.size();
    }

    /**
     * @param type
     * @return true if not reached
     */
    public boolean checkChunkLimit(RegionBlockType type) {
        return type == RegionBlockType.SPECIAL ? specialCount < level.getSpecialLimit() && blocks.size() < level.getChunkLimit() : blocks.size() - specialCount < level.getNormalLimit();
    }

    public void setNew() {
        this.isNew = true;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setSaved() {
        isNew = false;
    }

    public synchronized List<RegionPlayer> getPlayers() {
        return players;
    }

    public String getEnterMessage() {
        return enterMessage;
    }

    public void setEnterMessage(String enterMessage) {
        this.enterMessage = enterMessage;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
    }

    public boolean isProtectBuild() {
        return protectBuild;
    }

    public void setProtectBuild(boolean protectBuild) {
        this.protectBuild = protectBuild;
    }

    public boolean isProtectDestroy() {
        return protectDestroy;
    }

    public void setProtectDestroy(boolean protectDestroy) {
        this.protectDestroy = protectDestroy;
    }

    public boolean isProtectInteract() {
        return protectInteract;
    }

    public void setProtectInteract(boolean protectInteract) {
        this.protectInteract = protectInteract;
    }

    public boolean isProtectExplode() {
        return protectExplode;
    }

    public void setProtectExplode(boolean protectExplode) {
        this.protectExplode = protectExplode;
    }

    public boolean isProtectPlayer() {
        return protectPlayer;
    }

    public void setProtectPlayer(boolean protectPlayer) {
        this.protectPlayer = protectPlayer;
    }

    public boolean isProtectEntity() {
        return protectEntity;
    }

    public void setProtectEntity(boolean protectEntity) {
        this.protectEntity = protectEntity;
    }

    public boolean isProtectEntry() {
        return protectEntry;
    }

    public void setProtectEntry(boolean protectEntry) {
        this.protectEntry = protectEntry;
    }

    public boolean isLockDown() {
        return lockDown;
    }

    public void setLockDown(boolean lockDown) {
        this.lockDown = lockDown;
    }

    public boolean isProtectCreatureSpawn() {
        return protectCreatureSpawn;
    }

    public void setProtectCreatureSpawn(boolean protectCreatureSpawn) {
        this.protectCreatureSpawn = protectCreatureSpawn;
    }

    public void destroy() {
        for (RegionPlayer player : players) {
            player.setRegion(null, false);
            player.setLastRespawnableBlock(null);
        }
        for (RegionBlock block : blocks) {
            block.setRegion(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Region region = (Region) o;

        if (!blocks.equals(region.blocks)) return false;
        if (!center.equals(region.center)) return false;
        return name.equals(region.name);

    }

    @Override
    public int hashCode() {
        int result = blocks.hashCode();
        result = 31 * result + center.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
