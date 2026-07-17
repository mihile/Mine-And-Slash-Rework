package com.robertx22.mine_and_slash.mmorpg.init;

import com.robertx22.mine_and_slash.a_libraries.neat.NeatForgeConfig;
import com.robertx22.mine_and_slash.config.forge.ClientConfigs;
import com.robertx22.mine_and_slash.gui.SocketTooltip;
import com.robertx22.mine_and_slash.mmorpg.ForgeEvents;
import com.robertx22.mine_and_slash.mmorpg.event_registers.GuiOverlays;
import com.robertx22.mine_and_slash.mmorpg.registers.client.ContainerGuiRegisters;
import com.robertx22.mine_and_slash.mmorpg.registers.client.KeybindsRegister;
import com.robertx22.mine_and_slash.mmorpg.registers.client.RenderRegister;
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashAttachments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import java.util.function.Consumer;

public class ClientRegistration {

    public static void register(IEventBus bus, ModContainer modContainer) {
        ForgeEvents.registerForgeEvent(RegisterClientTooltipComponentFactoriesEvent.class, x -> {
            x.register(SocketTooltip.SocketComponent.class, SocketTooltip::new);
        });

        NeatForgeConfig.register(modContainer);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfigs.clientSpec, NeatForgeConfig.defaultConfigName(ModConfig.Type.CLIENT, "mine_and_slash"));
        bus.addListener(ClientInit::onInitializeClient);
        bus.addListener(ContainerGuiRegisters::reg);

        ForgeEvents.registerForgeEvent(RegisterGuiLayersEvent.class, GuiOverlays::registerOverlay);

        ForgeEvents.registerForgeEvent(RegisterKeyMappingsEvent.class, x -> {
            KeybindsRegister.register(x);
        });

        bus.addListener((Consumer<EntityRenderersEvent.RegisterRenderers>) x -> {
            RenderRegister.regRenders(x);
        });

        // 클라이언트에서 플레이어 로그인 후 Attachment init 보장
        // transient 필드(entity, player 참조)는 직렬화되지 않으므로 수동 주입이 필요합니다.
        ForgeEvents.registerForgeEvent(
                ClientPlayerNetworkEvent.LoggingIn.class,
                event -> {
                    try {
                        var p = event.getPlayer();
                        if (p != null) {
                            p.getData(SlashAttachments.ENTITY_DATA.get()).init(p);
                            p.getData(SlashAttachments.PLAYER_DATA.get()).init(p);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
