package com.robertx22.mine_and_slash.event_hooks.damage_hooks.reworked;

import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.mine_and_slash.event_hooks.damage_hooks.OnNonPlayerDamageEntityEvent;
import com.robertx22.mine_and_slash.event_hooks.damage_hooks.OnPlayerDamageEntityEvent;
import com.robertx22.mine_and_slash.mixin_ducks.DamageSourceDuck;
import com.robertx22.mine_and_slash.mmorpg.ForgeEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class NewDamageMain {

    public static void init() {
        ForgeEvents.registerForgeEvent(LivingIncomingDamageEvent.class, event -> {
            var dmgEvent = new ExileEvents.OnDamageEntity(event.getSource(), event.getAmount(), event.getEntity());
            ExileEvents.DAMAGE_BEFORE_CALC.callEvents(dmgEvent);

            if (dmgEvent.canceled || dmgEvent.damage <= 0) {
                event.setCanceled(true);
            }
        }, EventPriority.HIGHEST);

        ForgeEvents.registerForgeEvent(LivingDamageEvent.Pre.class, event -> {
            var dmgEvent = new ExileEvents.OnDamageEntity(event.getSource(), event.getNewDamage(), event.getEntity());

            var duck = (DamageSourceDuck) event.getSource();
            if (event.getSource().is(DamageTypes.PLAYER_ATTACK) && !duck.hasMnsDamageOverride()) {
                ExileEvents.DAMAGE_BEFORE_CALC.callEvents(dmgEvent);
            }

            ExileEvents.DAMAGE_AFTER_CALC.callEvents(dmgEvent);

            event.setNewDamage(dmgEvent.canceled ? 0 : dmgEvent.damage);
        }, EventPriority.LOWEST);

        ExileEvents.DAMAGE_BEFORE_CALC.register(new OnNonPlayerDamageEntityEvent());
        ExileEvents.DAMAGE_BEFORE_CALC.register(new OnPlayerDamageEntityEvent());

        ExileEvents.DAMAGE_BEFORE_CALC.register(new EventConsumer<>() {
            @Override
            public void accept(ExileEvents.OnDamageEntity event) {
                if (event.source != null) {
                    var duck = (DamageSourceDuck) event.source;
                    duck.setOriginalHP(event.mob.getHealth());
                }
            }
        });

        ExileEvents.DAMAGE_AFTER_CALC.register(new EventConsumer<>() {
            @Override
            public int callOrder() {
                return 100;
            }

            @Override
            public void accept(ExileEvents.OnDamageEntity event) {

                // this is cancelled by the time it hits here, probably..
            }
        });

        // uh to fix melee attacks not counting correct for recorded dmg..
        ExileEvents.DAMAGE_AFTER_CALC.register(new EventConsumer<>() {
            @Override
            public int callOrder() {
                return 9;
            }

            @Override
            public void accept(ExileEvents.OnDamageEntity event) {
                if (event.source != null) {
                    var duck = (DamageSourceDuck) event.source;
                    duck.tryOverrideDmgWithMns(event);
                }
            }
        });
        // todo this isnt last sometimes, and other mods might modify it..
        ExileEvents.DAMAGE_AFTER_CALC.register(new EventConsumer<>() {
            @Override
            public int callOrder() {
                return 10000;
            }

            @Override
            public void accept(ExileEvents.OnDamageEntity event) {
                if (event.source != null) {
                    var duck = (DamageSourceDuck) event.source;
                    duck.tryOverrideDmgWithMns(event);
                }
            }
        });
    }
}
