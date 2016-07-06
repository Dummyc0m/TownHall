package com.dummyc0m.bukkit.townhall.region;

import com.dummyc0m.bukkit.townhall.TownHallPlugin;
import com.dummyc0m.bukkit.townhall.core.util.DBConnectionFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringEscapeUtils;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dummyc0m on 3/15/16.
 */
public class RegionSQLSource {
    private static final String CREATE_PLAYERS;

    private static final String CREATE_WORLD;
    private static final String GET_REGIONS;
    private static final String INSERT_REGION;
    private static final String SAVE_REGION;
    private static final String REMOVE_REGION;

    private static final String GET_PLAYER;
    private static final String GET_PLAYERS;
    private static final String INSERT_PLAYER;
    private static final String SAVE_PLAYER;

    private static final JsonParser parser = new JsonParser();

    static {
        CREATE_WORLD = "CREATE TABLE IF NOT EXISTS `TABLENAME?` ("
                + "`Id` int NOT NULL AUTO_INCREMENT, "
                + "`Name` varchar(32) NULL, "
                + "`Blocks` text NULL, "
                + "`Players` text NULL, "
                + "`Owner` text NULL, "
                + "`Center` text NULL, "
                + "`EnterMessage` text NULL, "
                + "`ExitMessage` text NULL, "
                + "`Protection` text NULL, "
                + "PRIMARY KEY(`Id`), "
                + "INDEX `name_index` (`Name`)) DEFAULT CHARSET=UTF8;";

        CREATE_PLAYERS = "CREATE TABLE IF NOT EXISTS `PlayerData` ("
                + "`Id` int NOT NULL AUTO_INCREMENT, "
                + "`UniqueId` char(36) NULL, "
                + "`Inbox` text NULL, "
                + "`RespawnBlockX` int NULL, "
                + "`RespawnBlockZ` int NULL, "
                + "`Region` text NULL, "
                + "`IsOwner` tinyint(1) NULL, "
                + "PRIMARY KEY(`Id`), "
                + "INDEX `uuid_index` (`UniqueId`)) DEFAULT CHARSET=UTF8;";

        GET_REGIONS = "SELECT `Name`,`Blocks`,`Owner`,`Center`,`EnterMessage`,`ExitMessage`,`Protection` FROM `TABLENAME?`";
        INSERT_REGION = "INSERT INTO `TABLENAME?` (`Name`) VALUES(?)";
        SAVE_REGION = "UPDATE `TABLENAME?` SET `Blocks` = ?,`Owner` = ?,`Center` = ?,`EnterMessage` = ?,`ExitMessage` = ?,`Protection` = ? " +
                "WHERE `Name` = ?";
        REMOVE_REGION = "DELETE FROM `TABLENAME?` WHERE `Name` = ?";

        GET_PLAYERS = "SELECT `Inbox`,`RespawnBlockX`,`RespawnBlockZ`,`Region`,`IsOwner`,`UniqueId` FROM `PlayerData`";
        GET_PLAYER = "SELECT `Inbox`,`RespawnBlockX`,`RespawnBlockZ`,`Region`,`IsOwner` FROM `PlayerData` WHERE `UniqueId` = ?";
        INSERT_PLAYER = "INSERT INTO `PlayerData` (`UniqueId`) VALUES(?)";
        SAVE_PLAYER = "UPDATE `PlayerData` SET `Inbox` = ?,`RespawnBlockX` = ?,`RespawnBlockZ` = ?,`Region` = ?,`IsOwner` = ? " +
                "WHERE `UniqueId` = ?";
    }

    private DBConnectionFactory connectionFactory;
    private RegionManager manager;
    private List<String> enabledWorlds;
    private ExecutorService service;

    public RegionSQLSource(String type, String url, String username, String password, List<String> enabledWorlds, RegionManager manager) {
        connectionFactory = new DBConnectionFactory(type, url, username, password);
        this.enabledWorlds = enabledWorlds;
        this.manager = manager;
        service = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60000, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        try {
            Connection connection = connectionFactory.create();
            Statement statement = connection.createStatement();
            statement.execute(CREATE_PLAYERS);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadRegionAndPlayers() throws SQLException {
        TownHallPlugin.logger().info("Loading data on main thread, this may take a while...");
        //Load World
        Connection connection = connectionFactory.create();
        PreparedStatement getPlayer = connection.prepareStatement(GET_PLAYER);
        for (String worldName : enabledWorlds) {
            RegionWorld world = new RegionWorld(worldName);
            manager.addWorld(world);
            PreparedStatement createWorld = connection.prepareStatement(CREATE_WORLD.replace("TABLENAME?", StringEscapeUtils.escapeSql(world.getName())));
            createWorld.execute();
            createWorld.close();
            PreparedStatement getRegions = connection.prepareStatement(GET_REGIONS.replace("TABLENAME?", StringEscapeUtils.escapeSql(world.getName())));
            ResultSet resultSet = getRegions.executeQuery();
            while (resultSet.next()) {
                //"SELECT `Name`,`Blocks`,`Owner`,`Center`,`EnterMessage`,`ExitMessage`,`Protection` FROM `?`";
                String name = resultSet.getString("Name");
                String blocks = resultSet.getString("Blocks");
                String owner = resultSet.getString("Owner");
                String center = resultSet.getString("Center");
                String enterMessage = resultSet.getString("EnterMessage");
                String exitMessage = resultSet.getString("ExitMessage");
                String protection = resultSet.getString("Protection");

                JsonArray centerJsonArray = parser.parse(center).getAsJsonArray();
                int centerX = centerJsonArray.get(0).getAsInt();
                int centerZ = centerJsonArray.get(1).getAsInt();
                RegionBlockType centerType = RegionBlockType.valueOf(centerJsonArray.get(2).getAsString());

                getPlayer.setString(1, owner);
                ResultSet ownerResult = getPlayer.executeQuery();
                ownerResult.next();
                RegionPlayer ownerPlayer = parsePlayer(ownerResult, owner);
                manager.addPlayer(ownerPlayer);

                Region region = new Region(name, ownerPlayer, new RegionBlock(new ChunkRef(centerX, centerZ), centerType), world);
                //add to world
                world.addRegion(region);
                region.setEnterMessage(enterMessage);
                region.setExitMessage(exitMessage);
                JsonArray protectionArray = parser.parse(protection).getAsJsonArray();
                region.setProtectBuild(protectionArray.get(0).getAsBoolean());
                region.setProtectDestroy(protectionArray.get(1).getAsBoolean());
                region.setProtectInteract(protectionArray.get(2).getAsBoolean());
                region.setProtectExplode(protectionArray.get(3).getAsBoolean());
                region.setProtectPlayer(protectionArray.get(4).getAsBoolean());
                region.setProtectEntity(protectionArray.get(5).getAsBoolean());
                region.setProtectEntry(protectionArray.get(6).getAsBoolean());
                region.setLockDown(protectionArray.get(7).getAsBoolean());
                region.setProtectCreatureSpawn(protectionArray.get(8).getAsBoolean());
                JsonArray blockJsonArray = parser.parse(blocks).getAsJsonArray();
                for (JsonElement jsonElement : blockJsonArray) {
                    JsonArray blockArray = jsonElement.getAsJsonArray();
                    int blockX = blockArray.get(0).getAsInt();
                    int blockZ = blockArray.get(1).getAsInt();
                    if (blockX != centerX || blockZ != centerZ) {
                        RegionBlockType type = RegionBlockType.valueOf(blockArray.get(2).getAsString());
                        region.addBlockWithoutCheck(new RegionBlock(new ChunkRef(blockX, blockZ), type));
                    }
                }
            }
            getRegions.close();
        }

        //Load Players
        PreparedStatement getPlayers = connection.prepareStatement(GET_PLAYERS);
        //"SELECT `Inbox`,`RespawnBlockX`,`RespawnBlockZ`,`Region`,`IsOwner` FROM `PlayerData`";
        ResultSet players = getPlayers.executeQuery();
        while (players.next()) {
            String uuid = players.getString("UniqueId");
            if (!manager.hasPlayer(UUID.fromString(uuid))) {
                manager.addPlayer(parsePlayer(players, uuid));
            }
        }
        getPlayers.close();
        connection.close();
    }

    public void saveRegionAndPlayersConcurrently(Collection<RegionPlayer> players, Map<String, Collection<Region>> worldRegionMap) {
        service.submit(() -> {
            try {
                saveRegionAndPlayers(players, worldRegionMap);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    //Immutable List call concurrently!
    public void saveRegionAndPlayers(Collection<RegionPlayer> players, Map<String, Collection<Region>> worldRegionMap) throws SQLException {
        TownHallPlugin.logger().info("Saving data...");
        Connection connection = connectionFactory.create();
        PreparedStatement savePlayer = connection.prepareStatement(SAVE_PLAYER);
        PreparedStatement insertPlayer = connection.prepareStatement(INSERT_PLAYER);
        //SAVE_PLAYER = "UPDATE `PlayerData` SET `Inbox` = ?,`RespawnBlockX` = ?,`RespawnBlockZ` = ?,`Region` = ?,`IsOwner` = ? WHERE `UniqueId` = ?";
        for (RegionPlayer player : players) {
            if (player.isNew()) {
                insertPlayer.setString(1, player.getUniqueId().toString());
                insertPlayer.execute();
                player.setSaved();
            }
            JsonArray inboxArray = new JsonArray();
            for (PlayerMessage message : player.getInboxSnapshot()) {
                JsonArray messageArray = new JsonArray();
                messageArray.add(message.getFrom().toString());
                messageArray.add(message.getTitle());
                messageArray.add(message.getMessage());
                messageArray.add(message.isApplication());
                inboxArray.add(messageArray);
            }
            savePlayer.setString(1, inboxArray.toString());
            ChunkRef respawn = player.getLastRespawnableBlock();
            if (respawn == null) {
                savePlayer.setInt(2, Integer.MIN_VALUE);
                savePlayer.setInt(3, Integer.MIN_VALUE);
            } else {
                savePlayer.setInt(2, respawn.getX());
                savePlayer.setInt(3, respawn.getZ());
            }
            Region region = player.getRegion();
            if (region == null) {
                savePlayer.setString(4, new JsonArray().toString());
            } else {
                JsonArray regionArray = new JsonArray();
                regionArray.add(region.getWorld().getName());
                regionArray.add(region.getName());
                savePlayer.setString(4, regionArray.toString());
            }
            savePlayer.setBoolean(5, player.isOwner());
            savePlayer.setString(6, player.getUniqueId().toString());
            savePlayer.execute();
        }
        insertPlayer.close();
        savePlayer.close();
//        GET_REGIONS = "SELECT `Name`,`Blocks`,`Owner`,`Center`,`EnterMessage`,`ExitMessage`,`Protection` FROM `TABLENAME?`";
//        INSERT_REGION = "INSERT INTO `TABLENAME?` (`Name`) VALUES(?)";
//        SAVE_REGION = "UPDATE `TABLENAME?` SET `Blocks` = ?,`Owner` = ?,`Center` = ?,`EnterMessage` = ?,`ExitMessage` = ?,`Protection` = ? " +
//                "WHERE `Name` = ?";
//        REMOVE_REGION = "DELETE FROM `TABLENAME?` WHERE `Name` = ?";
        //.replace("TABLENAME?", StringEscapeUtils.escapeSql(world.getName()))
        //"UPDATE `?` SET `Blocks` = ?,`Owner` = ?,`Center` = ?,`EnterMessage` = ?,`ExitMessage` = ?,`Protection` = ? WHERE `Name` = ?";
        for (Map.Entry<String, Collection<Region>> entry : worldRegionMap.entrySet()) {
            PreparedStatement saveRegion = connection.prepareStatement(SAVE_REGION.replace("TABLENAME?", StringEscapeUtils.escapeSql(entry.getKey())));
            PreparedStatement insertRegion = connection.prepareStatement(INSERT_REGION.replace("TABLENAME?", StringEscapeUtils.escapeSql(entry.getKey())));

            for (Region region : entry.getValue()) {
                if (region.isNew()) {
                    insertRegion.setString(1, region.getName());
                    insertRegion.execute();
                    region.setSaved();
                }
                JsonArray blocksArray = new JsonArray();
                for (RegionBlock block : region.getBlocksSnapshot()) {
                    JsonArray blockArray = new JsonArray();
                    ChunkRef chunkRef = block.getChunk();
                    blockArray.add(chunkRef.getX());
                    blockArray.add(chunkRef.getZ());
                    blockArray.add(block.getType().name());
                    blocksArray.add(blockArray);
                }
                saveRegion.setString(1, blocksArray.toString());
                saveRegion.setString(2, region.getOwner().getUniqueId().toString());
                RegionBlock center = region.getCenter();
                JsonArray blockArray = new JsonArray();
                ChunkRef chunkRef = center.getChunk();
                blockArray.add(chunkRef.getX());
                blockArray.add(chunkRef.getZ());
                blockArray.add(center.getType().name());
                saveRegion.setString(3, blockArray.toString());
                saveRegion.setString(4, region.getEnterMessage());
                saveRegion.setString(5, region.getExitMessage());
                JsonArray protection = new JsonArray();
                protection.add(region.isProtectBuild());
                protection.add(region.isProtectDestroy());
                protection.add(region.isProtectInteract());
                protection.add(region.isProtectExplode());
                protection.add(region.isProtectPlayer());
                protection.add(region.isProtectEntity());
                protection.add(region.isProtectEntry());
                protection.add(region.isLockDown());
                protection.add(region.isProtectCreatureSpawn());
                saveRegion.setString(6, protection.toString());
                saveRegion.setString(7, region.getName());
                saveRegion.execute();
            }
            insertRegion.close();
            saveRegion.close();
        }
        connection.close();
    }

    //helper
    private RegionPlayer parsePlayer(ResultSet resultSet, String uuid) throws SQLException {
        RegionPlayer regionPlayer = new RegionPlayer(UUID.fromString(uuid));
        //"SELECT `Inbox`,`RespawnBlockX`,`RespawnBlockZ`,`Region`,`IsOwner` FROM `PlayerData` WHERE `UniqueId` = ?";

        JsonArray inboxArray = parser.parse(resultSet.getString("Inbox")).getAsJsonArray();
        for (JsonElement jsonElement : inboxArray) {
            JsonArray messageArray = jsonElement.getAsJsonArray();
            regionPlayer.getInbox().add(new PlayerMessage(UUID.fromString(messageArray.get(0).getAsString()),
                    messageArray.get(1).getAsString(), messageArray.get(2).getAsString(), messageArray.get(3).getAsBoolean()));
        }
        if (!resultSet.getBoolean("IsOwner")) {
            JsonArray regionArray = parser.parse(resultSet.getString("Region")).getAsJsonArray();
            if (regionArray.size() > 1) {
                RegionWorld world = manager.getWorld(regionArray.get(0).getAsString());
                if (world != null) {
                    Region region = world.getRegion(regionArray.get(1).getAsString());
                    if (region != null) {
                        region.addPlayer(regionPlayer);
                    }
                }
            }
        }
        int respawnX = resultSet.getInt("RespawnBlockX");
        int respawnZ = resultSet.getInt("RespawnBlockZ");
        if (respawnX != Integer.MIN_VALUE && respawnZ != Integer.MIN_VALUE) {
            ChunkRef respawn = new ChunkRef(respawnX, respawnZ);
            regionPlayer.setLastRespawnableBlock(respawn);
        }
        return regionPlayer;
    }

    //***call when remove region***
    public void removeRegion(String world, String region) {
        service.submit(() -> {
            Connection connection = connectionFactory.create();
            try {
                PreparedStatement deleteRegion = connection.prepareStatement(REMOVE_REGION.replace("TABLENAME?", StringEscapeUtils.escapeSql(world)));
                deleteRegion.setString(1, region);
                deleteRegion.execute();
                connection.close();
            } catch (SQLException e) {
                TownHallPlugin.logger().info("Failed to delete Region " + region);
                e.printStackTrace();
            }
        });
    }

    public void terminate() {
        TownHallPlugin.logger().info("Terminating workers, this may take up to a minute...");
        try {
            service.awaitTermination(60000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
