package com.dummyc0m.bukkit.townhall.region;

import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dummyc0m on 3/14/16.
 */
public class RegionWorld implements Observer {
    private Map<String, Region> nameRegionMap = new ConcurrentHashMap<>();
    private Map<ChunkRef, RegionBlock> chunkRegionBlockMap = new ConcurrentHashMap<>();
    private String name;

    public RegionWorld(String name) {
        this.name = name;
    }

    public boolean containsRegion(String name) {
        return nameRegionMap.containsKey(name);
    }

    public boolean addRegion(Region region) {
        if (!containsRegion(region.getName())) {
            nameRegionMap.put(region.getName(), region);
            for (RegionBlock block : region.getBlocks()) {
                chunkRegionBlockMap.put(block.getChunk(), block);
            }
            region.addObserver(this);
            return true;
        }
        return false;
    }

    public boolean removeRegion(String name) {
        Region region = nameRegionMap.remove(name);
        if (region != null) {
            for (RegionBlock block : region.getBlocks()) {
                chunkRegionBlockMap.remove(block.getChunk());
            }
            region.destroy();
            region.deleteObserver(this);
            RegionManager.getManager().removeRegion(this.name, name);
            return true;
        }
        return false;
    }

    public Region getRegion(String name) {
        return nameRegionMap.get(name);
    }

    public Region getRegion(Location location) {
        RegionBlock block = getBlock(location);
        if (block != null) {
            return block.getRegion();
        }
        return null;
    }

    public boolean removeRegion(Location loc) {
        RegionBlock block = getBlock(loc);
        return block != null && removeRegion(block.getRegion().getName());
    }

    public RegionBlock getBlock(Location loc) {
        return chunkRegionBlockMap.get(new ChunkRef(loc));
    }

    public RegionBlock getBlock(ChunkRef chunkRef) {
        return chunkRegionBlockMap.get(chunkRef);
    }

    public boolean containsBlock(ChunkRef chunk) {
        return chunkRegionBlockMap.containsKey(chunk);
    }

    public String getName() {
        return name;
    }

    protected Map<String, Region> getNameRegionMap() {
        return nameRegionMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(Observable o, Object arg) {
        Region region = ((Region) o);
        NotificationType notificationType = ((NotificationType) ((Object[]) arg)[0]);
        switch (notificationType) {
            case NAME_CHANGE:
                String oldName = (String) ((Object[]) arg)[1];
                nameRegionMap.put(region.getName(), region);
                nameRegionMap.remove(oldName);
                RegionManager.getManager().removeRegion(name, oldName);
                region.setNew();
            case BLOCK_ADD:
                RegionBlock addBlock = (RegionBlock) ((Object[]) arg)[1];
                chunkRegionBlockMap.put(addBlock.getChunk(), addBlock);
                break;
            case BLOCK_DEL:
                RegionBlock delBlock = (RegionBlock) ((Object[]) arg)[1];
                chunkRegionBlockMap.remove(delBlock.getChunk());
                break;
            case BLOCKS_ADD:
                List<RegionBlock> addBlocks = (List<RegionBlock>) ((Object[]) arg)[1];
                addBlocks.forEach(add -> chunkRegionBlockMap.put(add.getChunk(), add));
                break;
        }
    }
}
