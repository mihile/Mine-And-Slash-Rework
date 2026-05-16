package com.robertx22.mine_and_slash.uncommon.utilityclasses;

import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.config.forge.compat.CompatConfig;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HealthUtils {


    static AttributeModifier getHeartsAttributeMod(float num) {
        return new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath("mmorpg", "max_health"),
                num,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    public static void addHearts(LivingEntity en) {

        if (CompatConfig.get().healthSystem().usesVanillaHearts()) {

            var curmax = en.getMaxHealth();

            var data = Load.Unit(en);

            int cur = (int) data.getUnit().healthData().get();

            if (data.lastHealth != cur) {
                data.lastHealth = cur;
            }

            var mod = getHeartsAttributeMod(cur);

            var at = en.getAttribute(Attributes.MAX_HEALTH);

            if (en.getAttributes().hasModifier(Attributes.MAX_HEALTH, mod.id())) {
                at.removeModifier(mod.id());
            }
            data.heartsWithoutMnsHealth = (int) en.getMaxHealth();

            at.addPermanentModifier(mod);

            var aftermax = en.getMaxHealth();

            if (aftermax > curmax) {
                float toheal = aftermax - curmax;
                en.heal(toheal); // todo maybe use sethealth here instead
            }
        } else {
            var mod = getHeartsAttributeMod(0);
            var at = en.getAttribute(Attributes.MAX_HEALTH);
            if (en.getAttributes().hasModifier(Attributes.MAX_HEALTH, mod.id())) {
                at.removeModifier(mod.id());
            }
        }
    }

    public static void heal(LivingEntity en, float heal) {
        en.heal(heal);
    }


    public static float realToVanilla(LivingEntity en, float dmg) {
        if (CompatConfig.get().healthSystem().usesVanillaHearts()) {
            return dmg;
        }
        float multi = dmg / getMaxHealth(en);
        float max = en.getMaxHealth();
        float total = multi * max;
        return total;
    }


    public static float getMaxHealth(LivingEntity en) {

        if (CompatConfig.get().healthSystem().usesVanillaHearts()) {
            return en.getMaxHealth();
        }

        EntityData data = Load.Unit(en);

        if (en.level().isClientSide) {
            return data.getSyncedMaxHealth(); // for client, health needs to be synced
        }
        try {
            return data.getUnit().healthData().get();
        } catch (Exception e) {
            return 1;
        }

    }

    public static int getCurrentHealth(LivingEntity entity) {
        if (CompatConfig.get().healthSystem().usesVanillaHearts()) {
            return (int) entity.getHealth();
        }

        float multi = entity.getHealth() / entity.getMaxHealth();
        float max = getMaxHealth(entity);
        return (int) (max * multi);
    }

    public static int getCurrentHealthPlusMagicShield(LivingEntity entity) {
        return (int) (getCurrentHealth(entity) + Load.Unit(entity).getResources().getMagicShield());
    }

    public static int getMaxHealthPlusMagicShield(LivingEntity entity) {
        int num = (int) (getMaxHealth(entity) + Load.Unit(entity).getUnit().magicShieldData().get());
        if (num <= 0) {
            return 1;
        }
        return num;
    }

}
