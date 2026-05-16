package com.robertx22.mine_and_slash.mmorpg.registers.common;

import com.robertx22.library_of_exile.components.PlayerCapabilities;
import com.robertx22.mine_and_slash.capability.chunk.ChunkCap;
import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.capability.player.PlayerBackpackData;
import com.robertx22.mine_and_slash.capability.player.PlayerData;
import com.robertx22.mine_and_slash.mmorpg.ForgeEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * SlashCapabilities - NeoForge 1.21.1 Data Attachment 등록 및 이벤트 처리
 *
 * PlayerData, PlayerBackpackData, EntityData, ChunkCap 모두 INBTSerializable을 구현하고
 * AttachmentType.serializable()로 등록되어 있으므로, NeoForge가 자동으로 NBT 저장/로드를 처리합니다.
 *
 * 여기서는 추가로 필요한 런타임 초기화(entity 레퍼런스 주입)만 처리합니다.
 */
public class SlashCapabilities {

    public static void register() {

        /**
         * 플레이어 로그인 시 Attachment에 entity 레퍼런스 주입.
         * Attachment는 NBT에서 자동으로 로드되지만, transient 필드인 player/entity 참조는
         * 별도로 주입해야 합니다.
         */
        ForgeEvents.registerForgeEvent(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            try {
                if (event.getEntity() instanceof ServerPlayer sp) {
                    sp.getData(SlashAttachments.ENTITY_DATA.get()).init(sp);
                    sp.getData(SlashAttachments.PLAYER_DATA.get()).init(sp);
                    sp.getData(SlashAttachments.PLAYER_BACKPACK_DATA.get()).init(sp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        /**
         * 플레이어 사망/차원이동 후 Clone 이벤트 시 entity 레퍼런스 주입.
         * copyOnDeath()로 PlayerData/PlayerBackpackData의 NBT는 자동 복사되지만,
         * transient player 참조는 새 플레이어 인스턴스로 다시 주입해야 합니다.
         */
        ForgeEvents.registerForgeEvent(PlayerEvent.Clone.class, event -> {
            try {
                if (event.getEntity() instanceof ServerPlayer sp) {
                    sp.getData(SlashAttachments.ENTITY_DATA.get()).init(sp);
                    sp.getData(SlashAttachments.PLAYER_DATA.get()).init(sp);
                    sp.getData(SlashAttachments.PLAYER_BACKPACK_DATA.get()).init(sp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}
