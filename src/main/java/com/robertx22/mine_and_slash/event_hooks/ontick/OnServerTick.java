package com.robertx22.mine_and_slash.event_hooks.ontick;

import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.capability.player.PlayerData;
import com.robertx22.mine_and_slash.config.forge.compat.CompatConfig;
import com.robertx22.mine_and_slash.database.data.profession.StationPacket;
import com.robertx22.mine_and_slash.database.data.profession.StationSyncData;
import com.robertx22.mine_and_slash.database.data.profession.screen.CraftingStationMenu;
import com.robertx22.mine_and_slash.database.data.stat_compat.StatCompat;
import com.robertx22.mine_and_slash.gui.screens.map.MapSyncData;
import com.robertx22.mine_and_slash.saveclasses.unit.ResourceType;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.effectdatas.EventBuilder;
import com.robertx22.mine_and_slash.uncommon.effectdatas.RestoreResourceEvent;
import com.robertx22.mine_and_slash.uncommon.effectdatas.TenSecondPlayerTickEvent;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.RestoreType;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.LevelUtils;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.WorldUtils;
import com.robertx22.mine_and_slash.vanilla_mc.packets.MapCompletePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class OnServerTick {

    public static AttributeModifier CASTING_SPEED_SLOW = new AttributeModifier(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("mmorpg", "casting_speed_slow"),
            -0.5,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    );

    public static void onEndTick(ServerPlayer player) {
        try {
            if (player == null || player.isDeadOrDying()) {
                return;
            }
            EntityData unitdata = Load.Unit(player);
            PlayerData playerData = Load.player(player);


            if (!(player.level() instanceof ServerLevel sw)) {
                return;
            }

            tickMapState(player, unitdata, sw);

            playerData.spellCastingData.onTimePass(player);
            unitdata.didStatCalcThisTickForPlayer = false;

            int age = player.tickCount;

            tickRecurringPlayerEvents(player, unitdata, playerData, age);
            updateCastingSlow(player, playerData);
            cancelCastIfBlocking(player, playerData);
            tickResourcesAndCharges(player, unitdata, playerData, age);
            syncCraftingStation(player);

            if (age % 20 == 0) {
                tickSecondPlayerEffects(player, unitdata, playerData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Load.player(player).playerDataSync.onTickTrySync(player);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void tickMapState(ServerPlayer player, EntityData unitdata, ServerLevel sw) {
        WorldUtils.ifMapData(sw, player.blockPosition()).ifPresent(map -> {

            if (player.tickCount % 20 == 0) {
                if (map != null && map.map != null) {
                    if (!map.map.getStatReq().meetsReq(map.map.lvl, unitdata)) {
                        player.hurt(player.damageSources().generic(), player.getMaxHealth() * 0.1F);
                    }
                }
            }

            var pro = Load.player(player).prophecy;

            if (!pro.mapid.equals(map.map.uuid)) {
                pro.clearIfNewMap(map.map);
            }

            if (player.tickCount % (20 * 10) == 0) {
                Packets.sendToClient(player, new MapCompletePacket(new MapSyncData(map)));
            }

        });
    }

    private static void tickRecurringPlayerEvents(ServerPlayer player, EntityData unitdata, PlayerData playerData, int age) {
        if (age % 20 == 0) {
            StatCompat.onTick(player);
        }
        if (age % 200 == 0) {
            TenSecondPlayerTickEvent event = new TenSecondPlayerTickEvent(player, player);
            event.Activate();
        }

        if (age % (20 * 10) == 0) {
            unitdata.setEquipsChanged();
        }

        if (age % (20 * 3) == 0) {
            playerData.playerDataSync.setDirty();
        }

        if (age % (20 * 10) == 0) {
            playerData.miscInfo.area_lvl = LevelUtils.determineLevel(null, player.level(), player.blockPosition(), player, false).getLevel();
        }
    }

    private static void updateCastingSlow(ServerPlayer player, PlayerData playerData) {
        AttributeInstance atri = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (atri == null) {
            return;
        }
        if (playerData.spellCastingData.isCasting() && playerData.spellCastingData.castTickLeft > 0) {
            if (!atri.hasModifier(CASTING_SPEED_SLOW.id()) && playerData.spellCastingData.getSpellBeingCast() != null && playerData.spellCastingData.getSpellBeingCast().config.slows_when_casting) {
                atri.addTransientModifier(CASTING_SPEED_SLOW);
            }
        } else if (atri.hasModifier(CASTING_SPEED_SLOW.id())) {
            atri.removeModifier(CASTING_SPEED_SLOW.id());
        }
    }

    private static void cancelCastIfBlocking(ServerPlayer player, PlayerData playerData) {
        if (player.isBlocking() && playerData.spellCastingData.isCasting()) {
            playerData.spellCastingData.cancelCast(player);
        }
    }

    private static void tickResourcesAndCharges(ServerPlayer player, EntityData unitdata, PlayerData playerData, int age) {
        if (age % 5 == 0) {
            var tickrate = 5;

            unitdata.getResources().onTickBlock(player, tickrate);

            playerData.spellCastingData.charges.onTicks(player, 5);
        }
    }

    private static void syncCraftingStation(ServerPlayer player) {
        if (player.containerMenu instanceof CraftingStationMenu men && player.tickCount % 5 == 0) {
            men.be.onTickWhenPlayerWatching(player);
            Packets.sendToClient(player, new StationPacket(new StationSyncData(men.be)));
        }
    }

    private static void tickSecondPlayerEffects(ServerPlayer player, EntityData unitdata, PlayerData playerData) {
        playerData.buff.onTick(player, 20);

        playerData.favor.onSecond(player);

        applyEnergyPenalty(player, unitdata);
        restoreResourcesOnSecond(player, unitdata);
    }

    private static void applyEnergyPenalty(ServerPlayer player, EntityData unitdata) {
        if (unitdata.getResources().getEnergy() < unitdata.getUnit().energyData().get() / 10) {
            if (CompatConfig.get().energyPenalty()) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 3, 2));
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * 3, 2));
            }
        }
    }

    private static void restoreResourcesOnSecond(ServerPlayer player, EntityData unitdata) {
        if (player.getY() < (double) (player.level().getMinBuildHeight() - 64)) {
            return;
        }

        if (!ResourceType.mana.isFull(unitdata)) {
            RestoreResourceEvent mana = EventBuilder.ofRestore(player, player, ResourceType.mana, RestoreType.regen, 0)
                    .build();
            mana.Activate();
        }

        restoreEnergyOrStopBlocking(player, unitdata);

        if (!ResourceType.magic_shield.isFull(unitdata)) {
            RestoreResourceEvent msevent = EventBuilder.ofRestore(player, player, ResourceType.magic_shield, RestoreType.regen, 0)
                    .build();
            msevent.Activate();
        }

        boolean canHeal = player.getFoodData().getFoodLevel() >= 1;

        if (canHeal) {
            RestoreResourceEvent hpevent = EventBuilder.ofRestore(player, player, ResourceType.health, RestoreType.regen, 0)
                    .build();
            hpevent.Activate();
        }
    }

    private static void restoreEnergyOrStopBlocking(ServerPlayer player, EntityData unitdata) {
        if (!player.isBlocking()) {
            if (!ResourceType.energy.isFull(unitdata)) {
                RestoreResourceEvent energy = EventBuilder.ofRestore(player, player, ResourceType.energy, RestoreType.regen, 0)
                        .build();
                energy.Activate();
            }
            return;
        }
        if (unitdata.getResources().getEnergy() < 1) {
            player.getCooldowns().addCooldown(player.getOffhandItem().getItem(), 20 * 3);
            player.getCooldowns().addCooldown(player.getMainHandItem().getItem(), 20 * 3);
            player.stopUsingItem();
        }
    }


}
