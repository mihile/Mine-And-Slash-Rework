package com.robertx22.addons.dungeon_realm;

public class DungeonStatsSyncData {
    public boolean active = false;
    public String rarity = "common";
    public int killCompletion = 0;
    public int lootCompletion = 0;

    public DungeonStatsSyncData() {
    }

    public DungeonStatsSyncData(boolean active, String rarity, int killCompletion, int lootCompletion) {
        this.active = active;
        this.rarity = rarity;
        this.killCompletion = killCompletion;
        this.lootCompletion = lootCompletion;
    }
}
