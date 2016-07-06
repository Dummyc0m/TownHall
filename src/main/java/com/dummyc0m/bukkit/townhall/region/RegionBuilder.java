package com.dummyc0m.bukkit.townhall.region;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Created by Dummyc0m on 3/17/16.
 */
public class RegionBuilder {
    private final RegionManager manager = RegionManager.getManager();
    private String name;
    private RegionPlayer owner;
    private RegionBlock center;
    private RegionWorld world;

    public RegionBuilder(String world) {
        this.world = manager.getWorld(world);
    }

    public boolean setName(String name) {
        if (world != null && !world.containsRegion(name)) {
            this.name = name;
            return true;
        }
        return false;
    }

    public boolean setOwner(UUID owner) {
        RegionPlayer player = manager.getRegionPlayer(owner);
        if (player.getRegion() == null) {
            this.owner = player;
            return true;
        }
        return false;
    }

    public boolean setCenter(int x, int z) {
        ChunkRef chunkRef = new ChunkRef(ChunkRef.getChunkCoords(x), ChunkRef.getChunkCoords(z));
        if (world != null && !world.containsBlock(chunkRef)) {
            this.center = new RegionBlock(chunkRef, RegionBlockType.CENTER);
            return true;
        }
        return false;
    }

    public boolean setCenter(Location location) {
        return setCenter(location.getBlockX(), location.getBlockZ());
    }

    /**
     * keep the database clean!
     *
     * @return null if unsuccessful
     */
    public Region build() {
        Region region = null;
        if (world != null && name != null && owner != null && center != null) {
            region = new Region(name, owner, center, world);
            if (!world.addRegion(region)) {
                owner.setRegion(null, false);
                return null;
            }
            region.setNew();
        }
        return region;
    }
}
