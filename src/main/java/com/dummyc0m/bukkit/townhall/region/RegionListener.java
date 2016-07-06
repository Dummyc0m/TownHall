package com.dummyc0m.bukkit.townhall.region;

import com.dummyc0m.bukkit.townhall.TownHallPlugin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Dummyc0m on 4/9/15.
 */
public class RegionListener implements Listener {
    private final RegionManager manager;

    private final Map<UUID, Long> lastProcessed;
    private final Map<UUID, Location> lastLoc;
    private final Map<UUID, RegionBlock> lastIn;
    private final Map<UUID, Region> lastInRegion;

    public RegionListener(RegionManager manager) {
        this.manager = manager;
        this.lastProcessed = new HashMap<>();
        this.lastIn = new HashMap<>();
        this.lastLoc = new HashMap<>();
        this.lastInRegion = new HashMap<>();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("chatPrefix")) {
            List<MetadataValue> metadataValues = player.getMetadata("chatPrefix");
            for (MetadataValue value : metadataValues) {
                if (value.getOwningPlugin() == TownHallPlugin.getInstance()) {
                    event.setFormat(value.asString() + event.getFormat());
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityExplode(EntityExplodeEvent event) {
        RegionWorld regionWorld = manager.getWorld(event.getLocation().getWorld().getName());
        if (regionWorld != null) {
            RegionBlock block = regionWorld.getBlock(event.getLocation());
            if (block != null && block.isValid()) {
                block.onEntityExplode(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        RegionWorld regionWorld;
        if (block != null) {
            regionWorld = manager.getWorld(block.getWorld().getName());
        } else {
            regionWorld = manager.getWorld(event.getPlayer().getWorld().getName());
        }
        if (regionWorld != null) {
            RegionBlock regionBlock;
            if (block == null) {
                regionBlock = regionWorld.getBlock(event.getPlayer().getLocation());
            } else {
                regionBlock = regionWorld.getBlock(block.getLocation());
            }
            if (regionBlock != null) {
                regionBlock.onPlayerInteract(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        RegionWorld regionWorld = manager.getWorld(event.getBlock().getWorld().getName());
        if (regionWorld != null) {
            RegionBlock block = regionWorld.getBlock(event.getBlock().getLocation());
            if (block != null) {
                block.onPlayerBreakBlock(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        RegionWorld regionWorld = manager.getWorld(event.getBlock().getWorld().getName());
        if (regionWorld != null) {
            RegionBlock block = regionWorld.getBlock(event.getBlock().getLocation());
            if (block != null) {
                block.onPlayerPlaceBlock(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        RegionWorld regionWorld = manager.getWorld(event.getEntity().getWorld().getName());
        if (regionWorld != null) {
            RegionBlock block = regionWorld.getBlock(event.getEntity().getLocation());
            if (block != null) {
                block.onEntityDamaged(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            switch (event.getCause()) {
                case SUFFOCATION:
                case VOID:
                case SUICIDE:
                case STARVATION:
                case DRAGON_BREATH:
                case CUSTOM:
                case DROWNING:
                case MAGIC:
                    break;
                default:
                    RegionWorld regionWorld = manager.getWorld(event.getEntity().getWorld().getName());
                    if (regionWorld != null) {
                        RegionBlock block = regionWorld.getBlock(event.getEntity().getLocation());
                        if (block != null) {
                            block.onPlayerDamaged(event);
                        }
                    }
                    break;
            }
        }
//        CONTACT,
//                ENTITY_ATTACK,
//                PROJECTILE,
//                SUFFOCATION,
//                FALL,
//                FIRE,
//                FIRE_TICK,
//                MELTING,
//                LAVA,
//                DROWNING,
//                BLOCK_EXPLOSION,
//                ENTITY_EXPLOSION,
//                VOID,
//                LIGHTNING,
//                SUICIDE,
//                STARVATION,
//                POISON,
//                MAGIC,
//                WITHER,
//                FALLING_BLOCK,
//                THORNS,
//                DRAGON_BREATH,
//                CUSTOM,
//                FLY_INTO_WALL;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        RegionWorld regionWorld = manager.getWorld(event.getLocation().getWorld().getName());
        if (regionWorld != null) {
            RegionBlock block = regionWorld.getBlock(event.getEntity().getLocation());
            if (block != null) {
                block.onCreatureSpawn(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (manager.isPlayerLoaded(uuid)) {
            if (!lastProcessed.containsKey(uuid)) {
                lastProcessed.put(uuid, System.currentTimeMillis());
            }
            long lastProcessTime = lastProcessed.get(uuid);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastProcessTime < 500) {
                return;
            }
            lastProcessed.put(uuid, currentTime);
            if (event.getFrom().getWorld() == event.getTo().getWorld()) {
                if (event.getFrom().distance(event.getTo()) == 0) {
                    return;
                }
            }
            if (calculateMove(player, event.getTo(), event.getFrom())) {
                //event.setCancelled(true);
                if (lastLoc.containsKey(uuid)) {
                    player.teleport(lastLoc.get(uuid), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        switch (event.getCause()) {
            case PLUGIN:
            case UNKNOWN:
                break;
            default:
                event.setCancelled(calculateMove(player, event.getTo(), event.getFrom()));
        }
//        ENDER_PEARL,
//                COMMAND,
//                PLUGIN,
//                NETHER_PORTAL,
//                END_PORTAL,
//                SPECTATE,
//                END_GATEWAY,
//                CHORUS_FRUIT,
//                UNKNOWN;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!manager.hasPlayer(player.getUniqueId())) {
            manager.addPlayer(new RegionPlayer(player.getUniqueId()).setNew());
        }
        calculateMove(player, player.getLocation(), player.getLocation());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        RegionBlock prevBlock = lastIn.get(uuid);
        lastProcessed.remove(uuid);
        if (prevBlock != null && prevBlock.isValid()) {
            if (!prevBlock.onPlayerDepart(player, false)) {
                lastInRegion.remove(uuid);
                lastLoc.remove(uuid);
                lastIn.remove(uuid);
            }
        } else {
            lastInRegion.remove(uuid);
            lastLoc.remove(uuid);
            lastIn.remove(uuid);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        RegionPlayer player = manager.getRegionPlayer(p.getUniqueId());
        RegionBlock prevBlock = lastIn.get(p.getUniqueId());
        if (prevBlock != null && prevBlock.onPlayerDepart(p, false)) {
            event.setRespawnLocation(p.getLocation());
        } else if (p.getBedSpawnLocation() == null && player != null && player.getRegion() != null && player.getLastRespawnableBlock() != null) {
            RegionBlock respawnBlock = player.getRegion().getWorld().getBlock(player.getLastRespawnableBlock());
            if (respawnBlock != null) {
                event.setRespawnLocation(respawnBlock.getSpawnLocation());
            }
        }
    }

    private boolean calculateMove(Player player, Location location, Location from) {
        boolean retVal = false;
        UUID uuid = player.getUniqueId();
        RegionWorld regionWorld = manager.getWorld(location.getWorld().getName());
        if (regionWorld != null) {
            RegionBlock block = regionWorld.getBlock(location);
            Region prevRegion = lastInRegion.get(uuid);
            RegionBlock prevBlock = lastIn.get(uuid);
            if (block == null) {
                if (prevBlock != null && prevBlock.isValid()) {
                    retVal = prevBlock.onPlayerDepart(player, false);
                    if (!retVal) {
                        lastIn.remove(uuid);
                        lastInRegion.remove(uuid);
                    }
                }
            } else if (prevBlock != block) {
                if (block.getRegion() == prevRegion) {
                    if (prevBlock != null && prevBlock.isValid()) {
                        retVal = prevBlock.onPlayerDepart(player, true);
                    }
                    retVal = retVal || block.onPlayerEnter(player, true);
                } else {
                    if (prevBlock != null && prevBlock.isValid()) {
                        retVal = prevBlock.onPlayerDepart(player, false);
                    }
                    retVal = retVal || block.onPlayerEnter(player, false);
                }
                if (!retVal) {
                    lastIn.put(uuid, block);
                    lastInRegion.put(uuid, block.getRegion());
                }
            }
            if (!retVal) {
                lastLoc.put(uuid, from);
            }
        }
        return retVal;
    }
}
