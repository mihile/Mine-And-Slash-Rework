package com.robertx22.mine_and_slash.event_hooks.damage_hooks;

import com.robertx22.mine_and_slash.database.data.spells.components.Spell;
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.SpellCtx;
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.bases.SpellCastContext;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PetAttackUTIL {

    public static void tryAttack(LivingEntity summon, LivingEntity caster, LivingEntity target) {

        if (caster != null) {

            Spell spell = ExileDB.Spells().get(Load.Unit(summon).summonedPetData.spell);
            System.out.println("Pet Spell: " + (spell != null ? spell.toString() : "null") + ", Caster: " + (caster != null ? caster.getName().getString() : "null"));

            if (spell != null) {
                Spell basic = spell.getConfig().getSummonBasicSpell();
                var ctx = new SpellCastContext(caster, 0, basic);

                // 소환수의 기본 공격은 자원 검사 없이 발동하도록 수정
                boolean cancast = true;

                if (cancast) {
                    basic.spendResources(ctx);
                    basic.attached.onCast(SpellCtx.onCast(caster, ctx.calcData));
                    basic.attached.tryActivate(Spell.DEFAULT_EN_NAME, SpellCtx.onHit(caster, summon, target, ctx.calcData));
                }
            }
        } else {
            summon.kill();
        }
    }
}
