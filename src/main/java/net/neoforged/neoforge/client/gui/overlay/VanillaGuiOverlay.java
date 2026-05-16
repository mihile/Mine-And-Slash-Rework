package net.neoforged.neoforge.client.gui.overlay;

import net.minecraft.resources.ResourceLocation;

public enum VanillaGuiOverlay {
    CHAT_PANEL,
    ARMOR_LEVEL,
    MOUNT_HEALTH,
    PLAYER_HEALTH;

    public ResourceLocation id() {
        return ResourceLocation.fromNamespaceAndPath("minecraft", name().toLowerCase());
    }
}
