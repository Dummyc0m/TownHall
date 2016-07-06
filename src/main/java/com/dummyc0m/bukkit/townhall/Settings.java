package com.dummyc0m.bukkit.townhall;

import com.dummyc0m.bukkit.townhall.core.util.DBConnectionFactory;
import com.dummyc0m.bukkit.townhall.region.RegionBlockType;
import com.dummyc0m.bukkit.townhall.region.RegionLevel;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dummyc0m on 3/9/16.
 */
public class Settings {
    private static final int SETTINGS_VERSION = 1;
    private static Settings settings;
    private List<String> enabledWorlds;
    private String locale;
    private double damagePenalty;
    private transient boolean enableDamagePenalty;
    private int centerPrice;
    private int normalPrice;
    private int specialPrice;

    //    private final int normalLimit;
//    private final int specialLimit;
//    private final int playerForNextLevel;
    //village
    private int villageNormalLimit;
    private int villageSpecialLimit;
    //town
    private int townNormalLimit;
    private int townSpecialLimit;
    private int townPlayerRequirement;
    //city
    private int cityNormalLimit;
    private int citySpecialLimit;
    private int cityPlayerRequirement;
    //metropolis
    private int metropolisNormalLimit;
    private int metropolisSpecialLimit;
    private int metropolisPlayerRequirement;

    private String databaseTypeMysqlOrSqliteEtc;
    private String url;
    private String username;
    private String password;

    private int version;

    public Settings() {
        enabledWorlds = new ArrayList<>(Collections.singletonList("world"));
        locale = "en_US";
        damagePenalty = 1;

        centerPrice = 1024;
        normalPrice = 256;
        specialPrice = 768;

        //village
        villageNormalLimit = 16;
        villageSpecialLimit = 4;
        //town
        townNormalLimit = 64;
        townSpecialLimit = 8;
        townPlayerRequirement = 8;
        //city
        cityNormalLimit = 128;
        citySpecialLimit = 16;
        cityPlayerRequirement = 32;
        //metropolis
        metropolisNormalLimit = 256;
        metropolisSpecialLimit = 32;
        metropolisPlayerRequirement = 64;

        databaseTypeMysqlOrSqliteEtc = "mysql";
        url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8";
        username = "root";
        password = "root";

        version = SETTINGS_VERSION;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static void setSettings(Settings settings) {
        Settings.settings = settings;
    }

    public String getLocale() {
        return locale;
    }

    public double getDamagePenalty() {
        return damagePenalty;
    }

    public boolean isEnableDamagePenalty() {
        return enableDamagePenalty;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabaseType() {
        return databaseTypeMysqlOrSqliteEtc;
    }

    public List<String> getEnabledWorlds() {
        return enabledWorlds;
    }

    public void setRegionValues() {
        RegionLevel.VILLAGE.setValues(villageNormalLimit, villageSpecialLimit, 0, townPlayerRequirement);
        RegionLevel.TOWN.setValues(townNormalLimit, townSpecialLimit, townPlayerRequirement, cityPlayerRequirement);
        RegionLevel.CITY.setValues(cityNormalLimit, citySpecialLimit, cityPlayerRequirement, metropolisPlayerRequirement);
        RegionLevel.METROPOLIS.setValues(metropolisNormalLimit, metropolisSpecialLimit, metropolisPlayerRequirement, Integer.MAX_VALUE);

        RegionBlockType.CENTER.setChunkPrice(centerPrice);
        RegionBlockType.NORMAL.setChunkPrice(normalPrice);
        RegionBlockType.SPECIAL.setChunkPrice(specialPrice);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean verify() {
        if (damagePenalty > 0) {
            enableDamagePenalty = true;
        }

        if (!"en_US".equals(locale)) {
            File file = new File(TownHallPlugin.getInstance().getDataFolder(), locale + ".lang");
            if (!file.exists()) {
                TownHallPlugin.logger().severe("Localization file does not exist");
            }
        }

        if (enabledWorlds.size() < 1) {
            TownHallPlugin.logger().severe("None of the worlds are enabled");
            return false;
        }
        for (String world : enabledWorlds) {
            if (Bukkit.getWorld(world) == null) {
                TownHallPlugin.logger().severe("Invalid world");
                return false;
            }
        }

        if (normalPrice < 0 || specialPrice < 0) {
            TownHallPlugin.logger().severe("Price is less than 0");
            return false;
        }

        DBConnectionFactory testFactory = new DBConnectionFactory(databaseTypeMysqlOrSqliteEtc, url, username, password);
        if (testFactory.create() == null) {
            TownHallPlugin.logger().severe("Cannot connect to database");
            return false;
        }

        if (!(version == SETTINGS_VERSION)) {
            if (databaseTypeMysqlOrSqliteEtc.equalsIgnoreCase("mysql")) {
                String conversionUTF8 = "ALTER TABLE `TABLENAME?` CONVERT TO CHARACTER SET utf8;";
                TownHallPlugin.logger().warning("Converting pre-version database");
                Connection connection = testFactory.create();
                try {
                    for (String world : enabledWorlds) {
                        PreparedStatement ps = connection.prepareStatement(conversionUTF8.replace("TABLENAME?", StringEscapeUtils.escapeSql(world)));
                        ps.execute();
                    }
                    PreparedStatement ps = connection.prepareStatement(conversionUTF8.replace("TABLENAME?", StringEscapeUtils.escapeSql("PlayerData")));
                    ps.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                TownHallPlugin.logger().warning("Conversion complete");
                version = SETTINGS_VERSION;
            }
        }
        return true;
    }
}