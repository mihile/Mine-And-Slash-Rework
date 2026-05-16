package com.robertx22.mine_and_slash.uncommon.utilityclasses;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;

public class DataSaverCheckUtil {
    public static boolean checkForDataSaver(String saverGUID, ItemStack itemStack) {
        return itemStack.has(DataComponents.CUSTOM_DATA) && !itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString(saverGUID).isEmpty();

    }
}
