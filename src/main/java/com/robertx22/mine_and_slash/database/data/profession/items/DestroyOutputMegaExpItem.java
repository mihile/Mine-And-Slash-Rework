package com.robertx22.mine_and_slash.database.data.profession.items;

import com.robertx22.mine_and_slash.uncommon.IShapedRecipe;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.TooltipUtils;
import com.robertx22.mine_and_slash.vanilla_mc.items.misc.AutoItem;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DestroyOutputMegaExpItem extends AutoItem implements IShapedRecipe {

    String name;

    public DestroyOutputMegaExpItem(String name) {
        super(new Properties());
        this.name = name;

    }
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> list, TooltipFlag pIsAdvanced) {
        list.addAll(TooltipUtils.cutIfTooLong(Chats.DESTROYS_OUTPUT.locName()));
    }

    @Override
    public String locNameForLangFile() {
        return name;
    }

    @Override
    public String GUID() {
        return null;
    }

    @Override
    public ShapedRecipeBuilder getRecipe() {
        return shaped(this)
                .define('C', Items.ANVIL)
                .define('X', Items.IRON_NUGGET)
                .pattern("XXX")
                .pattern("XCX")
                .pattern("XXX")
                .unlockedBy("player_level", trigger());
    }
}

