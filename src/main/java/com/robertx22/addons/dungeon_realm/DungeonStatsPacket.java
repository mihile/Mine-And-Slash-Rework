package com.robertx22.addons.dungeon_realm;

import com.robertx22.library_of_exile.main.MyPacket;
import com.robertx22.library_of_exile.packets.ExilePacketContext;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class DungeonStatsPacket extends MyPacket<DungeonStatsPacket> {

    public static DungeonStatsSyncData SYNCED_DATA = new DungeonStatsSyncData();
    public static long LAST_SYNC_TIME = 0;

    public DungeonStatsSyncData data = new DungeonStatsSyncData();

    public DungeonStatsPacket() {
    }

    public DungeonStatsPacket(DungeonStatsSyncData data) {
        this.data = data;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "dungeon_stats");
    }

    @Override
    public void loadFromData(FriendlyByteBuf tag) {
        data = IAutoGson.GSON.fromJson(tag.readUtf(), DungeonStatsSyncData.class);
    }

    @Override
    public void saveToData(FriendlyByteBuf tag) {
        tag.writeUtf(IAutoGson.GSON.toJson(data));
    }

    @Override
    public void onReceived(ExilePacketContext ctx) {
        SYNCED_DATA = data;
        LAST_SYNC_TIME = System.currentTimeMillis();
    }

    @Override
    public MyPacket<DungeonStatsPacket> newInstance() {
        return new DungeonStatsPacket();
    }
}
