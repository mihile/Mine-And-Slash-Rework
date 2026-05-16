package com.robertx22.mine_and_slash.uncommon.coins;

import com.robertx22.mine_and_slash.database.data.profession.ICreativeTabTiered;
import com.robertx22.mine_and_slash.gui.texts.ExileTooltips;
import com.robertx22.mine_and_slash.gui.texts.textblocks.LeveledItemBlock;
import com.robertx22.mine_and_slash.vanilla_mc.items.misc.AutoItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CoinItem extends AutoItem implements ICreativeTabTiered {
    public Coin coin;

    public CoinItem(Coin coin) {
        super(new Properties().stacksTo(64));
        this.coin = coin;
    }

    @Override
    public String locNameForLangFile() {
        return ChatFormatting.LIGHT_PURPLE + coin.locname + " Coin";
    }
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        try {
            // todo need info for each coin type
            //  pTooltipComponents.add(coin.validator.isCoinValid(ClientOnly.getPlayer(), pStack).answer);
            pTooltipComponents.addAll(new ExileTooltips().accept(new LeveledItemBlock(pStack)).release());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String GUID() {
        return "";
    }

    @Override
    public Item getThis() {
        return this;
    }
}

