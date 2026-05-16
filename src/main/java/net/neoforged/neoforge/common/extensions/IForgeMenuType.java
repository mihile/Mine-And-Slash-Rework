package net.neoforged.neoforge.common.extensions;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class IForgeMenuType {
    @FunctionalInterface
    public interface Factory<T extends AbstractContainerMenu> {
        T create(int id, Inventory inventory, RegistryFriendlyByteBuf buffer);
    }

    public static <T extends AbstractContainerMenu> MenuType<T> create(Factory<T> factory) {
        return new MenuType<>((id, inventory) -> factory.create(id, inventory, null), FeatureFlags.VANILLA_SET);
    }
}
