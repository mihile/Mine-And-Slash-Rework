package com.robertx22.mine_and_slash.mmorpg.event_registers;

import com.robertx22.mine_and_slash.event_hooks.ontick.OnClientTick;
import com.robertx22.mine_and_slash.event_hooks.player.OnKeyPress;
import com.robertx22.mine_and_slash.mmorpg.ForgeEvents;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class Client {

    public static void register() {

        // todo
        // InputEvent.Key.class

        ForgeEvents.registerForgeEvent(ClientTickEvent.Post.class, event -> {
            OnClientTick.onEndTick(Minecraft.getInstance());
            OnKeyPress.onEndTick(Minecraft.getInstance());

        });

    }
}
