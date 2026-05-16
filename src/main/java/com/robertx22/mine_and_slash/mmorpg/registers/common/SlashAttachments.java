package com.robertx22.mine_and_slash.mmorpg.registers.common;

import com.robertx22.mine_and_slash.capability.chunk.ChunkCap;
import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.capability.player.PlayerBackpackData;
import com.robertx22.mine_and_slash.capability.player.PlayerData;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class SlashAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SlashRef.MODID);

    /**
     * EntityData - 플레이어 포함 모든 엔티티의 레벨, 스탯, HP, 경험치, 랜덤 어픽스 등을 저장.
     * INBTSerializable을 구현하므로 NeoForge가 자동으로 저장/로드합니다.
     */
    public static final Supplier<AttachmentType<EntityData>> ENTITY_DATA =
            ATTACHMENT_TYPES.register("entity_data",
                    () -> AttachmentType.serializable((Supplier<EntityData>) EntityData::new).copyOnDeath().build());

    /**
     * PlayerData - 플레이어 전용: 스킬트리, 클래스포인트, 스탯포인트, 설정(자동분해 등), 캐릭터 등.
     * copyOnDeath()로 사망 후에도 데이터가 유지됩니다.
     */
    public static final Supplier<AttachmentType<PlayerData>> PLAYER_DATA =
            ATTACHMENT_TYPES.register("player_data",
                    () -> AttachmentType.serializable((Supplier<PlayerData>) PlayerData::new).copyOnDeath().build());

    /**
     * PlayerBackpackData - 플레이어 백팩 인벤토리.
     */
    public static final Supplier<AttachmentType<PlayerBackpackData>> PLAYER_BACKPACK_DATA =
            ATTACHMENT_TYPES.register("player_backpack_data",
                    () -> AttachmentType.serializable((Supplier<PlayerBackpackData>) PlayerBackpackData::new).copyOnDeath().build());

    /**
     * ChunkCap - 청크별 몬스터 생성 여부, 지역 레벨 등.
     */
    public static final Supplier<AttachmentType<ChunkCap>> CHUNK_DATA =
            ATTACHMENT_TYPES.register("chunk_data",
                    () -> AttachmentType.serializable((Supplier<ChunkCap>) ChunkCap::new).build());

    public static void init(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
