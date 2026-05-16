package com.robertx22.mine_and_slash.mmorpg.init;

import com.mojang.datafixers.util.Either;
import com.robertx22.addons.orbs_of_crafting.currency.reworked.OrbAddonClientInit;
import com.robertx22.dungeon_realm.main.DungeonMain;
import com.robertx22.mine_and_slash.a_libraries.dmg_number_particle.DamageParticle;
import com.robertx22.mine_and_slash.a_libraries.dmg_number_particle.DamageParticleRenderer;
import com.robertx22.mine_and_slash.a_libraries.player_animations.PlayerAnimations;
import com.robertx22.mine_and_slash.capability.player.container.BackpackQuickLootButton;
import com.robertx22.mine_and_slash.config.forge.ClientConfigs;
import com.robertx22.mine_and_slash.config.forge.overlay.OverlayPresets;
import com.robertx22.mine_and_slash.gui.SocketTooltip;
import com.robertx22.mine_and_slash.gui.overlays.GuiPosition;
import com.robertx22.mine_and_slash.mmorpg.ForgeEvents;
import com.robertx22.mine_and_slash.mmorpg.event_registers.Client;
import com.robertx22.mine_and_slash.mmorpg.event_registers.GuiOverlays;
import com.robertx22.mine_and_slash.mmorpg.registers.client.ClientSetup;
import com.robertx22.mine_and_slash.saveclasses.gearitem.gear_bases.ModRange;
import com.robertx22.mine_and_slash.saveclasses.gearitem.gear_bases.StatRangeInfo;
import com.robertx22.mine_and_slash.saveclasses.gearitem.gear_parts.SocketData;
import com.robertx22.mine_and_slash.saveclasses.item_classes.GearItemData;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.ClientOnly;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientInit {

    public static void onInitializeClient(final FMLClientSetupEvent event) {

        ForgeEvents.registerForgeEvent(ScreenEvent.Init.Post.class, x -> {
            BackpackQuickLootButton.addLootButton(x);
        });

        OrbAddonClientInit.init();

        OverlayPresets.init();

        PlayerAnimations.initClient();

        AtomicInteger sounds = new AtomicInteger();

        // todo
        // experimental fix for massive 1 min lag after joining a map. No clue what causes it
        ForgeEvents.registerForgeEvent(PlaySoundEvent.class, x -> {
            var p = ClientOnly.getPlayer();
            if (p != null && p.tickCount < (20 * 5)) {
                if (p.level().dimension().location().equals(DungeonMain.DIMENSION_KEY)) {
                    //Minecraft.getInstance().player.sendSystemMessage(Component.literal("Sounds blocked: " + sounds + " - " + x.getName()));
                    sounds.getAndIncrement();
                    x.setSound(null); // forge wtf.. not cancellable but set nullable?
                }
            } else {
                sounds.set(0);
            }
        });


        var todisable = Arrays.asList(
                VanillaGuiLayers.ARMOR_LEVEL,
                VanillaGuiLayers.VEHICLE_HEALTH,
                VanillaGuiLayers.PLAYER_HEALTH
        );

        ForgeEvents.registerForgeEvent(RenderGuiLayerEvent.Pre.class, x -> {
            if (ClientConfigs.getConfig().GUI_POSITION.get() == GuiPosition.OVER_VANILLA) {
                if (todisable.stream().anyMatch(e -> e.equals(x.getName()))) {
                    x.setCanceled(true);
                }
            }

        });


        ForgeEvents.registerForgeEvent(RenderTooltipEvent.GatherComponents.class, x -> {

            try {
                GearItemData data = StackSaving.GEARS.loadFrom(x.getItemStack());
                if (data != null) {

                    List<SocketData> gems = new ArrayList<>();

                    for (SocketData socket : data.sockets.getSocketed()) {
                        gems.add(socket);
                    }


                    int e = 0;
                    if (!gems.isEmpty()) {

                        List<Either<FormattedText, TooltipComponent>> list = x.getTooltipElements();
                        for (int i = 0; i < list.size(); i++) {
                            Optional<FormattedText> o = list.get(i).left();
                            if (o.isPresent() && o.get() instanceof Component comp && comp.getContents() instanceof LiteralContents tc) {
                                if (tc.text().contains("[SOCKET_PLACEHOLDER]")) {
                                    if (e < gems.size()) {
                                        if (!new StatRangeInfo(ModRange.hide()).shouldShowDescriptions()) {
                                            list.set(i, Either.right(new SocketTooltip.SocketComponent(x.getItemStack(), Collections.singletonList(gems.get(e)))));
                                        } else {
                                            list.set(i, Either.right(new SocketTooltip.SocketComponent(x.getItemStack(), Collections.singletonList(gems.get(e)))));
                                            int finalI = i;
                                            list.get(i + 1).left().ifPresent(formattedText -> {
                                                if (formattedText instanceof Component && formattedText.getString().contains("[SOCKET_PLACEHOLDER]")) {
                                                    String replaced = formattedText.getString().replace("[SOCKET_PLACEHOLDER]", "");
                                                    MutableComponent desc = Component.literal(replaced).withStyle(ChatFormatting.BLUE);
                                                    list.set(finalI + 1, Either.left(desc));
                                                }
                                            });
                                        }
                                    }
                                    e++;

                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        ForgeEvents.registerForgeEvent(net.neoforged.neoforge.client.event.RenderLevelStageEvent.class, e -> {
            if (e.getStage() == net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
                if (ClientConfigs.getConfig().ENABLE_FLOATING_DMG.get().getReal()) {
                    Minecraft mc = Minecraft.getInstance();
                    com.mojang.blaze3d.vertex.PoseStack poseStack = e.getPoseStack(); // 1.21.1 올바른 이벤트 매트릭스
                    net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
                    DamageParticleRenderer.renderParticles(poseStack, bufferSource, e.getCamera());
                    bufferSource.endBatch(); // 렌더링 Flush
                }
            }
        });

        ForgeEvents.registerForgeEvent(net.neoforged.neoforge.client.event.RenderLivingEvent.Post.class, x -> {
            // 데미지 렌더링 코드를 RenderLevelStageEvent로 완전히 이전함
        });

        ForgeEvents.registerForgeEvent(net.neoforged.neoforge.client.event.ClientTickEvent.Post.class, x -> {
            for (DamageParticle p : DamageParticleRenderer.PARTICLES) {
                p.tick();
            }
            DamageParticleRenderer.PARTICLES.removeIf(e -> e.age > 50);
        });

        // 클라이언트 전용: SyncPlayerCapToClient 패킷 수신 시 capid → Attachment 역직렬화 등록
        // PlayerData.init()에서 서버 스레드에 접근하지 않도록 여기서만 등록합니다.
        com.robertx22.library_of_exile.packets.SyncPlayerCapToClient.ATTACHMENT_LOOKUP.putIfAbsent(
                "rpg_player_data",
                p -> com.robertx22.mine_and_slash.uncommon.datasaving.Load.player(p)
        );

        ClientSetup.setup();
        Client.register();


    }
}
