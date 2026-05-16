package com.robertx22.mine_and_slash.uncommon.datasaving;

import com.robertx22.mine_and_slash.capability.chunk.ChunkCap;
import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.capability.player.PlayerBackpackData;
import com.robertx22.mine_and_slash.capability.player.PlayerData;
import com.robertx22.mine_and_slash.capability.world.WorldData;
import com.robertx22.mine_and_slash.maps.MapData;
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashAttachments;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class Load {

    /**
     * 엔티티(몬스터/플레이어 포함)의 EntityData를 NeoForge Attachment에서 가져옵니다.
     * 레벨, 스탯, HP, 경험치, 어픽스 등을 포함합니다.
     */
    public static EntityData Unit(Entity entity) {
        if (entity instanceof LivingEntity living) {
            return living.getData(SlashAttachments.ENTITY_DATA.get()).init(living);
        }
        return new EntityData(); // fallback (entity null)
    }

    /**
     * 플레이어의 PlayerData를 NeoForge Attachment에서 가져옵니다.
     * 스킬트리, 스탯포인트, 설정(자동분해 등), 캐릭터 정보 등을 포함합니다.
     */
    public static PlayerData player(Player player) {
        return player.getData(SlashAttachments.PLAYER_DATA.get()).init(player);
    }

    /**
     * 플레이어의 백팩 데이터를 NeoForge Attachment에서 가져옵니다.
     */
    public static PlayerBackpackData backpacks(Player player) {
        return player.getData(SlashAttachments.PLAYER_BACKPACK_DATA.get()).init(player);
    }

    /**
     * 월드 데이터를 가져옵니다.
     */
    public static WorldData worldData(Level l) {
        return WorldData.get(l);
    }

    // todo add connected maps
    public static MapData mapAt(Level l, BlockPos pos) {
        try {
            return WorldUtils.ifMapData(l, pos).get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 청크 데이터를 NeoForge Attachment에서 가져옵니다.
     */
    public static ChunkCap chunkData(LevelChunk c) {
        return c.getData(SlashAttachments.CHUNK_DATA.get()).init(c);
    }

}
