package net.neoforged.neoforge.event.entity.player;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

public class EntityItemPickupEvent extends Event {
    private final Player player;
    private final ItemEntity item;
    private boolean canceled;

    public EntityItemPickupEvent(Player player, ItemEntity item) {
        this.player = player;
        this.item = item;
    }

    public Player getEntity() {
        return player;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
