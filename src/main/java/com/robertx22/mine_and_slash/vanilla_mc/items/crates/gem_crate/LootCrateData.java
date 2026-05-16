package com.robertx22.mine_and_slash.vanilla_mc.items.crates.gem_crate;

import com.robertx22.mine_and_slash.mmorpg.registers.common.items.SlashItems;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import com.robertx22.mine_and_slash.uncommon.enumclasses.LootType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;


public class LootCrateData {


    public LootType type = LootType.Gem;

    public int tier = 1;

    public enum Type {
        GEAR_SOUL,
    }

    public ItemStack createStack() {
        

        ItemStack stack = new ItemStack(SlashItems.LOOT_CRATE.get());
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(type.custommodeldata));

        StackSaving.GEM_CRATE.saveTo(stack, this);
        return stack;
    }

}
