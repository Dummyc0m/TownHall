package com.dummyc0m.bukkit.townhall.region;

import com.dummyc0m.bukkit.townhall.Settings;
import com.dummyc0m.bukkit.townhall.core.util.I18N;
import com.dummyc0m.bukkit.townhall.core.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Dummyc0m on 3/14/16.
 */
public class RegionBlock {
    private Region region;
    private volatile ChunkRef chunk;
    private volatile RegionBlockType type;

    public RegionBlock(ChunkRef chunk, RegionBlockType type) {
        this.chunk = chunk;
        this.type = type;
    }

    public boolean isValid() {
        return region != null;
    }

    public ChunkRef getChunk() {
        return chunk;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public boolean isConnected(ChunkRef that) {
        int xDelta = Math.abs(chunk.getX() - that.getX());
        int zDelta = Math.abs(chunk.getZ() - that.getZ());
        return xDelta + zDelta == 1;
    }

    public Location getSpawnLocation() {
        World world = Bukkit.getWorld(region.getWorld().getName());
        int x = chunk.getWorldX();
        if (x > 0) {
            x += MathUtil.random(0, 15);
        } else {
            x -= MathUtil.random(0, 15);
        }
        int z = chunk.getWorldZ();
        if (z > 0) {
            z += MathUtil.random(0, 15);
        } else {
            z -= MathUtil.random(0, 15);
        }
        return new Location(world, x, world.getHighestBlockYAt(x, z) + 1, z);
    }

    /*
        return true to forbid entrance
     */
    public boolean onPlayerEnter(Player player, boolean sameRegion) {
        if (!sameRegion) {
            if (region.isProtectEntry() && !region.containsPlayer(player.getUniqueId())) {
                player.sendMessage(I18N.translateKey("chat.townhall.denyEntry"));
                return true;
            }
            if (region.isLockDown() && !region.containsPlayer(player.getUniqueId())) {
                player.sendMessage(I18N.translateKey("chat.townhall.lockDownEntry"));
                return true;
            }
            RegionPlayer player1 = region.getPlayer(player.getUniqueId());
            if ((type == RegionBlockType.SPECIAL || type == RegionBlockType.CENTER) && player1 != null) {
                player1.setLastRespawnableBlock(chunk);
            }
            player.sendMessage(region.getEnterMessage());
        }
        return false;
    }

    /*
        return true to forbid exit
     */
    public boolean onPlayerDepart(Player player, boolean sameRegion) {
        if (!sameRegion) {
            if (region.isLockDown() && !region.containsPlayer(player.getUniqueId())) {
                player.sendMessage(I18N.translateKey("chat.townhall.lockDownExit"));
                return true;
            }
            player.sendMessage(region.getExitMessage());
        }
        return false;
    }

    //for Perms:
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (region.isProtectInteract() && !region.containsPlayer(event.getPlayer().getUniqueId())) {
            if (Settings.getSettings().isEnableDamagePenalty()) {
                event.getPlayer().damage(Settings.getSettings().getDamagePenalty());
            }
            event.setCancelled(true);
        }
    }

    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (region.isProtectDestroy() && !region.containsPlayer(event.getPlayer().getUniqueId())) {
            if (Settings.getSettings().isEnableDamagePenalty()) {
                event.getPlayer().damage(Settings.getSettings().getDamagePenalty());
            }
            event.setCancelled(true);
        }
    }

    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (region.isProtectBuild() && !region.containsPlayer(event.getPlayer().getUniqueId())) {
            if (Settings.getSettings().isEnableDamagePenalty()) {
                event.getPlayer().damage(Settings.getSettings().getDamagePenalty());
            }
            event.setCancelled(true);
        }
    }

    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();
        if (region.isProtectEntity()) {
            if (damager instanceof Player) {
                if (!region.containsPlayer(damager.getUniqueId())) {
                    if (Settings.getSettings().isEnableDamagePenalty()) {
                        ((Player) damager).damage(Settings.getSettings().getDamagePenalty());
                    }
                    event.setCancelled(true);
                }
            } else if (!(damaged instanceof Player && !region.containsPlayer(damaged.getUniqueId()))) {
                event.setCancelled(true);
            }
        }
    }

    public void onPlayerDamaged(EntityDamageEvent event) {
        if (region.isProtectPlayer() && region.containsPlayer(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    public void onEntityExplode(EntityExplodeEvent event) {
        if (region.isProtectExplode()) {
            event.setCancelled(true);
            Location loc = event.getLocation();
            Bukkit.getWorld(region.getWorld().getName()).createExplosion(loc.getX(), loc.getY(), loc.getZ(), 4f, false, false);
        }
    }

    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (region.isProtectCreatureSpawn()) {
            event.setCancelled(true);
        }
    }

    protected void setCenter() {
        type = RegionBlockType.CENTER;
    }

    protected void setNormal() {
        type = RegionBlockType.NORMAL;
    }

    public RegionBlockType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegionBlock that = (RegionBlock) o;

        if (!chunk.equals(that.chunk)) return false;
        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = chunk.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
