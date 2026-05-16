package net.neoforged.neoforge.client.event;

import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

public class RenderGuiOverlayEvent extends Event {
    private boolean canceled;

    public VanillaGuiOverlay getOverlay() {
        return VanillaGuiOverlay.PLAYER_HEALTH;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
