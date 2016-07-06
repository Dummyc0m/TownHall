package com.dummyc0m.bukkit.townhall.region;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dummyc0m on 8/26/15.
 */
public final class ChunkRef {
    private final int x;
    private final int z;

    public ChunkRef(Location loc) {
        this.x = getChunkCoords(loc.getBlockX());
        this.z = getChunkCoords(loc.getBlockZ());
    }

    public ChunkRef(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static int getChunkCoords(final int val) {
        return val >> 4;
    }

    public static int getWorldCoords(final int val) {
        return val << 4;
    }

    public static List<ChunkRef> getChunkRefs(int x1, int z1, int x2, int z2) {
        List<ChunkRef> ret = new ArrayList<>();
        int chunkX1 = getChunkCoords(Math.min(x1, x2));
        int chunkZ1 = getChunkCoords(Math.min(z1, z2));
        int chunkX2 = getChunkCoords(Math.max(x1, x2)) + 1;
        int chunkZ2 = getChunkCoords(Math.max(z1, z2)) + 1;
        for (int i = chunkX1; i < chunkX2; i++) {
            for (int j = chunkZ1; i < chunkZ2; j++) {
                ret.add(new ChunkRef(i, j));
            }
        }
        return ret;
    }

    public int getX() {
        return x;
    }

    public int getWorldX() {
        return getWorldCoords(x);
    }

    public int getZ() {
        return z;
    }

    public int getWorldZ() {
        return getWorldCoords(z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChunkRef chunkRef = (ChunkRef) o;

        return x == chunkRef.x && z == chunkRef.z;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "ChunkRef{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}
