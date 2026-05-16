package net.neoforged.neoforge.client.event;

import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

public class RegisterGuiOverlaysEvent extends Event {
    public void registerAbove(Object anchor, String id, IGuiOverlay overlay) {
    }
}
