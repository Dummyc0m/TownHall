package com.dummyc0m.bukkit.townhall.region;

import com.dummyc0m.bukkit.townhall.Settings;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dummyc0m on 6/4/15.
 * TownHall Regions
 */
public class RegionManager {
    private static RegionManager manager;
    private Map<UUID, RegionPlayer> namePlayerMap = new ConcurrentHashMap<>();
    private Map<String, RegionWorld> nameWorldMap = new ConcurrentHashMap<>();
    private RegionSQLSource dataSource;

    public RegionManager(Settings settings) throws SQLException {
        dataSource = new RegionSQLSource(settings.getDatabaseType(), settings.getUrl(), settings.getUsername(), settings.getPassword(), settings.getEnabledWorlds(), this);
        dataSource.loadRegionAndPlayers();
        manager = this;
    }

    public static RegionManager getManager() {
        return manager;
    }

    public void addWorld(RegionWorld world) {
        nameWorldMap.put(world.getName(), world);
    }

    public boolean removeWorld(String name) {
        RegionWorld world = nameWorldMap.remove(name);
        return world != null;
    }

    public void addPlayer(RegionPlayer player) {
        namePlayerMap.put(player.getUniqueId(), player);
    }

    public RegionPlayer getRegionPlayer(UUID uuid) {
        return namePlayerMap.get(uuid);
    }

    public boolean hasPlayer(UUID uuid) {
        return namePlayerMap.containsKey(uuid);
    }

    public boolean isPlayerLoaded(UUID uuid) {
        return namePlayerMap.containsKey(uuid);
    }

    public RegionWorld getWorld(String name) {
        return nameWorldMap.get(name);
    }

    public Region getRegion(Location location) {
        RegionWorld world = nameWorldMap.get(location.getWorld().getName());
        if (world != null) {
            return world.getRegion(location);
        }
        return null;
    }

    public void saveConcurrently() throws SQLException {
        Map<String, Collection<Region>> worldRegionsMap = new HashMap<>();
        for (RegionWorld world : nameWorldMap.values()) {
            worldRegionsMap.put(world.getName(), ImmutableList.copyOf(world.getNameRegionMap().values()));
        }
        dataSource.saveRegionAndPlayersConcurrently(ImmutableList.copyOf(namePlayerMap.values()), worldRegionsMap);
    }

    public void onStop() throws SQLException {
        Map<String, Collection<Region>> worldRegionsMap = new HashMap<>();
        for (RegionWorld world : nameWorldMap.values()) {
            worldRegionsMap.put(world.getName(), ImmutableList.copyOf(world.getNameRegionMap().values()));
        }
        dataSource.saveRegionAndPlayers(ImmutableList.copyOf(namePlayerMap.values()), worldRegionsMap);
        dataSource.terminate();
    }

    protected void removeRegion(String world, String name) {
        dataSource.removeRegion(world, name);
    }
}