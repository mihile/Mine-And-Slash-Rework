package com.robertx22.mine_and_slash.mmorpg.registers.common;

import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SlashItemTags {

    public static void init() {

    }

    public static TagKey<Item> tagOf(String id) {
        return ItemTags.create(ResourceLocation.parse(SlashRef.MODID + ":" + id));
    }
}
