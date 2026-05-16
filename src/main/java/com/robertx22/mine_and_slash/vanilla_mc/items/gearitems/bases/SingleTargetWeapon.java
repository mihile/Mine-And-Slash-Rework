package com.robertx22.mine_and_slash.vanilla_mc.items.gearitems.bases;

import com.robertx22.mine_and_slash.uncommon.interfaces.IAutoLocName;
import com.robertx22.library_of_exile.vanilla_util.main.VanillaUTIL;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

public abstract class SingleTargetWeapon extends Item implements IAutoLocName {

    public SingleTargetWeapon(Tiers mat, Properties settings, String locname) {

        super(settings);
        this.locname = locname;

    }

    String locname;
    public float attackSpeed = -2.4F;

    @Override
    public boolean hurtEnemy(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
        p_77644_1_.hurtAndBreak(1, p_77644_3_, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public final String locNameForLangFile() {
        return locname;
    }

    @Override
    public AutoLocGroup locNameGroup() {
        return AutoLocGroup.Gear_Items;
    }

    @Override
    public String locNameLangFileGUID() {
        return VanillaUTIL.REGISTRY.items().getKey(this)
                .toString();
    }


    @Override
    public String GUID() {
        return "";
    }

}
