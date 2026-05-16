package net.neoforged.neoforge.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;

public class NetworkHooks {
    public static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity) {
        return null;
    }
}
