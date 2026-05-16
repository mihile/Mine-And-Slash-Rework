package com.robertx22.addons.orbs_of_crafting.currency.base;

import com.robertx22.addons.orbs_of_crafting.currency.IItemAsCurrency;
import com.robertx22.library_of_exile.util.ExplainedResult;
import com.robertx22.library_of_exile.vanilla_util.main.VanillaUTIL;
import com.robertx22.mine_and_slash.aoe_data.datapacks.models.IAutoModel;
import com.robertx22.mine_and_slash.aoe_data.datapacks.models.ItemModelManager;
import com.robertx22.mine_and_slash.database.data.rarities.GearRarity;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.gui.texts.textblocks.WorksOnBlock;
import com.robertx22.mine_and_slash.uncommon.interfaces.IAutoLocName;
import com.robertx22.mine_and_slash.uncommon.interfaces.IRarityItem;
import com.robertx22.orbs_of_crafting.misc.LocReqContext;
import com.robertx22.orbs_of_crafting.misc.ResultItem;
import com.robertx22.orbs_of_crafting.register.ExileCurrency;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ExileCurrencyItem extends Item implements IAutoLocName, IAutoModel, IRarityItem, IItemAsCurrency {

    ExileCurrency effect;

    String name;

    public ExileCurrencyItem(ExileCurrency effect) {
        super(new Item.Properties());
        this.effect = effect;
        this.name = effect.locname;
    }


    @Override
    public void generateModel(ItemModelManager manager) {
        manager.generated(this);
    }

    @Override
    public AutoLocGroup locNameGroup() {
        return AutoLocGroup.Currency_Items;
    }

    @Override
    public String locNameLangFileGUID() {
        return VanillaUTIL.REGISTRY.items().getKey(this).toString();
    }


    @Override
    public String locNameForLangFile() {
        return name;
    }

    @Override
    public String GUID() {
        return "";
    }


    @Override
    public GearRarity getItemRarity(ItemStack stack) {
        try {
            return ExileDB.GearRarities().get(ExileCurrency.get(stack).get().rar);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CodeCurrency currencyEffect(ItemStack stack) {
        ExileCurrency currency = ExileCurrency.get(stack).orElse(effect);
        return new CodeCurrency() {
            @Override
            public WorksOnBlock.ItemType usedOn() {
                return WorksOnBlock.ItemType.ANY;
            }

            @Override
            public void internalModifyMethod(LocReqContext ctx) {
            }

            @Override
            public ExplainedResult canItemBeModified(LocReqContext context) {
                return currency.canItemBeModified(context);
            }

            @Override
            public void addToTooltip(List<Component> tooltip) {
                tooltip.addAll(currency.getTooltip());
            }

            @Override
            public ResultItem modifyItem(LocReqContext context) {
                return currency.modifyItem(context);
            }

            @Override
            public String locNameForLangFile() {
                return currency.locname;
            }

            @Override
            public String locDescForLangFile() {
                return "";
            }

            @Override
            public String GUID() {
                return currency.GUID();
            }

            @Override
            public int Weight() {
                return currency.Weight();
            }
        };
    }
}
