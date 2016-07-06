package com.dummyc0m.bukkit.townhall.region;

/**
 * Created by Dummyc0m on 3/17/16.
 */
public enum RegionLevel {
    VILLAGE(16, 1, 1, 8),
    TOWN(64, 4, 9, 32),
    CITY(128, 16, 33, 64),
    METROPOLIS(256, 64, 65, Integer.MAX_VALUE);

    private static final RegionLevel[] values = values();

    private int normalLimit;
    private int specialLimit;
    private int playerRequirement;
    private int playerForNextLevel;

    RegionLevel(int normalLimit, int specialLimit, int playerRequirement, int playerForNextLevel) {
        this.normalLimit = normalLimit;
        this.specialLimit = specialLimit;
        this.playerRequirement = playerRequirement;
        this.playerForNextLevel = playerForNextLevel;
    }

    public int getChunkLimit() {
        return normalLimit + specialLimit;
    }

    public int getNormalLimit() {
        return normalLimit;
    }

    public int getSpecialLimit() {
        return specialLimit;
    }

    public int getNextLevelPlayerRequirement() {
        return playerForNextLevel;
    }

    public int getPlayerRequirement() {
        return playerRequirement;
    }

    public void setValues(int normalLimit, int specialLimit, int playerRequirement, int playerForNextLevel) {
        this.normalLimit = normalLimit;
        this.specialLimit = specialLimit;
        this.playerRequirement = playerRequirement + 1;
        this.playerForNextLevel = playerForNextLevel;
    }

    public RegionLevel getNextLevel() {
        if (values.length > ordinal() + 1) {
            return values[ordinal() + 1];
        }
        return this;
    }

    public RegionLevel getLastLevel() {
        if (ordinal() > 0) {
            return values[ordinal() - 1];
        }
        return this;
    }
}
