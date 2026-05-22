package com.robertx22.mine_and_slash.mmorpg.event_registers;

import com.robertx22.addons.dungeon_realm.DungeonStatsOverlay;
import com.robertx22.mine_and_slash.a_libraries.dmg_number_particle.DamageParticleRenderer;
import com.robertx22.mine_and_slash.config.forge.ClientConfigs;
import com.robertx22.mine_and_slash.config.forge.overlay.OverlayType;
import com.robertx22.mine_and_slash.gui.overlays.EffectsOverlay;
import com.robertx22.mine_and_slash.gui.overlays.bar_overlays.types.RPGGuiOverlay;
import com.robertx22.mine_and_slash.gui.overlays.spell_cast_bar.SpellCastBarOverlay;
import com.robertx22.mine_and_slash.gui.overlays.spell_hotbar.SpellHotbarOverlay;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class GuiOverlays {

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CHAT, ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "spell_hotbar"), (guiGraphics, deltaTracker) -> {
            if (ClientConfigs.CLIENT.SPELL_HOTBAR_OVERLAY_TYPE.get() == ClientConfigs.HorizontalOrVertical.HORIZONTAL) {
                if (ClientConfigs.getConfig().shouldRenderOverlay(OverlayType.SPELL_HOTBAR_HORIZONTAL)) {
                    new SpellHotbarOverlay().onHudRender(guiGraphics, ClientConfigs.getConfig().getOverlayConfig(OverlayType.SPELL_HOTBAR_HORIZONTAL), OverlayType.SPELL_HOTBAR_HORIZONTAL);
                }
            } else {
                if (ClientConfigs.getConfig().shouldRenderOverlay(OverlayType.SPELL_HOTBAR_VERTICAL)) {
                    new SpellHotbarOverlay().onHudRender(guiGraphics, ClientConfigs.getConfig().getOverlayConfig(OverlayType.SPELL_HOTBAR_VERTICAL), OverlayType.SPELL_HOTBAR_VERTICAL);
                }
            }
        });

        event.registerAbove(VanillaGuiLayers.CHAT, ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "cast_bar"), (guiGraphics, deltaTracker) -> {
            if (ClientConfigs.getConfig().shouldRenderOverlay(OverlayType.SPELL_CAST_BAR)) {
                new SpellCastBarOverlay().onHudRender(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
            }
        });

        event.registerAbove(VanillaGuiLayers.CHAT, ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "rpg_gui"), (guiGraphics, deltaTracker) -> {
            new RPGGuiOverlay().onHudRender(guiGraphics);
        });

        event.registerAbove(VanillaGuiLayers.CHAT, ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "dungeon_stats"), (guiGraphics, deltaTracker) -> {
            DungeonStatsOverlay.render(guiGraphics);
        });

        event.registerAbove(VanillaGuiLayers.CHAT, ResourceLocation.fromNamespaceAndPath(SlashRef.MODID, "status_effects"), (guiGraphics, deltaTracker) -> {
            if (ClientConfigs.CLIENT.STATUS_EFFECTS_OVERLAY_TYPE.get() == ClientConfigs.HorizontalOrVertical.HORIZONTAL) {
                if (ClientConfigs.getConfig().shouldRenderOverlay(OverlayType.EFFECTS_HORIZONTAL)) {
                    EffectsOverlay.render(guiGraphics, true);
                }
            } else {
                if (ClientConfigs.getConfig().shouldRenderOverlay(OverlayType.EFFECTS_VERTICAL)) {
                    EffectsOverlay.render(guiGraphics, false);
                }
            }
        });
    }
}
