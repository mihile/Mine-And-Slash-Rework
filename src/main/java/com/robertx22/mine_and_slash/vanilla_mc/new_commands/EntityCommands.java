package com.robertx22.mine_and_slash.vanilla_mc.new_commands;

import com.mojang.brigadier.CommandDispatcher;
import com.robertx22.library_of_exile.command_wrapper.*;
import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.database.data.rarities.MobRarity;
import com.robertx22.mine_and_slash.database.registry.ExileRegistryTypes;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.vanilla_mc.commands.CommandRefs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;

public class EntityCommands {

    public static void init(CommandDispatcher dis) {

        CommandBuilder.of(CommandRefs.ID, dis, x -> {
            var entityArg = new ResourceLocationWrapper("entity");
            var rarArg = new RegistryWrapper<MobRarity>(ExileRegistryTypes.MOB_RARITY);
            var lvlArg = new IntWrapper("level");

            x.addLiteral("spawn", PermWrapper.OP);
            x.addArg(entityArg);
            x.addArg(rarArg);
            x.addArg(lvlArg);

            x.action(e -> {
                try {
                    var p = e.getSource().getPlayerOrException();
                    var world = p.serverLevel();
                    var entityLoc = entityArg.get(e);
                    var rar = rarArg.get(e);
                    var lvl = lvlArg.get(e);

                    var type = BuiltInRegistries.ENTITY_TYPE.get(entityLoc);
                    Entity entity = type.spawn(world, p.blockPosition(), MobSpawnType.COMMAND);
                    if (entity instanceof LivingEntity living) {
                        EntityData data = EntityData.get(living);
                        data.setRarity(rar);
                        data.setLevel(lvl);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }, "Spawns an entity with specific rarity and level.");


        CommandBuilder.of(CommandRefs.ID, dis, x -> {
            EntityWrapper enarg = new EntityWrapper();
            IntWrapper intarg = new IntWrapper("level");

            x.addLiteral("set", PermWrapper.OP);
            x.addLiteral("level", PermWrapper.OP);

            x.addArg(enarg);
            x.addArg(intarg);

            x.action(e -> {
                var en = enarg.get(e);
                var num = intarg.get(e);

                EntityData data = Load.Unit(en);
                data.setLevel(num);
            });
        }, "Sets Mine and Slash level of entity.");

        CommandBuilder.of(CommandRefs.ID, dis, x -> {
            EntityWrapper enarg = new EntityWrapper();
            var strarg = new RegistryWrapper<MobRarity>(ExileRegistryTypes.MOB_RARITY);

            x.addLiteral("set", PermWrapper.OP);
            x.addLiteral("rarity", PermWrapper.OP);

            x.addArg(enarg);
            x.addArg(strarg);

            x.action(e -> {
                var en = enarg.get(e);
                var rar = strarg.get(e);
                EntityData data = Load.Unit(en);
                data.setRarity(rar);
            });
        }, "Sets the Mine and Slash rarity of an entity.");

    }
}
