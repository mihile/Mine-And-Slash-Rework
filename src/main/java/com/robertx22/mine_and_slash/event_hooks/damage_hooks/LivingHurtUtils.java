package com.robertx22.mine_and_slash.event_hooks.damage_hooks;

import com.robertx22.mine_and_slash.a_libraries.curios.MyCurioUtils;
import com.robertx22.mine_and_slash.event_hooks.damage_hooks.util.AttackInformation;
import com.robertx22.mine_and_slash.event_hooks.damage_hooks.util.DmgSourceUtils;
import com.robertx22.mine_and_slash.saveclasses.item_classes.GearItemData;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.OnScreenMessageUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LivingHurtUtils {


    public static int getItemDamage(float dmg) {
        return 1; // todo lets reduce the dmg jewelry takes(int) Mth.clamp(dmg / 10F, 1, 4);
    }

    public static void damageCurioItems(LivingEntity en, float dmg) {

        if (en instanceof Player) {

            Player player = (Player) en;

            List<ItemStack> curios = MyCurioUtils.getAllSlots(player);

            if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                curios.forEach(x -> x.hurtAndBreak(getItemDamage(dmg), serverLevel, player, item -> {
                }));
            }

        }
    }

    // this is a bit hard to rework if i dont first change how hp works
    public static void tryAttack(AttackInformation event) {

        LivingEntity target = event.getTargetEntity();


        if (target.level().isClientSide) {
            return;
        }

        if (event.getSource() != null) {
            if (DmgSourceUtils.isMyDmgSource(event.getSource())) {
                return;
            }
            if (event.getSource().getEntity() instanceof LivingEntity) {
                onAttack(event);
            }
        }

    }

    private static void onAttack(AttackInformation data) {

        try {

            if (!data.getTargetEntity().isAlive()) {
                return; // stops attacking dead mobs
            }

            GearItemData weapondata = data.weaponData;

            if (data.getAttackerEntity() instanceof Player p) {

                attackAsPlayer(data, p, weapondata);

            } else { // if its a mob
                data.getAttackerEntityData().mobBasicAttack(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void attackAsPlayer(AttackInformation data, Player p, GearItemData weapondata) {
        if (shouldUseUnarmedAttack(data, weapondata)) {
            Load.Unit(p).unarmedAttack(data);
            return;
        }

        if (!weapondata.canPlayerWear(data.getAttackerEntityData())) {
            OnScreenMessageUtils.sendMessage((ServerPlayer) data.getAttackerEntity(), Component.literal(""), Component.literal("Weapon requirements not met"));
            Load.Unit(p).unarmedAttack(data);
            return;
        }

        if (weapondata != null && weapondata.isWeapon()) {
            if (data.getAttackerEntityData().canUseWeapon(weapondata)) {
                data.getAttackerEntityData().attackWithWeapon(data);
            }
        } else {
            Load.Unit(p).unarmedAttack(data);
        }
    }

    private static boolean shouldUseUnarmedAttack(AttackInformation data, GearItemData weapondata) {
        return weapondata == null || !weapondata.GetBaseGearType().weaponType().damage_validity_check.isValid(data.getSource());
    }

    public static boolean isEnviromentalDmg(DamageSource source) {
        return !(source.getEntity() instanceof LivingEntity);
    }

}
