package com.robertx22.mine_and_slash.database.data.profession;

import com.robertx22.mine_and_slash.uncommon.utilityclasses.LevelUtils;
import com.robertx22.temp.SkillItemTier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class LeveledItem {


    // todo remove levels, not used

    public static int getLevel(ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            return LevelUtils.tierToLevel(getTier(stack).tier).getMinLevel();
        }
        return 0;
    }

    public static int getTierNum(ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("tier");
        }
        return 0;
    }

    public static SkillItemTier getTier(ItemStack stack) {
        return SkillItemTier.of(getTierNum(stack));
    }

    public static void setTier(ItemStack stack, int lvl) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> tag.putInt("tier", lvl)));
    }
}
