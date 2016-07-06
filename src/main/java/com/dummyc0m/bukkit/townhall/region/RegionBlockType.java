package com.dummyc0m.bukkit.townhall.region;

/**
 * Created by Dummyc0m on 3/14/16.
 */
public enum RegionBlockType {
    CENTER(1024),
    NORMAL(256),
    SPECIAL(768);

    private int chunkPrice;

    RegionBlockType(int chunkPrice) {
        this.chunkPrice = chunkPrice;
    }

    public int getChunkPrice() {
        return chunkPrice;
    }

    public void setChunkPrice(int chunkPrice) {
        this.chunkPrice = chunkPrice;
    }
}
