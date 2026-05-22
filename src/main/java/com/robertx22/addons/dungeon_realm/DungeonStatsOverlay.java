package com.robertx22.addons.dungeon_realm;

import com.robertx22.dungeon_realm.main.DungeonMain;
import com.robertx22.dungeon_realm.main.DungeonWords;
import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.localization.TranslationType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class DungeonStatsOverlay {

    public static void render(GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.getDebugOverlay().showDebugScreen()) {
            return;
        }
        if (!mc.level.dimension().location().equals(DungeonMain.DIMENSION_KEY)) {
            return;
        }
        if (System.currentTimeMillis() - DungeonStatsPacket.LAST_SYNC_TIME > 3000) {
            return;
        }

        DungeonStatsSyncData data = DungeonStatsPacket.SYNCED_DATA;
        if (data == null || !data.active) {
            return;
        }

        Component rarity = Component.literal(data.rarity);
        ChatFormatting rarityColor = ChatFormatting.GRAY;
        try {
            var rar = LibDatabase.MapFinishRarity().get(data.rarity);
            rarity = rar.getTranslation(TranslationType.NAME).getTranslatedName();
            rarityColor = rar.textFormatting();
        } catch (Exception ignored) {
        }

        Component kill = DungeonWords.DUNGEON_STATS_KILL_COMPLETION.get()
                .append(": " + clampPercent(data.killCompletion) + "%");
        Component loot = DungeonWords.DUNGEON_STATS_LOOT_COMPLETION.get()
                .append(": " + clampPercent(data.lootCompletion) + "%");

        int width = Math.max(132, Math.max(mc.font.width(kill), mc.font.width(loot)) + 24);
        int height = 42;
        int x = mc.getWindow().getGuiScaledWidth() - width - 8;
        int y = 8;

        gui.fill(x, y, x + width, y + height, 0xAA101010);
        gui.fill(x, y, x + width, y + 1, 0xFF666666);
        gui.fill(x, y + height - 1, x + width, y + height, 0xFF666666);
        gui.fill(x, y, x + 1, y + height, 0xFF666666);
        gui.fill(x + width - 1, y, x + width, y + height, 0xFF666666);

        gui.drawString(mc.font, rarity.copy().withStyle(rarityColor), x + (width - mc.font.width(rarity)) / 2, y + 5, 0xFFFFFF, false);
        gui.drawString(mc.font, kill, x + 8, y + 18, 0xFFFFFF, false);
        gui.drawString(mc.font, loot, x + 8, y + 30, 0xFFFFFF, false);
    }

    private static int clampPercent(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
